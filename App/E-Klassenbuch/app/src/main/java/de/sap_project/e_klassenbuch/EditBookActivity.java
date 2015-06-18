package de.sap_project.e_klassenbuch;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.AppController;


public class EditBookActivity extends ActionBarActivity {
    // LogCat tag
    private static final String TAG = EditBookActivity.class.getSimpleName();

    private String teacher_name;
    private String subject;
    private String className;
    private Integer teacher_id;
    private ProgressDialog pDialog;
    private String url = AppConfig.URL_BOOK_CREATE;
    private EditText datumView;
    private EditText infoView;
    private Boolean view;
    private Button button_book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        teacher_name = getIntent().getStringExtra("teacherName");
        subject = getIntent().getStringExtra("subject");
        className = getIntent().getStringExtra("className");
        teacher_id = getIntent().getIntExtra("teacher_id", 0);
        view = getIntent().getBooleanExtra("view", false);
        String info = getIntent().getStringExtra("info");

        TextView teacherView = (TextView) findViewById(R.id.teacher_book);
        teacherView.setText(teacher_name);

        TextView subjectView = (TextView) findViewById(R.id.subject_book);
        subjectView.setText(subject);

        datumView = (EditText) findViewById(R.id.datum_book);
        datumView.setText(AppConfig.formatter.format(new Date()));

        infoView = (EditText) findViewById(R.id.text_book);

        Button button_book = (Button) findViewById(R.id.button_book);

        if (view) {
            datumView.setEnabled(false);
            datumView.setTextColor(subjectView.getCurrentTextColor());
            infoView.setEnabled(false);
            infoView.setText(info);
            infoView.setTextColor(subjectView.getCurrentTextColor());
            button_book.setVisibility(View.INVISIBLE);
        }
    }

    public void onClickEditBook(View view) {
        String date = datumView.getText().toString();
        String info = infoView.getText().toString();

        editBook(date, subject, teacher_id.toString(), className, info);
    }

    private void editBook(final String date, final String subject, final String teacher, final String className, final String info) {
        // Tag used to cancel the request
        String tag_string_req = "req_editBook";

        pDialog.setMessage("Editierung läuft ...");
        showDialog();

//        if (edit) {
//            url = AppConfig.URL_CLASS_UPDATE;
//        }

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
                        // user successfully registered
                        String successMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                successMsg, Toast.LENGTH_LONG).show();

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
                Log.e(TAG, "Edit Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to registration url
                Map<String, String> params = new HashMap<>();
                params.put("date", date);
                params.put("subject", subject);
                params.put("teacher", teacher);
                params.put("class", className);
                params.put("info", info);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
        if (id == R.id.action_settings) {
            return true;
        }

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
}
