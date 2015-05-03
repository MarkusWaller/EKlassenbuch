package de.sap_project.e_klassenbuch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.sap_project.e_klassenbuch.data.SecureUtil;
import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.AppController;

/**
 * Register Activity
 * <p/>
 * Created by Markus
 */
public class RegisterActivity extends ActionBarActivity {
    // LogCat tag
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private EditText regEmail;
    private EditText regPwd;
    private EditText regFirst;
    private EditText regLast;
    private EditText regDate;
    private EditText regClass;
    private ProgressDialog pDialog;
    private boolean isTeacher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        regEmail = (EditText) findViewById(R.id.reg_email);
        regFirst = (EditText) findViewById(R.id.reg_first_name);
        regLast = (EditText) findViewById(R.id.reg_last_name);
        regDate = (EditText) findViewById(R.id.reg_date);
        regPwd = (EditText) findViewById(R.id.reg_password);
        regClass = (EditText) findViewById(R.id.reg_class);

        Button btnRegister = (Button) findViewById(R.id.button_register);
        Button btnLinkToLogin = (Button) findViewById(R.id.button_link_to_login);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = regEmail.getText().toString();
                String password = regPwd.getText().toString();
                String first_name = regFirst.getText().toString();
                String last_name = regLast.getText().toString();
                String date = regDate.getText().toString();
                String class_name = regClass.getText().toString();


                // Check for empty data in the form
                if (email.trim().length() > 0 && password.trim().length() > 0) {
                    // login user
                    register(email, password, first_name, last_name, date, class_name);
                } else {
                    // Prompt user to enter credentials
                    Toast.makeText(getApplicationContext(),
                            "Please enter the credentials!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    public void onRegCheckboxClicked(View view) {
        // Ist die Checkbox angekreuzt
        isTeacher = ((CheckBox) view).isChecked();

        if (isTeacher) {
            regDate.setVisibility(View.INVISIBLE);
            regClass.setVisibility(View.INVISIBLE);
        } else {
            regDate.setVisibility(View.VISIBLE);
            regClass.setVisibility(View.VISIBLE);
        }
    }

    private void register(final String email, final String password, final String first_name,
                          final String last_name, final String date, final String class_name) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registrierung läuft ...");
        showDialog();

        String url = AppConfig.URL_STUDENT_REGISTER;

        if (isTeacher) {
            url = AppConfig.URL_TEACHER_REGISTER;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Registration Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    // Check for error node in json
                    if (success == 1) {
                        // user successfully registered
                        String successMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                successMsg, Toast.LENGTH_LONG).show();

                        // Launch login activity
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Error in registration. Get the error message
                        String errorMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Registration Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to registration url
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("password", SecureUtil.getInstance().getPasswordHash(password));
                params.put("first_name", first_name);
                params.put("last_name", last_name);
                params.put("birth_date", date);
                params.put("class", class_name);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_regristrierung, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
