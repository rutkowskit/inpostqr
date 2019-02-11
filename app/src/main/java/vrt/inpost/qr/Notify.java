package vrt.inpost.qr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

class Notify {

    static void Info(Activity activity, String msg) {
        Info(activity,msg,3000);
    }

    static void Info(Activity activity, String msg, int duration) {
        if(null==msg || msg.length()==0) return;

        //Context context = activity.getApplicationContext();
        final View rootView = activity.getWindow().getDecorView().getRootView();

        Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG)
                .setDuration(duration)
                .show();

        /*
        Toast toast = Toast.makeText(context, msg,Toast.LENGTH_LONG);
        toast.setDuration(duration);
        toast.show();
        */
    }

    static void Error(Activity activity, String msg) {
        if(null==msg || msg.length()==0) return;
        Context context = activity.getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, msg, duration);
        TextView v = toast.getView().findViewById(android.R.id.message);
        v.setTextColor(Color.RED);

        toast.show();
    }
}
