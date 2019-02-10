package vrt.inpost.qr;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmsReceiver extends BroadcastReceiver {

    private static final Pattern sPattern
                = Pattern.compile(".*?Kod.*?(\\d{6})");

        @Override
        public void onReceive(Context context, Intent intent) {

            if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION.equals(intent.getAction())) {
                for (SmsMessage smsMessage : Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                    String messageBody = smsMessage.getMessageBody();
                    Matcher m= sPattern.matcher(messageBody);
                    if(m.matches()) {
                        String group = sPattern.matcher(messageBody).group(0);
                        String code = m.group(0);
                    }
                }
            }
    }
}
