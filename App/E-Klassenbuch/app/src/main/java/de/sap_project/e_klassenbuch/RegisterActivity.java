package de.sap_project.e_klassenbuch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.sap_project.e_klassenbuch.data.SecureUtil;
import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.AppController;

/**
 * Register Activity.
 * <p/>
 * Created by Markus on 28.04.2015.
 */
public class RegisterActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    // LogCat tag
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private List<String> classList = new ArrayList<>();
    private EditText regEmail;
    private EditText regPwd;
    private EditText regFirst;
    private EditText regLast;
    private EditText regDate;
    private ProgressDialog pDialog;
    private Spinner classSpinner;
    private TextView classLabel;
    private String class_name;
    private String url = AppConfig.URL_STUDENT_REGISTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Spinner regSpinner = (Spinner) findViewById((R.id.spinner));
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.spinner_array, R.layout.simple_listview);
        adapter.setDropDownViewResource(R.layout.simple_listview);
        regSpinner.setAdapter(adapter);
        regSpinner.setOnItemSelectedListener(this);

        regEmail = (EditText) findViewById(R.id.reg_email);
        regFirst = (EditText) findViewById(R.id.reg_first_name);
        regLast = (EditText) findViewById(R.id.reg_last_name);
        regDate = (EditText) findViewById(R.id.reg_date);
        regPwd = (EditText) findViewById(R.id.reg_password);

        classLabel = (TextView) findViewById((R.id.reg_class_label));
        classSpinner = (Spinner) findViewById((R.id.reg_class_spinner));

        Button btnRegister = (Button) findViewById(R.id.button_register);
        Button btnLinkToLogin = (Button) findViewById(R.id.button_link_to_login);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String email = regEmail.getText().toString();
                String password = regPwd.getText().toString();
                String first_name = regFirst.getText().toString();
                String last_name = regLast.getText().toString();
                String date = regDate.getText().toString();

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

        readDbClass();
    }

    /**
     * Adds the new user to the database table.
     * For the selection of the correct database table (URL) see method onItemSelected.
     *
     * @see RegisterActivity#onItemSelected(AdapterView, View, int, long)
     *
     * @param email The email address of the user
     * @param password The password of the user
     * @param first_name The users first name
     * @param last_name The users last name
     * @param date The users birthdate (only for student)
     * @param class_name The class name (only for student)
     */
    private void register(final String email, final String password, final String first_name,
                          final String last_name, final String date, final String class_name) {
        // Tag used to cancel the request
        String tag_string_req = "req_register";

        pDialog.setMessage("Registrierung läuft ...");
        showDialog();

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

    /**
     * Read 'class' table from database.
     */
    private void readDbClass() {
        // Tag used to cancel the request
        String tag_string_req = "req_readDbClass";

        pDialog.setMessage("Lese Klassen ...");
        showDialog();

        classList.clear();

        String url = AppConfig.URL_CLASS_GET_ALL;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Class Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    // Check for error node in json
                    if (success == 1) {
                        // Class data
                        JSONArray classData = jObj.getJSONArray("class");
                        for (int i = 0; i < classData.length(); i++) {
                            JSONObject c = classData.getJSONObject(i);
                            String name = c.getString("name");
                            classList.add(name);
                        }
                        fillSpinnerList();
                    } else {
                        // Error in login. Get the error message
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
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Fills the dropdown list with the class names.
     */
    private void fillSpinnerList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_listview, classList);
        adapter.setDropDownViewResource(R.layout.simple_listview);
        classSpinner.setAdapter(adapter);
        classSpinner.setOnItemSelectedListener(this);
    }

    /**
     * Shows the message dialog.
     */
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    /**
     * Hides the message dialog.
     */
    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Spinner spinner = (Spinner) parent;

        if (spinner.getId() == R.id.spinner) {
            Object item = parent.getItemAtPosition(position);

            if (item instanceof String) {
                String s = (String) item;
                switch (s) {
                    case "Schüler":
                        url = AppConfig.URL_STUDENT_REGISTER;
                        regDate.setVisibility(View.VISIBLE);
                        classLabel.setVisibility(View.VISIBLE);
                        classSpinner.setVisibility(View.VISIBLE);
                        break;
                    case "Lehrer":
                        url = AppConfig.URL_TEACHER_REGISTER;
                        regDate.setVisibility(View.INVISIBLE);
                        classLabel.setVisibility(View.INVISIBLE);
                        classSpinner.setVisibility(View.INVISIBLE);
                        break;
                    case "Admin":
                        url = AppConfig.URL_ADMIN_REGISTER;
                        regDate.setVisibility(View.INVISIBLE);
                        classLabel.setVisibility(View.INVISIBLE);
                        classSpinner.setVisibility(View.INVISIBLE);
                        break;
                }
            }
        } else if (spinner.getId() == R.id.reg_class_spinner) {
            class_name = parent.getItemAtPosition(position).toString();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
