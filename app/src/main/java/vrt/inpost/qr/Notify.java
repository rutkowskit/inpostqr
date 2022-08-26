package vrt.inpost.qr;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import com.google.android.material.snackbar.Snackbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

class Notify {

    static void Info(Activity activity, String msg) {
        Info(activity,msg,3000);
    }
    static void Info(Activity activity, String msg, int duration) {
        ShowNotifiation(activity,msg,duration, Color.parseColor("#00FF00"));
    }

    static void ShowNotifiation(Activity activity, String msg, int duration, int color) {
        if(null==msg || msg.length()==0) return;

        //Context context = activity.getApplicationContext();
        final View rootView = activity.getWindow().getDecorView().getRootView();

        Snackbar.make(rootView, msg, Snackbar.LENGTH_LONG)
                .setDuration(duration)
                .setTextColor(color)
                .setText(msg)
                .show();
    }

    static void Error(Activity activity, String msg) {
        ShowNotifiation(activity,msg,3000, Color.parseColor("#FF0000"));
    }
}
