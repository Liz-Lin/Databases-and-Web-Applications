package edu.uci.ics.team43.fabflix;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import android.widget.AutoCompleteTextView;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A login screen that offers login via email/password.
 */
//ActionBarActivity
public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         Button loginButton = (Button) findViewById(R.id.email_sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                connectToTomcat(view);
            }
        });

    }
    @Override
    protected void onStart() {
        super.onStart();
        if (SessionManager.getInstance().isUserLogin(this)){
            goToSearch();
        }
    }
    private void goToSearch(){
        Intent goToIntent = new Intent(LoginActivity.this, SearchActivity.class);
        startActivity(goToIntent);
    }

    public void connectToTomcat(View view) {

        AutoCompleteTextView mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        EditText mPasswordView = (EditText) findViewById(R.id.password);
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();
        // Post request form data
        final Map<String, String> params = new HashMap<String, String>();
        params.put("email", email);
        params.put("password", password);

        // no user is logged in, so we must connect to the server

        // Use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue;

        final String login_url =getResources().getString(R.string.API_host)+getResources().getString(R.string.API_login);
        final StringRequest loginRequest = new StringRequest(Request.Method.POST, login_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        ((TextView) findViewById(R.id.http_response)).setText(response);
                        // Add the request to the RequestQueue.
                        //queue.add(afterLoginRequest);
                        try {
                            JSONObject jObject = new JSONObject(response);
                            String status = jObject.getString("status");
                            String message = jObject.getString("message");

                            if (status.equals("success")) {
                                SessionManager.getInstance().setIsLoginTrue();
                                goToSearch();

                            } else {
                                //ADD WORNG PASSWORD
//                                ((TextView) findViewById(R.id.http_response)).setText(getResources().getString(R.string.error_incorrect_password));
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        Log.d("security.error", error.toString());
                        Toast.makeText(getApplicationContext(),
                                "Sorry, cannot access the server.", Toast.LENGTH_LONG).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }  // HTTP POST Form Data
        };

        queue.add(loginRequest);

//        SafetyNet.getClient(this).verifyWithRecaptcha("your-site-key")
//                .addOnSuccessListener(this, new OnSuccessListener<SafetyNetApi.RecaptchaTokenResponse>() {
//                    @Override
//                    public void onSuccess(SafetyNetApi.RecaptchaTokenResponse response) {
//                        if (!response.getTokenResult().isEmpty()) {
//                            // Add the request to the RequestQueue.
//                            params.put("g-recaptcha-response", response.getTokenResult());
//                            queue.add(loginRequest);
//                        }
//                    }
//                })
//                .addOnFailureListener(this, new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        if (e instanceof ApiException) {
//                            ApiException apiException = (ApiException) e;
//                            Log.d("Login", "Error message: " +
//                                    CommonStatusCodes.getStatusCodeString(apiException.getStatusCode()));
//                        } else {
//                            Log.d("Login", "Unknown type of error: " + e.getMessage());
//                        }
//                    }
//                });

    }

}
