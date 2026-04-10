package vrt.inpost.qr;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class InpostHelper {
    private static final int patternFlags = Pattern.CASE_INSENSITIVE |Pattern.MULTILINE |Pattern.DOTALL;
    private static final Map<String,Pattern> sPatterns = new HashMap<>();
    static {
        sPatterns.put("Paczkomat", Pattern.compile(".*?(?:Kod|kodu)\\s*odbioru.*?(\\d{6})\\b.*", patternFlags));
        sPatterns.put("Appkomat", Pattern.compile(".*?InPost.*?kod:\\s*.*?(\\d{6})\\b.*", patternFlags));
    };

    static String getReceptionCode(SmsData sms)
    {
        return null==sms
                ? null
                : getReceptionCode(sms.Body);
    }

    private static String getReceptionCode(String smsBody)
    {
        if(null==smsBody) return null;
        for (Map.Entry<String, Pattern> entry : sPatterns.entrySet()) {
            Matcher matcher = entry.getValue().matcher(smsBody);
            if (matcher.matches()) {
                return  matcher.group(1);
            }
        }
        return  null;
    }

    static String getQrText(String phoneNumber, String receptionCode)
    {
        if(null==phoneNumber || null==receptionCode) return null;
        return String.format("P|%s|%s",phoneNumber, receptionCode);
    }
}
