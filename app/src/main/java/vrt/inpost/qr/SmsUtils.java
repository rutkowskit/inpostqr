package vrt.inpost.qr;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

class SmsUtils {
    private static List<SmsData> _cache;

    static List<SmsData> getSmsList(Context activity, Boolean forceRefresh, int maxDaysFromNow) {
        if(null!=_cache && !forceRefresh) return _cache;
        Uri uri = Uri.parse("content://sms/inbox");
        Cursor cursor = activity
                .getContentResolver()
                .query(uri, null, null, null, null);

        if(null==cursor) return null;
        List<SmsData> smsList = new ArrayList<>();
        SmsDataResolver resolver = new SmsDataResolver(cursor);
        SmsData sms;
        Date minDate = addDays(Calendar.getInstance().getTime(),-maxDaysFromNow);
        while ((sms=resolver.getNext())!=null) {
            if(sms.DateSent.before(minDate)) break;
            if(null==sms.ReceptionCode) continue;
            smsList.add(sms);
        }
        resolver.finish();
        _cache = smsList;
        return smsList;
    }

    private static Date addDays(Date date, int days) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days);
        return cal.getTime();
    }
}
