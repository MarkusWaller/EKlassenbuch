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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.AppController;

/**
 * Activity to view and edit an entry of the database table 'book'.
 * <p/>
 * Created by Markus on 10.06.2015.
 */
public class EditBookActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    // LogCat tag
    private static final String TAG = EditBookActivity.class.getSimpleName();

    private List<String> classList = new ArrayList<>();
    private String book_id;
    private String teacher_name;
    private String subject;
    private String class_name;
    private Integer teacher_id;
    private ProgressDialog pDialog;
    private String url = AppConfig.URL_BOOK_CREATE;
    private EditText datumView;
    private EditText infoView;
    private EditText subjectView;
    private Boolean view;
    private Boolean edit;
    private Button button_book;
    private Spinner classSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        book_id = getIntent().getStringExtra("book_id");
        teacher_name = getIntent().getStringExtra("teacherName");
        subject = getIntent().getStringExtra("subject");
        class_name = getIntent().getStringExtra("class_name");
        teacher_id = getIntent().getIntExtra("teacher_id", 0);
        view = getIntent().getBooleanExtra("view", false);
        edit = getIntent().getBooleanExtra("edit", false);
        String info = getIntent().getStringExtra("info");

        TextView teacherView = (TextView) findViewById(R.id.teacher_book);
        teacherView.setText(teacher_name);

        subjectView = (EditText) findViewById(R.id.subject_book);
        subjectView.setText(subject);

        datumView = (EditText) findViewById(R.id.datum_book);
        datumView.setText(AppConfig.formatter.format(new Date()));

        classSpinner = (Spinner) findViewById((R.id.class_spinner));

        infoView = (EditText) findViewById(R.id.text_book);
        infoView.setText(info);

        button_book = (Button) findViewById(R.id.button_book);

        // Settings for read only mode.
        if (view) {
            subjectView.setEnabled(false);
            subjectView.setTextColor(teacherView.getCurrentTextColor());
            classSpinner.setEnabled(false);
            datumView.setEnabled(false);
            datumView.setTextColor(teacherView.getCurrentTextColor());
            infoView.setEnabled(false);
            infoView.setTextColor(teacherView.getCurrentTextColor());
            button_book.setVisibility(View.INVISIBLE);
        }
        // Settings for edit mode.
        if (edit) {
            button_book.setText("Ändern");
        }

        readDbClass();
    }

    /**
     * Invoked if the create / update button is pressed.
     *
     * @param view The interface component
     */
    public void onClickEditBook(View view) {
        subject = subjectView.getText().toString();
        String date = datumView.getText().toString();
        String info = infoView.getText().toString();

        editBook(date, subject, teacher_id.toString(), class_name, info);
    }

    /**
     * Creates / Updates the entry in the database table 'book'.
     *
     * @param date       The date of the entry
     * @param subject    The subject
     * @param teacher    The teacher id
     * @param class_name The class name
     * @param info       The info of the entry
     */
    private void editBook(final String date, final String subject, final String teacher, final String class_name, final String info) {
        // Tag used to cancel the request
        String tag_string_req = "req_editBook";

        pDialog.setMessage("Editierung läuft ...");
        showDialog();

        if (edit) {
            url = AppConfig.URL_BOOK_UPDATE;
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
                        // book successfully created / updated
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
                if (edit) {
                    params.put("book_id", book_id);
                }
                params.put("date", date);
                params.put("subject", subject);
                params.put("teacher", teacher);
                params.put("class", class_name);
                params.put("info", info);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * Read table 'class' from the database.
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

        final int position = adapter.getPosition(class_name);
        classSpinner.post(new Runnable() {
            @Override
            public void run() {
                classSpinner.setSelection(position);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        class_name = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
