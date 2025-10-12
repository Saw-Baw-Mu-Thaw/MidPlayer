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
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    EditText edtLoginUsername, edtLoginPassword;
    Button btnLoginRegister, btnLogin;
    TextView txtSignup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtLoginUsername = findViewById(R.id.edtLoginUsername);
        edtLoginPassword = findViewById(R.id.edtLoginPassword);
        btnLoginRegister = findViewById(R.id.btnLoginRegister);
        btnLogin = findViewById(R.id.btnLogin);
        txtSignup = findViewById(R.id.txtSignup);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = edtLoginUsername.getText().toString();
                String password = edtLoginPassword.getText().toString();

                if(checkUserLogin(username, password)) {
                    Toast.makeText(MainActivity.this,"Login Successfully",Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(MainActivity.this, SongsLibrary.class);
                    // pass username to next screen
                    startActivity(intent);
                    finish();

                } else {
                    Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnLoginRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
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
        try {
            InputStream inputStream = getResources().openRawResource(R.raw.user);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String storedUsername = parts[0].trim();
                    String storedPassword = parts[2].trim();

                    if(storedUsername.equals(username) && storedPassword.equals(password)) {
                        reader.close();
                        return true;
                    }
                }
            }

            reader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;

    }


}