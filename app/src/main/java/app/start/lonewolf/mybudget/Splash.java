package app.start.lonewolf.mybudget;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;

import is.arontibo.library.ElasticDownloadView;

public class Splash extends AppCompatActivity {

    private Thread mSplashThread;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        setContentView(R.layout.activity_splash);


        progressBar = findViewById(R.id.progressSplash);
        //progressBar.setSecondaryProgressTintMode();

        mSplashThread =  new Thread(){
            @Override
            public void run(){
                try {
                    synchronized(this){
                        // Wait given period of time or exit on touch
                        //startAnimation();
                        wait(3000);
                    }
                }
                catch(InterruptedException ex){
                }

                finish();

                // Run next activity
                Intent intent = new Intent(Splash.this, Login.class);

                startActivity(intent);
                finish();
            }
        };


        startAnimation();
    }


    private void startAnimation(){
//         ElasticDownloadView mElasticDownloadView = findViewById(R.id.elastic_download_view);
//
//        mElasticDownloadView.startIntro();
//
//        //Set any progress:
//
//        mElasticDownloadView.setProgress(100);
//
//        //Notify if the download has failed or not:
//
//        mElasticDownloadView.setBackgroundColor(Color.GREEN);
//
//        mElasticDownloadView.success(); //This function moves the cursor to 100 if the progress has not been set already

        //mElasticDownloadView.fail();
        //ProgressBar mProgressBar = (ProgressBar) findViewById(R.id.pb_loading);

        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, 100);
        progressAnimator.setDuration(3000);
        progressAnimator.setInterpolator(new LinearInterpolator());
        progressAnimator.start();
        mSplashThread.start();
    }
}
