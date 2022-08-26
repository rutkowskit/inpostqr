package vrt.inpost.qr;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_SMS=6789;
    private static  final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.READ_SMS,
    };

    private SharedPreferences _prefPhones;
    private SharedPreferences _prefMain;
    private final Activity _context = this;
    private final Locale _locale= new Locale("POLISH","PL","pl");
    private SwipeRefreshLayout _swipe;

    private ViewGroup _rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Context appCtx = getApplicationContext();
        _prefPhones = appCtx.getSharedPreferences(getString(R.string.prefName), 0); // 0 - for private mode
        _prefMain = appCtx.getSharedPreferences(getString(R.string.prefMainName),0);

        _rootView = findViewById(android.R.id.content);
        _swipe = findViewById(R.id.swipeRefresh);
        //setTitle(R.string.appLabel);
        registerSwipeRefresh();
        loadSmsList();
    }

    private void loadSmsList() {
        if(hasRequiredPermissions()) {
            onRefreshSmsList(false);
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS , MY_PERMISSIONS_REQUEST_READ_SMS);
        }
    }

    private boolean hasRequiredPermissions() {
        for (String permission: REQUIRED_PERMISSIONS){
            if(!hasPermission(permission)) {
                return false;
            }
        }
        return true;
    }

    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void registerSwipeRefresh() {
        if (null != _swipe) {
            _swipe.setOnRefreshListener(() -> onRefreshSmsList(true)
            );
        }
    }

    private void onRefreshSmsList(boolean forceRefresh) {
        int maxDays = _prefMain.getInt(getString(R.string.prefMaxDays), 10);
        updateListView(SmsUtils.getSmsList(_context, forceRefresh, maxDays));
        if(null!=_swipe)
            _swipe.setRefreshing(false);
        if(forceRefresh)
            Notify.Info(this, getString(R.string.msgRefreshed));
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            onRefreshSmsList(true);
            return true;
        }
        if (id == R.id.action_closeApp) {
            this.finish();
            return true;
        }
        if (id == R.id.action_settings) {
            Intent i = new Intent(getApplicationContext(), AppPrefsActivity.class);
            startActivity(i);
            return true;
        }
        if(id==R.id.action_generateManual) {
            onGenerateManual();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_SMS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onRefreshSmsList(false);
            } else {
                Notify.Error(this, getString(R.string.msgAccessDeniedSmsRead));
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void updateListView(List<SmsData> data) {
        final ListView listview = findViewById(R.id.smsListView);

        final SmsListAdapter adapter = new SmsListAdapter(this,data);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener((parent, view, position, id) -> {
            SmsData item = (SmsData) parent.getItemAtPosition(position);
            String phoneNumber = getPhoneNumber(item);
            showQrCode(phoneNumber, item.ReceptionCode);
        });
    }

    private void showQrCode(String phoneNumber, String receptionCode) {
        if(phoneNumber==null || null==receptionCode) return;
        if(phoneNumber.length()!=9)
        {
            Notify.Error(this, getString(R.string.msgPhoneNumberTooShort));
            return;
        }
        if(receptionCode.length()!=6)
        {
            Notify.Error(this, getString(R.string.msgReceptionCodeTooShort));
            return;
        }

        String qrCodeText = InpostHelper.getQrText(phoneNumber, receptionCode);

        Intent i = new Intent(getApplicationContext(), QrDisplayActivity.class);
        i.putExtra(getString(R.string.prefSMS), qrCodeText);
        startActivity(i);
    }


    private void onGenerateManual() {

        LayoutInflater li = LayoutInflater.from(_context);
        View promptsView = li.inflate(R.layout.prompt_for_reception_data, _rootView,false);

        final AutoCompleteTextView phoneField = promptsView.findViewById(R.id.uxPhoneNumberField);
        final EditText codeField = promptsView.findViewById(R.id.uxReceptionCodeField);

        List<String> phones = getPhoneNumberFromPrefs();
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,phones);
        phoneField.setAdapter(adapter);
        phoneField.setImeOptions(EditorInfo.IME_ACTION_NEXT);

        createAlertDialog(promptsView, dialog -> {
            String phone = phoneField.getText().toString();
            String code = codeField.getText().toString();
            if(phone.length()!=9) {
                Notify.Error(_context, getString(R.string.msgPhoneNumberTooShort));
                return false;
            }
            if(code.length()!=6) {
                Notify.Error(_context, getString(R.string.msgReceptionCodeTooShort));
                return false;
            }
            showQrCode(phone, code);
            return true;
        }).show();
    }

    private List<String> getPhoneNumberFromPrefs() {

        List<String> result=new ArrayList<>();
        for(Map.Entry<String, ?> entry : _prefPhones.getAll().entrySet()) {
            String key = entry.getKey();
            if(!key.startsWith("SIM")) continue;
            result.add(entry.getValue().toString());
        }
        return result;
    }


    private String getPhoneNumber(final SmsData sms) {
        if(null==sms) return null;

        final String key = String.format(_locale, getString(R.string.formatSimKey),sms.SimSlot+1, sms.SimImsi);

        String result = _prefPhones.getString(key, null);
        if(null!=result) return result;

        LayoutInflater li = LayoutInflater.from(_context);

        View promptsView = li.inflate(R.layout.prompt_for_phone, _rootView,false);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
        alertDialogBuilder.setView(promptsView);

        final EditText phoneField = promptsView.findViewById(R.id.uxPhoneNumberField);
        phoneField.setHint(String.format(_locale,
                getString(R.string.formatEnterPhoneForSimInSlot), sms.SimSlot+1));

        createAlertDialog(promptsView, dialog -> {
            String phone = phoneField.getText().toString();
            if(phone.length()!=9) {
                Notify.Error(_context, getString(R.string.msgPhoneNumberTooShort));
                return false;
            }
            SharedPreferences.Editor editor = _prefPhones.edit();
            editor.putString(key,phone );
            editor.apply();
            showQrCode(phone,sms.ReceptionCode);
            return true;
        }).show();

        return null;
    }

    private AlertDialog createAlertDialog(View promptsView, final OnAlertDialogClickListener okListener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(_context)
                .setView(promptsView)
                .setPositiveButton(R.string.okLabel, null)
                .setNegativeButton(R.string.cancelLabel, null)
                .create();

        alertDialog.setOnShowListener(dialog -> {

            Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            b.setOnClickListener(view -> {
                if(okListener.onOK(dialog))
                    dialog.dismiss();
            });
        });
        return alertDialog;
    }
}
