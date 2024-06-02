package com.example.asli.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.asli.help.DBHelper;
import com.example.asli.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegis,etPassregis;
    DBHelper dbHelper;
    LottieAnimationView background,progressbar;
    private TextView textConnectionLost;
    private Button but_retry,buttonRegister;
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
        setContentView(R.layout.activity_register);

        dbHelper = new DBHelper(this);

        background = findViewById(R.id.lottieBackground);
        etRegis = findViewById(R.id.et_nicknameRegister);
        etPassregis = findViewById(R.id.et_passwordRegister);
        buttonRegister = findViewById(R.id.but_register);
        progressbar = findViewById(R.id.lottieProgressBar);
        textConnectionLost = findViewById(R.id.text_connection_lost);
        but_retry = findViewById(R.id.btn_retry);

        buttonRegister.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                String user = etRegis.getText().toString().trim();
                String password = etPassregis.getText().toString().trim();

                if (!user.isEmpty() && !password.isEmpty()) {
                    registerUser(user, password);
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            } else {
                showConnectionLost();
            }
        });

        but_retry.setOnClickListener(v -> {
            progressbar.setVisibility(View.VISIBLE);
            progressbar.playAnimation();
            but_retry.setVisibility(View.GONE);
            textConnectionLost.setVisibility(View.GONE);

            executorService.execute(() -> {
                if (isNetworkAvailable()) {
                    runOnUiThread(() -> {
                        Intent intent = new Intent(RegisterActivity.this, RegisterActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(this::handleRetryFailure);
                }
            });
        });
    }

    private void registerUser(String user, String password) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DBHelper.UserEntry.COLUMN_NAME_USERNAME, user);
        values.put(DBHelper.UserEntry.COLUMN_NAME_PASSWORD, password);

        long newRowId = db.insert(DBHelper.UserEntry.TABLE_NAME, null, values);
        if (newRowId != -1) {
            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(RegisterActivity.this, "Failed to register", Toast.LENGTH_SHORT).show();
        }
    }

    private void showConnectionLost() {
        progressbar.setVisibility(View.VISIBLE);
        progressbar.playAnimation();
        textConnectionLost.setVisibility(View.GONE);
        but_retry.setVisibility(View.GONE);
        buttonRegister.setVisibility(View.GONE);
        etRegis.setVisibility(View.GONE);
        etPassregis.setVisibility(View.GONE);
        background.setVisibility(View.GONE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressbar.setVisibility(View.GONE);
            textConnectionLost.setVisibility(View.VISIBLE);
            but_retry.setVisibility(View.VISIBLE);
        }, 2000);
    }

    private void handleRetryFailure() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressbar.setVisibility(View.GONE);
            but_retry.setVisibility(View.VISIBLE);
            textConnectionLost.setVisibility(View.VISIBLE);
        }, 300);
    }
}
