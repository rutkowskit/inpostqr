package vrt.inpost.qr;

import android.database.Cursor;

import java.util.Calendar;
import java.util.Date;

public class SmsDataResolver {

    private int _idIdx;
    private int _senderIdx;
    private int _dateSentIdx ;
    private int _simSlotIdx;
    private int _simImsiIdx;
    private int _subjectIdx;
    private int _bodyIdx;
    private Cursor _cursor;
    private  boolean _hasNext;

    public SmsDataResolver(Cursor cursor) {
        _idIdx = cursor.getColumnIndex("_id");
        _senderIdx = cursor.getColumnIndex("address");
        _dateSentIdx = cursor.getColumnIndex("date_sent");
        _simSlotIdx = cursor.getColumnIndex("sim_slot");
        _simImsiIdx = cursor.getColumnIndex("sim_imsi");
        _subjectIdx = cursor.getColumnIndex("subject");
        _bodyIdx = cursor.getColumnIndex("body");
        _cursor = cursor;
        _hasNext= _cursor.moveToFirst();
    }

    public SmsData getNext() {
        if(!_hasNext) return null;
        SmsData result = new SmsData();
        result.Id = _cursor.getString(_idIdx);
        result.Subject = _cursor.getString(_subjectIdx);
        result.Body = _cursor.getString(_bodyIdx);
        result.Sender = _cursor.getString(_senderIdx);
        result.DateSent = millisToDate(_cursor.getLong(_dateSentIdx));
        result.SimSlot = _cursor.getInt(_simSlotIdx);
        result.SimImsi = _cursor.getString(_simImsiIdx);
        result.ReceptionCode = InpostHelper.getReceptionCode(result);

        _hasNext=_cursor.moveToNext();
        return result;
    }

    public void finish() {
        if(null==_cursor) return;
        _cursor.close();
    }

    private static Date millisToDate(long currentTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(currentTime);
        return calendar.getTime();
    }
}
