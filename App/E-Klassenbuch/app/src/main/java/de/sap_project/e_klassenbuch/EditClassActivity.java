package de.sap_project.e_klassenbuch;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.AppController;


public class EditClassActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    // LogCat tag
    private static final String TAG = EditClassActivity.class.getSimpleName();

    private EditText class_name;
    private Spinner hTeacherSpinner;
    private String url = AppConfig.URL_CLASS_CREATE;
    private String h_teacher;
    private HashMap<Integer, String> teacherMap = new HashMap<>();
    private Boolean edit;

    private ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_class);

        teacherMap = (HashMap<Integer, String>) getIntent().getSerializableExtra("teacherMap");
        edit = getIntent().getBooleanExtra("edit", false);
        String className = getIntent().getStringExtra("className");
        String h_teacherName = getIntent().getStringExtra("h_teacherName");

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        List<String> list = new ArrayList<>(teacherMap.values());

        hTeacherSpinner = (Spinner) findViewById((R.id.h_teacher_spinner));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_listview, list);
        adapter.setDropDownViewResource(R.layout.simple_listview);
        hTeacherSpinner.setAdapter(adapter);
        hTeacherSpinner.setOnItemSelectedListener(this);

        class_name = (EditText) findViewById(R.id.class_name);
        class_name.setText(className);

        Button button = (Button) findViewById(R.id.edit_button);

        if (edit) {
            final int position = adapter.getPosition(h_teacherName);
            hTeacherSpinner.post(new Runnable() {
                @Override
                public void run() {
                    hTeacherSpinner.setSelection(position);
                }
            });

            class_name.setEnabled(false);

            button.setText("Ändern");
        }
    }

    public void onClickEditClass(View view) {
        String name = class_name.getText().toString();

        String h_teacher_id = "";
        for (Integer key : teacherMap.keySet()) {
            if (teacherMap.get(key).equals(h_teacher)) {
                h_teacher_id = key.toString();
            }
        }

        editClass(name, h_teacher_id);
    }

    private void editClass(final String class_name, final String h_teacher) {
        // Tag used to cancel the request
        String tag_string_req = "req_editClass";

        pDialog.setMessage("Editierung läuft ...");
        showDialog();

        if (edit) {
            url = AppConfig.URL_CLASS_UPDATE;
        }
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Edit Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    // Check for error node in json
                    if (success == 1) {
                        // class successfully created / updated
                        String successMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                successMsg, Toast.LENGTH_LONG).show();

                        finish();
                    } else {
                        // Error in creation / update. Get the error message
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
                Log.e(TAG, "Edit Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to create / update url
                Map<String, String> params = new HashMap<>();
                params.put("name", class_name);
                params.put("h_teacher", h_teacher);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_class, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        h_teacher = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
