package es.upm.gsi.jsanchez.smartphoneapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;

/**
 * Created by JesúsManuel on 12/06/2015.
 */
public class AboutActivity extends Activity {
    private ImageView mImageView;
    private ImageView mImageView2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        mImageView = (ImageView) findViewById(R.id.imageView);
        mImageView.setImageResource(R.drawable.logoeva);

        mImageView2 = (ImageView) findViewById(R.id.imageView2);
        mImageView2.setImageResource(R.drawable.logosgsi);
    }
}
