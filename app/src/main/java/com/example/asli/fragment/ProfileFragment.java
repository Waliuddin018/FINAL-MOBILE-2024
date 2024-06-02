package com.example.asli.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.asli.help.DBHelper;
import com.example.asli.R;
import com.example.asli.activity.LoginActivity;

public class ProfileFragment extends Fragment {

    TextView tv_welcome;
    Button btn_logout, btn_update;
    EditText et_username, et_password;
    DBHelper dbHelper;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tv_welcome = view.findViewById(R.id.tv_welcome);
        btn_logout = view.findViewById(R.id.btn_logout);
        btn_update = view.findViewById(R.id.btn_update);
        et_username = view.findViewById(R.id.et_username);
        et_password = view.findViewById(R.id.et_password);
        dbHelper = new DBHelper(getActivity());

        String user = dbHelper.getLoggedInUser();

        if (user != null) {
            tv_welcome.setText("Hello " + user);
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        btn_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dbHelper.logout();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String oldUsername = dbHelper.getLoggedInUser();
                String newUsername = et_username.getText().toString().trim();
                String newPassword = et_password.getText().toString().trim();

                if (newUsername.isEmpty() || newPassword.isEmpty()) {
                    Toast.makeText(getActivity(), "Username atau password tidak boleh kosong", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check if new username is the same as the old username
                if (oldUsername.equals(newUsername)) {
                    // Check if the new password is different from the old password
                    String oldPassword = dbHelper.getPasswordForUser(oldUsername);
                    if (!newPassword.equals(oldPassword)) {
                        boolean isAccountUpdated = dbHelper.updateAkun(oldUsername, newUsername, newPassword);
                        if (isAccountUpdated) {
                            Toast.makeText(getActivity(), "Data akun berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            tv_welcome.setText("Hello " + newUsername); // <-- Updated to use new username
                            et_username.setText(""); // Clear input fields after successful update
                            et_password.setText("");
                        } else {
                            Toast.makeText(getActivity(), "Gagal memperbarui data akun", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "Password baru tidak boleh sama dengan password lama", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (dbHelper.isUsernameExists(newUsername)) {
                        Toast.makeText(getActivity(), "Username sudah ada dalam database", Toast.LENGTH_SHORT).show();
                    } else {
                            Toast.makeText(getActivity(), "Data akun dan favorit berhasil diperbarui", Toast.LENGTH_SHORT).show();
                            tv_welcome.setText("Hello " + newUsername);
                            et_username.setText("");
                            et_password.setText("");
                    }
                }
            }
        });

        return view;
    }
}
