package com.example.asli.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.asli.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SuppressLint("CustomSplashScreen")
public class SplashScreenActivity extends AppCompatActivity {
    TextView connection, textView;
    Button but_retry;
    LottieAnimationView logoAnimationView, progressBar;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        connection = findViewById(R.id.text_connection_lost);
        but_retry = findViewById(R.id.btn_retry);
        progressBar = findViewById(R.id.lottieProgressBar);
        textView = findViewById(R.id.textView);
        logoAnimationView = findViewById(R.id.logo);

        connection.setVisibility(View.GONE);
        but_retry.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        logoAnimationView.setAnimation(R.raw.logo);
        logoAnimationView.playAnimation();


        executorService.execute(() -> {
            if (!isNetworkAvailable()) {
                runOnUiThread(this::handleNoNetwork);
            } else {
                runOnUiThread(this::startMainActivityWithDelay);
            }
        });

        but_retry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.playAnimation();
            but_retry.setVisibility(View.GONE);
            connection.setVisibility(View.GONE);

            executorService.execute(() -> {
                if (isNetworkAvailable()) {
                    runOnUiThread(() -> {
                        progressBar.playAnimation();
                        startMainActivityWithDelay();
                    });
                } else {
                    runOnUiThread(this::handleRetryFailure);
                }
            });
        });
    }

    private void startMainActivityWithDelay() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            Intent intent = new Intent(SplashScreenActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }, 4000);
    }

    private void handleNoNetwork() {
        connection.setVisibility(View.VISIBLE);
        but_retry.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);
        logoAnimationView.setVisibility(View.GONE);
    }

    private void handleRetryFailure() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            but_retry.setVisibility(View.VISIBLE);
            connection.setVisibility(View.VISIBLE);
        }, 300);
    }
}
