package vrt.inpost.qr;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Objects;

class SmsListAdapter extends ArrayAdapter<SmsData> {

    static class ViewHolder {
        TextView smsIdField;
        TextView recCodeField;
        TextView smsBodyField;
        TextView smsSentDateField;
    }
    // List context
    private final Context context;
    // List values
    private final List<SmsData> smsList;

    SmsListAdapter(Context context, List<SmsData> smsList) {
        super(context, R.layout.activity_main, smsList);
        this.context = context;
        this.smsList = smsList;
    }

    @Override
    @NonNull
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        ViewHolder holder;
        if(convertView==null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = Objects.requireNonNull(inflater).inflate(R.layout.sms_list_row_layout, parent, false);
            holder = new ViewHolder();
            holder.smsIdField = convertView.findViewById(R.id.labelSmsId);
            holder.recCodeField = convertView.findViewById(R.id.label);
            holder.smsSentDateField = convertView.findViewById(R.id.labelSmsDate);
            holder.smsBodyField = convertView.findViewById(R.id.labelSmsBody);
            convertView.setTag(holder);
        }
        else {
            holder= (ViewHolder)convertView.getTag();
        }

        SmsData sms = smsList.get(position);
        holder.smsIdField.setText(sms.Id);
        holder.recCodeField.setText(String.format("Kod odbioru: %s", sms.ReceptionCode));
        holder.smsSentDateField.setText(DateFormat.format("yyyy-MM-dd hh:mm:ss", sms.DateSent));
        holder.smsBodyField.setText(sms.Body);

        return convertView;
    }
}