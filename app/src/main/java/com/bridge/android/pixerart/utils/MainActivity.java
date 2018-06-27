package com.bridge.android.pixerart.utils;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bridge.android.pixerart.R;

public class MainActivity extends AppCompatActivity {
    Thread splashTread;
    ImageView imageView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView)findViewById(R.id.splash);
        //textView = (TextView)findViewById(R.id.tv);
        //textView.setPaintFlags(textView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Animation translate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.translate);
        Animation fadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.alpha);
        imageView.startAnimation(translate);
        //textView.startAnimation(translate);


        splashTread = new Thread() {
            @Override
            public void run() {
                try {
                    int waited = 0;
                    // Splash screen pause time
                    while (waited < 3500) {
                        sleep(100);
                        waited += 100;
                    }
                    Intent intent = new Intent(MainActivity.this,PicsBucket.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    startActivity(intent);
                    MainActivity.this.finish();
                } catch (InterruptedException e) {
                    // do nothing
                } finally {
                    MainActivity.this.finish();
                }

            }
        };
        splashTread.start();

    }
}
