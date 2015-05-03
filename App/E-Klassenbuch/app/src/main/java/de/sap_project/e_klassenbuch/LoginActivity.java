package de.sap_project.e_klassenbuch;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.sap_project.e_klassenbuch.data.SecureUtil;
import de.sap_project.e_klassenbuch.data.User;
import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.AppController;
import de.sap_project.e_klassenbuch.db.SessionManager;

/**
 * Login User Activity
 * <p/>
 * Created by Markus on 24.04.2015.
 */
public class LoginActivity extends Activity {
    // LogCat tag
    private static final String TAG = LoginActivity.class.getSimpleName();

    private EditText inputEmail;
    private EditText inputPassword;
    private ProgressDialog pDialog;
    private SessionManager session;
    private boolean isTeacher = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        Button btnLogin = (Button) findViewById(R.id.email_sign_in_button);
        Button btnLinkToRegister = (Button) findViewById(R.id.button_link_to_register);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        // Session manager
        session = SessionManager.getInstance();

        // Check if user is already logged in or not
        if (session.isLoggedIn()) {
            // User is already logged in. Take him to main activity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        // Login button Click Event
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = inputEmail.getText().toString();
                String password = inputPassword.getText().toString();

                // Check for empty data in the form
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    checkLogin(email, password);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Register Screen
        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void onCheckboxClicked(View view) {
        // Ist die Checkbox angekreuzt
        isTeacher = ((CheckBox) view).isChecked();
    }

    /**
     * function to verify login details in mysql db
     */
    private void checkLogin(final String email, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";

        pDialog.setMessage(getString(R.string.login_running));
        showDialog();

        String url = AppConfig.URL_STUDENT_LOGIN;

        if (isTeacher) {
            url = AppConfig.URL_TEACHER_LOGIN;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Login Response: " + response);
                hideDialog();
                String db_password;
                int db_id;
                String db_first_name;
                String db_last_name;
                String db_email;
                String db_class = "";
                Date db_birth_date = null;
                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    // Check for error node in json
                    if (success == 1) {
                        // user successfully logged in
                        if (isTeacher) {
                            JSONArray user = jObj.getJSONArray("teacher");
                            db_id = user.getJSONObject(0).getInt("teacher_id");
                            db_first_name = user.getJSONObject(0).getString("first_name");
                            db_last_name = user.getJSONObject(0).getString("last_name");
                            db_email = user.getJSONObject(0).getString("email");
                            db_password = user.getJSONObject(0).getString("password");
                        } else {
                            JSONArray user = jObj.getJSONArray("student");
                            db_id = user.getJSONObject(0).getInt("student_id");
                            db_first_name = user.getJSONObject(0).getString("first_name");
                            db_last_name = user.getJSONObject(0).getString("last_name");
                            db_class = user.getJSONObject(0).getString("class");
                            db_email = user.getJSONObject(0).getString("email");
                            db_birth_date = AppConfig.formatter.parse(user.getJSONObject(0).getString("birth_date"));
                            db_password = user.getJSONObject(0).getString("password");
                        }
                        String passwordHash = SecureUtil.getInstance().getPasswordHash(password);
                        if (passwordHash.equals(db_password)) {
                            // Create login session
                            session.setLogin(true);
                            session.setUser(new User(db_id,db_first_name, db_last_name, db_email, db_password, isTeacher, db_class, db_birth_date ));

                            // Launch main activity
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.error_email_password), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        // Error in login. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Login Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
