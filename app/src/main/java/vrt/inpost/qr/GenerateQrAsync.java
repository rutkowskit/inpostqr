package vrt.inpost.qr;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;

import java.lang.ref.WeakReference;

class GenerateQrAsync extends AsyncTask<String, Void, Bitmap> {

    final private WeakReference<QrDisplayActivity> _context;
    private int _size;

    GenerateQrAsync(WeakReference<QrDisplayActivity> activity) {
        _context = activity;
    }

    protected void onPreExecute() {
        QrDisplayActivity activity = _context.get();
        if(null==activity || activity.isFinishing()) return;
        activity.showLoadIndicator(true);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        _size = Math.max(Math.min(height,width)-40, 500);
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        if(params.length==0) return null;
        try {
            return GenerateQR.generate(params[0],_size);
        }
        catch(Exception ex)
        {
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        QrDisplayActivity activity = _context.get();
        if(null==activity || activity.isFinishing()) return;
        activity.showLoadIndicator(false);
        activity.setQrBitmap(result);
    }
}
