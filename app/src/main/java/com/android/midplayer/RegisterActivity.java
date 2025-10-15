package com.android.midplayer;

import static android.app.ProgressDialog.show;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public class RegisterActivity extends AppCompatActivity {


    EditText edtRegisterUsername, edtRegisterEmail, edtRegisterPassword, edtConfirmPassword;
    Button btnCreateAccount;
    TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtRegisterUsername = findViewById(R.id.edtRegisterUsername);
        edtRegisterEmail = findViewById(R.id.edtRegisterEmail);
        edtRegisterPassword = findViewById(R.id.edtRegisterPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        txtLogin = findViewById(R.id.txtLogin);


        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtRegisterUsername.getText().toString();
                String email = edtRegisterEmail.getText().toString();
                String password = edtRegisterPassword.getText().toString();
                String confirm_password = edtConfirmPassword.getText().toString();


                if(username.isEmpty() || email.isEmpty() || password.isEmpty() || confirm_password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this,"Please fill all fields", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirm_password)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                } else if(password.length() < 6) {
                    Toast.makeText(RegisterActivity.this,"Password mush be at least 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    saveUserData(username, email, password);

                    Intent intent = new Intent(RegisterActivity.this, BaseActivity.class);
                    startActivity(intent);
                    finish();

                }
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    private void saveUserData(String username, String email, String password) {
        username = username.trim();
        email = email.trim();
        password = password.trim();

        try{
            FileOutputStream fos = openFileOutput("users.txt", Context.MODE_APPEND);
            OutputStreamWriter writer = new OutputStreamWriter(fos);

            String userline = username + "," + email + "," + password + "\n";
            writer.write(userline);
            writer.flush();
            writer.close();

            Toast.makeText(RegisterActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(RegisterActivity.this, BaseActivity.class);
            intent.putExtra("username", username);
            intent.putExtra("email", email);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}