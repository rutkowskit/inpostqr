package vrt.inpost.qr;

import java.io.Serializable;
import java.util.Date;

public class SmsData implements Serializable {
    public String Id;
    public String Sender;
    public Date DateSent;
    public int SimSlot;
    public String SimImsi;
    public String ServiceCenter;
    public String Subject;
    public String Body;
    public String ReceptionCode;

}
