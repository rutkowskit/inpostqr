package vrt.inpost.qr;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class QrDisplayActivity extends AppCompatActivity {

    private ImageView imageView;
    private ProgressBar _loadProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr_display);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        imageView = findViewById(R.id.imageView);
        ActionBar actionBar = getActionBar();
        if(null!=actionBar)
            actionBar.setDisplayHomeAsUpEnabled(true);

        Intent i = getIntent();
        try
        {
            String sms = i.getExtras().getString("SMS");
            if(null==sms) this.finish();
            setTitle(String.format("QR text: %s", sms));
            _loadProgress = findViewById(R.id.loading_spinner);
            new GenerateQrAsync(this).execute(sms);
        }
        catch (Exception ex)
        {
            this.finish();
        }
    }
    public void setQrBitmap(Bitmap bitmap)
    {
        imageView.setImageBitmap(bitmap);
    }

    public void showLoadIndicator(boolean show) {
        if(!show) _loadProgress.setVisibility(View.GONE);
        else _loadProgress.setVisibility(View.VISIBLE);
    }
}
