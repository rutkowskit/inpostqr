package vrt.inpost.qr;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_SMS=6789;

    private SharedPreferences _pref;
    private List<SmsData> _smsData;
    private final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _pref = getApplicationContext().getSharedPreferences(getString(R.string.prefName), 0); // 0 - for private mode
        setTitle(R.string.appTitle);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_SMS)) {
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_SMS},
                        MY_PERMISSIONS_REQUEST_READ_SMS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        else {
            updateListView(getSmsList());
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_refresh) {
            updateListView(getSmsList());
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
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_SMS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    updateListView(getSmsList());
                } else {
                    Notify.Error(this,getString(R.string.msgAccessDeniedSmsRead));
                }
                return;
            }
        }
    }

    private void updateListView(List<SmsData> data) {
        final ListView listview = findViewById(R.id.smsListiew);

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
        i.putExtra("SMS", qrCodeText);
        startActivity(i);
    }


    private String onGenerateManual() {

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt_for_reception_data, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setView(promptsView);

        final AutoCompleteTextView phoneField = promptsView.findViewById(R.id.uxPhoneNumberField);
        final EditText codeField = promptsView.findViewById(R.id.uxReceptionCodeField);

        List<String> phones = getPhoneNumberFromPrefs();
        ArrayAdapter<String> adapter =
                new ArrayAdapter(this, android.R.layout.simple_list_item_1,phones);
        phoneField.setAdapter(adapter);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.okLabel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String phone = phoneField.getText().toString();
                                String code = codeField.getText().toString();
                                showQrCode(phone, code);
                            }
                        })
                .setNegativeButton(R.string.cancelLabel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        return null; //alert is async, so return null
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

        final String key = String.format("SIM_%d_%s",sms.SimSlot+1, sms.SimImsi);

        String result = _pref.getString(key, null);
        if(null!=result) return result;

        // get prompts.xml view
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.prompt_for_phone, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setView(promptsView);

        final TextView label = promptsView.findViewById(R.id.uxPhoneNumberLabel);
        final EditText field = promptsView.findViewById(R.id.uxPhoneNumberField);

        label.setText(String.format("Wpisz nr telefonu dla sim w slocie %d", sms.SimSlot+1));

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(R.string.okLabel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                String value = field.getText().toString();
                                SharedPreferences.Editor editor = _pref.edit();
                                editor.putString(key,value );
                                editor.commit();
                                showQrCode(value, sms.ReceptionCode);
                            }
                        })
                .setNegativeButton(R.string.cancelLabel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
        return null; //alert is async, so return null
    }

    private List<SmsData> getSmsList() {
        if(null!=_smsData) return _smsData;
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = getContentResolver()
                .query(uri, null, null, null, null);

        List<SmsData> smsList = new ArrayList<>();
        SmsDataResolver resolver = new SmsDataResolver(cursor);
        SmsData sms;
        Date minDate = addDays(Calendar.getInstance().getTime(),-10);
        while ((sms=resolver.getNext())!=null) {
            if(sms.DateSent.before(minDate)) break; //zbyt starych nie pobieraj
            if(null==sms.ReceptionCode) continue;
            smsList.add(sms);
        }
        resolver.finish();
        _smsData = smsList;
        return smsList;
    }

    public static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
