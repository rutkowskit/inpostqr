package vrt.inpost.qr;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InpostHelper {

    private final static String regex = ".*?(?:Kod|kodu)\\s*odbioru.*?(\\d{6})\\b.*";
    private static final Pattern sPattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE |Pattern.MULTILINE |Pattern.DOTALL);

    static String getReceptionCode(SmsData sms)
    {
        return null==sms
                ? null
                : getReceptionCode(sms.Body);
    }

    private static String getReceptionCode(String smsBody)
    {
        if(null==smsBody) return null;
        final Matcher m = sPattern.matcher(smsBody);
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
