package com.example.asli.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

import com.airbnb.lottie.LottieAnimationView;
import com.example.asli.R;
import com.example.asli.help.DBHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoginActivity extends AppCompatActivity {
    private EditText nickname, Password;
    private Button but_login, but_retry;
    private TextView textConnectionLost,but_register,textView;
    private LottieAnimationView progressBar,background;
    DBHelper dbHelper;
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
        setContentView(R.layout.activity_login);

        dbHelper = new DBHelper(this);

        nickname = findViewById(R.id.et_nickname);
        Password = findViewById(R.id.et_password);
        but_login = findViewById(R.id.but_login);
        but_register = findViewById(R.id.but_register);
        but_retry = findViewById(R.id.btn_retry);
        textConnectionLost = findViewById(R.id.text_connection_lost);
        progressBar = findViewById(R.id.lottieProgressBar);
        background = findViewById(R.id.lottieBackground);
        textView = findViewById(R.id.tv_belum_punya_akun);

        textConnectionLost.setVisibility(View.GONE);
        but_retry.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);

        checkLoginStatus();

        but_register.setOnClickListener(v -> {
            if (isNetworkAvailable()) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            } else {
                showConnectionLost();
            }
        });


        but_login.setOnClickListener(view -> {
            if (isNetworkAvailable()) {
                attemptLogin();
            } else {
                showConnectionLost();
            }
        });

        but_retry.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.playAnimation();
            but_retry.setVisibility(View.GONE);
            textConnectionLost.setVisibility(View.GONE);

            executorService.execute(() -> {
                if (isNetworkAvailable()) {
                    runOnUiThread(() -> {
                        Intent intent = new Intent(LoginActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    });
                } else {
                    runOnUiThread(this::handleRetryFailure);
                }
            });
        });

    }

    private void attemptLogin() {
        String user = nickname.getText().toString().trim();
        String password = Password.getText().toString().trim();
        if (!user.isEmpty() && !password.isEmpty()) {
            boolean isValid = isValidLogin(user, password);
            if (isValid) {
                saveLoginStatus(user);
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(LoginActivity.this, "Please enter username and password", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkLoginStatus() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.UserEntry.TABLE_NAME + " WHERE " + DBHelper.UserEntry.COLUMN_NAME_LOGGED_IN + " = 1", null);
        if (cursor.getCount() > 0) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        cursor.close();
    }

    private boolean isValidLogin(String user, String password) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DBHelper.UserEntry.TABLE_NAME + " WHERE " + DBHelper.UserEntry.COLUMN_NAME_USERNAME + " = ? AND " + DBHelper.UserEntry.COLUMN_NAME_PASSWORD + " = ?", new String[]{user, password});
        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        return isValid;
    }

    private void saveLoginStatus(String user) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DBHelper.UserEntry.COLUMN_NAME_LOGGED_IN, 1);
        db.update(DBHelper.UserEntry.TABLE_NAME, values, DBHelper.UserEntry.COLUMN_NAME_USERNAME + " = ?", new String[]{user});
    }

    private void showConnectionLost() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.playAnimation();
        textConnectionLost.setVisibility(View.GONE);
        but_retry.setVisibility(View.GONE);
        but_login.setVisibility(View.GONE);
        nickname.setVisibility(View.GONE);
        Password.setVisibility(View.GONE);
        but_register.setVisibility(View.GONE);
        background.setVisibility(View.GONE);
        textView.setVisibility(View.GONE);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            textConnectionLost.setVisibility(View.VISIBLE);
            but_retry.setVisibility(View.VISIBLE);
        }, 2000);
    }


    private void handleRetryFailure() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            progressBar.setVisibility(View.GONE);
            but_retry.setVisibility(View.VISIBLE);
            textConnectionLost.setVisibility(View.VISIBLE);
        }, 300);
    }
}