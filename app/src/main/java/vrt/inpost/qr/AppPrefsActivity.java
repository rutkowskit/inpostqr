package vrt.inpost.qr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import com.google.android.material.textfield.TextInputLayout;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class AppPrefsActivity extends SwipeDismissBaseActivity {

    private SharedPreferences _prefMain;
    private SharedPreferences _pref;
    final private int _minDays=5;

    final private HashMap<String,View> _prefViews= new HashMap<>();
    private LinearLayout _prefContainer;
    private final ViewGroup.LayoutParams _viewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
    private SeekBar _maxDaysValueField;

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_prefs);
        _prefContainer =  findViewById(R.id.prefContainer);
        _pref = getApplicationContext().getSharedPreferences(getString(R.string.prefName), 0); // 0 - for private mode
        _prefMain = getApplicationContext().getSharedPreferences(getString(R.string.prefMainName), 0); // 0 - for private mode
        _maxDaysValueField = findViewById(R.id.maxDaysField);
        registerMaxDaysCallbacks();
        createOptions();
    }

    private void registerMaxDaysCallbacks() {
        final TextView seekValue = findViewById(R.id.maxDaysNumeric);

        _maxDaysValueField.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progress = (progress/_minDays)*_minDays ;
                seekValue.setText(String.valueOf(progress+_minDays));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            UpdatePrefs();
            if(UpdatePrefs()) {
                Notify.Info(this, getString(R.string.msgSaved));
                createOptions();
            }
            else
                Notify.Error(this, getString(R.string.msgSaveError));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean UpdatePrefs() {
        if(_prefViews.isEmpty()) return false;

        //aktualizacja ustawien głównych
        SharedPreferences.Editor prefMainEditor = _prefMain.edit();
        int progress = _maxDaysValueField.getProgress();
        progress = (progress/_minDays)*_minDays ;
        prefMainEditor.putInt(getString(R.string.prefMaxDays),progress+_minDays);
        prefMainEditor.apply();

        SharedPreferences.Editor prefEditor = _pref.edit();
        //aktualizacja numerów telefonów dla sim-ow
        for(Map.Entry<String, ?> entry : _prefViews.entrySet()) {
            String key = entry.getKey();
            EditText value = (EditText)entry.getValue();
            if(null==key || null==value) continue;
            String strValue = value.getText().toString();
            if(strValue.length()==0) prefEditor.remove(key);
            else prefEditor.putString(key,strValue);
        }
        return prefEditor.commit();
    }

    private void createOptions() {
        _prefContainer.removeAllViews();
        createMainOptions();
        createPhonesOptions();
    }

    private void createMainOptions() {
        String maxDaysKey = getString(R.string.prefMaxDays);
        if(!_prefMain.contains(maxDaysKey)) {
            _prefMain.edit().putInt(maxDaysKey,10).apply();
        }
        int maxDaysValue = _prefMain.getInt(maxDaysKey,10);
        _maxDaysValueField.setProgress(maxDaysValue-_minDays);
    }

    private void createPhonesOptions() {
        Map<String,?> prefs =  _pref.getAll();
        _prefViews.clear();

        if(null==prefs || prefs.isEmpty()) return;

        Context ctx = getBaseContext();

        for(Map.Entry<String, ?> entry : prefs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();

            TextInputLayout innerContainer = CreateInnerLayout();
            EditText editText = CreateField(ctx,value,key);
            innerContainer.addView(editText);

            _prefViews.put(key,editText);
            _prefContainer.addView(innerContainer);
        }
    }

    private TextInputLayout CreateInnerLayout() {
        TextInputLayout txtLayout = new TextInputLayout(this);
        TextInputLayout.LayoutParams txtPar = new TextInputLayout.LayoutParams(TextInputLayout.LayoutParams.MATCH_PARENT,TextInputLayout.LayoutParams.WRAP_CONTENT);
        txtLayout.setLayoutParams(txtPar);
        return txtLayout;
    }

    private EditText CreateField(Context ctx, String fieldValue, String tag) {
        EditText editText = new EditText(ctx);
        return SetFieldContext(editText,fieldValue,tag);
    }

    private EditText SetFieldContext(@NonNull EditText editText, String fieldValue, String tag) {

        editText.setTag(tag);
        editText.setHint(tag);
        editText.setText(fieldValue);
        editText.setLayoutParams(_viewLayoutParams);
        if(tag.startsWith("SIM")) {
            editText.setInputType(InputType.TYPE_CLASS_PHONE);
            editText.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        }
        return editText;
    }
}
