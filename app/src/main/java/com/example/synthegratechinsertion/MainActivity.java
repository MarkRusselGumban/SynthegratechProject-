package com.example.synthegratechinsertion;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        EditText emailField = findViewById(R.id.loginUsername);
        EditText passwordField = findViewById(R.id.loginPassword);
        Button lButton = findViewById(R.id.loginButton);
        Button rButton = findViewById(R.id.loginRegisterButton);
        Button fpButton = findViewById(R.id.forgotPasswordButton);

        lButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailField.getText().toString();
                String password = passwordField.getText().toString();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpURLConnection urlConnection = null;
                        try {
                            URL url = new URL("http://10.0.2.2/synthegratech/login.php");
                            urlConnection = (HttpURLConnection) url.openConnection();
                            urlConnection.setRequestMethod("POST");
                            urlConnection.setDoOutput(true);
                            String requestBody = "email=" + email + "&password=" + password;
                            OutputStream outputStream = urlConnection.getOutputStream();
                            outputStream.write(requestBody.getBytes());
                            outputStream.close();
                            InputStream inputStream = urlConnection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            final StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();
                            inputStream.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (!response.toString().equals("Login failed")) {
                                        String[] parts = response.toString().split(",");
                                        if (parts.length >= 3) {
                                            String name = parts[0];
                                            String email = parts[1];
                                            String type = parts[2];
                                            Bundle bundle = new Bundle();
                                            bundle.putString("name", name);
                                            bundle.putString("email", email);
                                            bundle.putString("type", type);
                                            Intent intent = new Intent(MainActivity.this, UserPanel.class);
                                            intent.putExtras(bundle);
                                            startActivity(intent);
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MainActivity.this, "Login failed! Please try again later.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } finally {
                            if (urlConnection != null) {
                                urlConnection.disconnect();
                            }
                        }
                    }
                });
                thread.start();
            }
        });
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,RegisterActivity.class));
            }
        });
        fpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ForgotPassword.class));
            }
        });
    }
}
