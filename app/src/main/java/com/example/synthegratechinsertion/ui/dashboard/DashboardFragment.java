package com.example.synthegratechinsertion.ui.dashboard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;

import com.example.synthegratechinsertion.MainActivity;
import com.example.synthegratechinsertion.R;
import com.example.synthegratechinsertion.RegisterActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DashboardFragment extends Fragment {
    String email = "";
    private DashboardViewModel dashboardViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dashboardViewModel =  ViewModelProviders.of(this).get(DashboardViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        Button deleteButton = root.findViewById(R.id.deleteAccountButton);
        Bundle bundle = getArguments();
        if (bundle != null) {
            String name = bundle.getString("name");
            email = bundle.getString("email");
            String type = bundle.getString("type");
            TextView nameTextView = root.findViewById(R.id.accountName);
            nameTextView.setText(name);
            TextView emailTextView = root.findViewById(R.id.accountEmail);
            emailTextView.setText(email);
            TextView typeTextView = root.findViewById(R.id.accountType);
            typeTextView.setText(type);
        }
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext()); // or requireContext()
                builder.setTitle("Confirmation");
                builder.setMessage("Are you sure you want to delete your account?");
                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                HttpURLConnection urlConnection = null;
                                try {
                                    URL url = new URL("http://10.0.2.2/synthegratech/deleteaccount.php");
                                    urlConnection = (HttpURLConnection) url.openConnection();
                                    urlConnection.setRequestMethod("POST");
                                    urlConnection.setDoOutput(true);
                                    String requestBody = "email=" + email; // Provide the email here
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
                                    getActivity().runOnUiThread(new Runnable() { // Use getActivity() to access the activity's runOnUiThread
                                        @Override
                                        public void run() {
                                            AlertDialog.Builder resultBuilder = new AlertDialog.Builder(getContext()); // or requireContext()
                                            resultBuilder.setTitle("Delete Account");
                                            if (response.toString().equals("Account deleted successfully")) {
                                                resultBuilder.setMessage("Your account has been deleted successfully.");
                                            } else {
                                                resultBuilder.setMessage("Failed to delete your account. Please try again later.");
                                            }
                                            resultBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    startActivity(new Intent(root.getContext(), MainActivity.class));
                                                }
                                            });
                                            resultBuilder.show();
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
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
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
        });

        getActivity().setTitle("Account");
        return root;
    }
}

