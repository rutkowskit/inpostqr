package vrt.inpost.qr;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.DisplayMetrics;

public class GenerateQrAsync extends AsyncTask<String, Void, Bitmap> {

    private QrDisplayActivity _context;
    private int _size;


    public GenerateQrAsync(QrDisplayActivity activity) {
        _context = activity;
    }

    protected void onPreExecute() {
        _context.showLoadIndicator(true);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        _context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        _size = Math.min(height,width)-40;
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
        _context.showLoadIndicator(false);
        _context.setQrBitmap(result);
    }
}
