package vrt.inpost.qr;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
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
    private SharedPreferences _pref;
    private int _maxDaysBeforeNow;
    private final Activity _context = this;
    private final Locale _locale= new Locale("POLISH","PL","pl");

    private ViewGroup _rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _pref = getApplicationContext().getSharedPreferences(getString(R.string.prefName), 0); // 0 - for private mode
        _maxDaysBeforeNow= _pref.getInt("MAX_DAYS", 10);
        _rootView = findViewById(android.R.id.content);

        setTitle(R.string.appTitle);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);
        }
        else {
            updateListView(SmsUtils.getSmsList(_context,false,_maxDaysBeforeNow));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            updateListView(SmsUtils.getSmsList(_context,true,_maxDaysBeforeNow));
            Notify.Info(this, getString(R.string.msgRefreshed));
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
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    updateListView(SmsUtils.getSmsList(_context,false,_maxDaysBeforeNow));
                } else {
                    Notify.Error(this,getString(R.string.msgAccessDeniedSmsRead));
                }
            }
        }
    }

    private void updateListView(List<SmsData> data) {
        final ListView listview = findViewById(R.id.smsListView);

        final SmsListAdapter adapter = new SmsListAdapter(this,data);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                SmsData item = (SmsData) parent.getItemAtPosition(position);
                String phoneNumber = getPhoneNumber(item);
                showQrCode(phoneNumber, item.ReceptionCode);
            }
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

        createAlertDialog(promptsView, new OnAlertDialogClickListener() {
            @Override
            public boolean onOK(DialogInterface dialog) {
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
            }
        }).show();
    }

    private List<String> getPhoneNumberFromPrefs() {

        List<String> result=new ArrayList<>();
        for(Map.Entry<String, ?> entry : _pref.getAll().entrySet()) {
            String key = entry.getKey();
            if(!key.startsWith("SIM")) continue;
            result.add(entry.getValue().toString());
        }
        return result;
    }


    private String getPhoneNumber(final SmsData sms) {

        if(null==sms) return null;

        final String key = String.format(_locale, getString(R.string.formatSimKey),sms.SimSlot+1, sms.SimImsi);

        String result = _pref.getString(key, null);
        if(null!=result) return result;

        LayoutInflater li = LayoutInflater.from(_context);

        View promptsView = li.inflate(R.layout.prompt_for_phone, _rootView,false);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_context);
        alertDialogBuilder.setView(promptsView);

        final EditText phoneField = promptsView.findViewById(R.id.uxPhoneNumberField);
        phoneField.setHint(String.format(_locale,
                getString(R.string.formatEnterPhoneForSimInSlot), sms.SimSlot+1));

        createAlertDialog(promptsView, new OnAlertDialogClickListener() {
            @Override
            public boolean onOK(DialogInterface dialog) {
                String phone = phoneField.getText().toString();
                if(phone.length()!=9) {
                    Notify.Error(_context, getString(R.string.msgPhoneNumberTooShort));
                    return false;
                }
                SharedPreferences.Editor editor = _pref.edit();
                editor.putString(key,phone );
                editor.apply();
                showQrCode(phone,sms.ReceptionCode);
                return true;
            }
        }).show();

        return null;
    }

    private AlertDialog createAlertDialog(View promptsView, final OnAlertDialogClickListener okListener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(_context)
                .setView(promptsView)
                .setPositiveButton(R.string.okLabel, null)
                .setNegativeButton(R.string.cancelLabel, null)
                .create();

        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {

                Button b = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(okListener.onOK(dialog))
                            dialog.dismiss();
                    }
                });
            }
        });
        return alertDialog;
    }
}
