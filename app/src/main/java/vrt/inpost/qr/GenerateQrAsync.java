package vrt.inpost.qr;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;

class GenerateQrAsync extends AsyncTask<String, Void, Bitmap> {

    final private WeakReference<QrDisplayActivity> _context;

    GenerateQrAsync(WeakReference<QrDisplayActivity> activity) {
        _context = activity;
    }

    protected void onPreExecute() {
        QrDisplayActivity activity = _context.get();
        if(null==activity || activity.isFinishing()) return;
        activity.showLoadIndicator(true);
    }

    @Override
    protected Bitmap doInBackground(String... params) {

        if(params.length==0) return null;
        try {
            return GenerateQR.generate(params[0],350);
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
