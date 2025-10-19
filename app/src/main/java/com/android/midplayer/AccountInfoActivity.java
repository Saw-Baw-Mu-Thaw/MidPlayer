package com.android.midplayer;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class AccountInfoActivity extends AppCompatActivity {
    TextView txtAccountUsername, txtAccountEmail;
    Toolbar tbAccountInfo;

    RadioGroup rgTheme;
    RadioButton rbLight, rbDark;
    Button btnLogout, btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        tbAccountInfo = findViewById(R.id.tbAccountInfo);
        setSupportActionBar(tbAccountInfo);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        txtAccountUsername = findViewById(R.id.txtAccountUsername);
        txtAccountEmail = findViewById(R.id.txtAccountEmail);
        tbAccountInfo = findViewById(R.id.tbAccountInfo);
        btnLogout = findViewById(R.id.btnLogout);
        btnDelete = findViewById(R.id.btnDelete);
        rbLight = findViewById(R.id.rbLight);
        rbDark = findViewById(R.id.rbDark);
        rgTheme = findViewById(R.id.rgTheme);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        String email = intent.getStringExtra("email");

        txtAccountUsername.setText(String.format("Username - %s", username));
        txtAccountEmail.setText(String.format("Email - %s", email));

        boolean savedDark = getSharedPreferences("theme", MODE_PRIVATE)
                .getBoolean("dark_mode", false);

        if (savedDark) {
            rbDark.setChecked(true);
        } else {
            rbLight.setChecked(true);
        }

        rgTheme.setOnCheckedChangeListener((radioGroup, checkedId) -> {
            boolean dark = (checkedId == R.id.rbDark);

            if (dark) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            getSharedPreferences("theme", MODE_PRIVATE)
                    .edit()
                    .putBoolean("dark_mode", dark)
                    .apply();


            recreate();
        });


        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccountInfoActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteConfirmation();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void deleteConfirmation() {
        new AlertDialog.Builder(AccountInfoActivity.this)
                .setTitle("Delete Account")
                .setMessage("Are you sure you want to delete your account?")
                .setPositiveButton("Yes",(dialog, which)-> deleteUserData())
                .setNegativeButton("Cancel", null)
                .show();

    }

    private void deleteUserData() {
        String emailToDel = getIntent().getStringExtra("email");
        if (emailToDel == null)
            return;

        File originalFile = new File(getFilesDir(), "users.txt");
        File tempFile = new File(getFilesDir(), "users_temp.txt");

        try {
            FileInputStream fis = new FileInputStream(originalFile);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            FileOutputStream fos = new FileOutputStream(tempFile);

            String line;
            while ((line = reader.readLine()) != null ) {
                String[] parts = line.split(",");
                if (parts.length >= 2 ) {
                    String email = parts[1].trim();
                    if (!email.equalsIgnoreCase(emailToDel)) {
                        fos.write((line + "\n").getBytes());
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if(originalFile.delete()) {
            if (tempFile.renameTo(originalFile)) {
                Toast.makeText(AccountInfoActivity.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AccountInfoActivity.this, MainActivity.class);
                intent.addFlags((Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                startActivity(intent);
                finish();
            }
        }
    }
}