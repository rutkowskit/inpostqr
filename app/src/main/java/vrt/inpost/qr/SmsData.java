package vrt.inpost.qr;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings({"SpellCheckingInspection", "unused"})
class SmsData implements Serializable {
    String Id;
    String Sender;
    Date DateSent;
    int SimSlot;
    String SimImsi;
    String Subject;
    String Body;
    String ReceptionCode;
}
