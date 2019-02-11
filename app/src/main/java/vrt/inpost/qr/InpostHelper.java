package vrt.inpost.qr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InpostHelper {

    private static final Pattern sPattern
            = Pattern.compile(".*?Kod odbioru.*?(\\d{6}).*?",Pattern.CASE_INSENSITIVE);

    static String getReceptionCode(SmsData sms)
    {
        return null==sms
                ? null
                : getReceptionCode(sms.Body);
    }

    private static String getReceptionCode(String smsBody)
    {
        if(null==smsBody) return null;
        Matcher m= sPattern.matcher(smsBody);
        if(m.matches()) {
            return  m.group(1);
        }
        return  null;
    }

    static String getQrText(String phoneNumber, String receptionCode)
    {
        if(null==phoneNumber || null==receptionCode) return null;
        return String.format("P|%s|%s",phoneNumber, receptionCode);
    }
}
