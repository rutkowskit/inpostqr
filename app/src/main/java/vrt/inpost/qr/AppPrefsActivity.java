package vrt.inpost.qr;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Map;

public class AppPrefsActivity extends AppCompatActivity {

    private SharedPreferences _pref;
    private SharedPreferences.Editor _prefEditor;
    final private HashMap<String,View> _prefViews= new HashMap<>();

    private final ViewGroup.LayoutParams _viewLayoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);

    @SuppressLint("CommitPrefEdits")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_prefs);

        _pref = getApplicationContext().getSharedPreferences(getString(R.string.prefName), 0); // 0 - for private mode
        _prefEditor = _pref.edit();
        createOptions();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_save) {
            UpdatePrefs();
            if(_prefEditor.commit()) {
                Notify.Info(this, getString(R.string.msgSaved));
                createOptions();
            }
            else
                Notify.Error(this, getString(R.string.msgSaveError));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void UpdatePrefs() {
        if(_prefViews.isEmpty()) return;
        for(Map.Entry<String, ?> entry : _prefViews.entrySet()) {
            String key = entry.getKey();
            EditText value = (EditText)entry.getValue();
            if(null==key || null==value) continue;
            String strValue = value.getText().toString();
            if(strValue.length()==0) _prefEditor.remove(key);
            else _prefEditor.putString(key,strValue);
        }
    }

    private void createOptions() {
        Map<String,?> prefs =  _pref.getAll();
        _prefViews.clear();
        LinearLayout container =  findViewById(R.id.prefContainer);// new LinearLayout(this);
        container.removeAllViews();
        if(null==prefs || prefs.isEmpty()) return;

        Context ctx = getBaseContext();

        for(Map.Entry<String, ?> entry : prefs.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue().toString();

            TextInputLayout innerContainer = CreateInnerLayout();
            EditText editText = CreateField(ctx,value,key);
            innerContainer.addView(editText);

            _prefViews.put(key,editText);
            container.addView(innerContainer);
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
