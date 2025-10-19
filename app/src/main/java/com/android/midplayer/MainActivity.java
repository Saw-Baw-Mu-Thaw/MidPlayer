package com.android.midplayer;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.Buffer;

public class MainActivity extends AppCompatActivity{

    EditText edtLoginUsername, edtLoginPassword;
    Button btnLogin;
    TextView txtSignup;
    String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtLoginUsername = findViewById(R.id.edtLoginUsername);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignup = findViewById(R.id.txtSignup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtLoginUsername.getText().toString();
                String password = edtLoginPassword.getText().toString();

                if(checkUserLogin(username, password)) {
                    Toast.makeText(MainActivity.this,"Login Successfully",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, BaseActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("email", email);
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean checkUserLogin(String username, String password) {

        username = username.trim();
        password = password.trim();


        try {

            BufferedReader[] readers = new BufferedReader[2];

            File internalFile = new File(getFilesDir(), "users.txt");
            if (internalFile.exists()) {
                readers[0] = new BufferedReader(new InputStreamReader(openFileInput("users.txt")));
            }

            readers[1] = new BufferedReader(new InputStreamReader(getResources().openRawResource(R.raw.users)));

            for(BufferedReader reader : readers) {
                if(reader == null)
                    continue;

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 3) {
                        String storedUsername = parts[0].trim();
                        String storedEmail = parts[1].trim();
                        String storedPassword = parts[2].trim();

                        if(storedUsername.equals(username) && storedPassword.equals(password)) {
                            email = storedEmail;
                            reader.close();
                            return true;
                        }
                    }
                }
                reader.close();

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }
}