package vrt.inpost.qr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.lang.ref.WeakReference;

public class QrDisplayActivity extends SwipeDismissBaseActivity {

    private ImageView _imageView;
    private ProgressBar _loadProgress;
    private final Activity _context=this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_display);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        _imageView = findViewById(R.id.imageView);
        startQrGenerator();
    }

    private void startQrGenerator() {
        final Intent intent = getIntent();

        try
        {
            final String sms = intent.getStringExtra("SMS");
            if(null==sms) this.finish();
            _imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Notify.Info(_context,sms, 5000);
                }
            });
            _loadProgress = findViewById(R.id.loading_spinner);
            new GenerateQrAsync(new WeakReference<>(this)).execute(sms);

        }
        catch (NullPointerException ex)
        {
            showLoadIndicator(false);
            this.finish();
        }
    }

    void setQrBitmap(Bitmap bitmap) {
        _imageView.setImageBitmap(bitmap);
    }

    void showLoadIndicator(boolean show) {
        if(!show) _loadProgress.setVisibility(View.GONE);
        else _loadProgress.setVisibility(View.VISIBLE);
    }
}
