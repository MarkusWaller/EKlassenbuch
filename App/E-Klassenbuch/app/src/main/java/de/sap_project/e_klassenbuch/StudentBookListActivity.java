package de.sap_project.e_klassenbuch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.sap_project.e_klassenbuch.data.User;
import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.AppController;
import de.sap_project.e_klassenbuch.db.SessionManager;


public class StudentBookListActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    // LogCat tag
    private static final String TAG = StudentBookListActivity.class.getSimpleName();

    private SessionManager session;
    private ProgressDialog pDialog;
    private ListView listView;
    private User user;
    private HashMap<Integer, String> teacherMap = new HashMap<>();
    private List<HashMap<String, String>> bookList = new ArrayList<>();
    private String[] from = new String[]{"col_1", "col_2", "col3", "class", "info"};
    private int[] to = new int[]{R.id.textViewCol1, R.id.textViewCol2, R.id.textViewCol3};

    private Spinner filterSubjectSpinner;
    private Set<String> subjectList = new HashSet<>();
    private List<HashMap<String, String>> filteredBookList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_book_list);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        filterSubjectSpinner = (Spinner) findViewById((R.id.filter_subject_spinner));

        listView = (ListView) findViewById(R.id.listViewStudentBook);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String date = bookList.get(position).get(from[0]);
                String subject = bookList.get(position).get(from[1]);
                String teacher = bookList.get(position).get(from[2]);
                String class_name = bookList.get(position).get(from[3]);
                String info = bookList.get(position).get(from[4]);

                // Launch EditClass activity
                Intent intent = new Intent(StudentBookListActivity.this, EditBookActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("subject", subject);
                intent.putExtra("teacherName", teacher);
                intent.putExtra("class_name", class_name);
                intent.putExtra("info", info);
                intent.putExtra("view", true);
                startActivity(intent);
            }
        });
        session = SessionManager.getInstance();
        user = session.getUser();

        readDbTeacher();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student_book_list, menu);
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
     * read class table from db
     */
    private void readDbTeacher() {
        // Tag used to cancel the request
        String tag_string_req = "req_readDbTeacher";

        pDialog.setMessage("Lese Lehrer ...");
        showDialog();

        String url = AppConfig.URL_TEACHER_GET_ALL;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Teacher Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    // Check for error node in json
                    if (success == 1) {
                        // teacher data
                        JSONArray teacherData = jObj.getJSONArray("teacher");
                        for (int i = 0; i < teacherData.length(); i++) {
                            JSONObject c = teacherData.getJSONObject(i);
                            int teacher_id = c.getInt("teacher_id");
                            String first_name = c.getString("first_name");
                            String last_name = c.getString("last_name");
                            teacherMap.put(teacher_id, first_name + " " + last_name);
                        }
                        readDbBook(user);
                    } else {
                        // Error in get all. Get the error message
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
     * read book table from db
     */
    private void readDbBook(final User user) {
        // Tag used to cancel the request
        String tag_string_req = "req_readDbBook";

        pDialog.setMessage("Lese Einträge ...");
        showDialog();

        String url = AppConfig.URL_BOOK_GET_BY_CLASS;

        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Book Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    // Check for error node in json
                    if (success == 1) {
                        // book data
                        JSONArray classData = jObj.getJSONArray("book");
                        for (int i = 0; i < classData.length(); i++) {
                            JSONObject c = classData.getJSONObject(i);
                            int book_id = c.getInt("book_id");
                            Date date = AppConfig.formatter.parse(c.getString("date"));
                            String subject = c.getString("subject");
                            int teacher = c.getInt("teacher");
                            String class_name = c.getString("class");
                            String info = c.getString("info");

                            HashMap<String, String> map = new HashMap<>();
                            map.put(from[0], AppConfig.formatter.format(date));
                            map.put(from[1], subject);
                            map.put(from[2], teacherMap.get(teacher));
                            map.put(from[3], class_name);
                            map.put(from[4], info);
                            bookList.add(map);

                            // fill subject list for spinner
                            subjectList.add(subject);
                        }
                        fillListView();
                        fillSpinnerList();
                    } else {
                        // Error in get by class. Get the error message
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
                params.put("class", user.getClassName());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * fill the data in the listview_three_column layout
     */
    private void fillListView() {
        SimpleAdapter adapter = new SimpleAdapter(this, bookList, R.layout.listview_three_column, from, to);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    /**
     * fill the filtered data in the listview_three_column layout
     */
    private void fillListViewFiltered(String subjectFilter) {
        // Clear filtered List
        filteredBookList.clear();

        // Fill filtered List with entries equals subject Filter
        Iterator iterator = bookList.iterator();
        while (iterator.hasNext()) {
            HashMap<String, String> map = (HashMap) iterator.next();
            if (map.get(from[1]).equals(subjectFilter)) {
                filteredBookList.add(map);
            }
        }

        SimpleAdapter adapter = new SimpleAdapter(this, filteredBookList, R.layout.listview_three_column, from, to);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    private void fillSpinnerList() {
        List<String> list = new ArrayList<>();
        list.add("Alle");
        list.addAll(subjectList);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_listview, list);
        adapter.setDropDownViewResource(R.layout.simple_listview);
        filterSubjectSpinner.setAdapter(adapter);
        filterSubjectSpinner.setOnItemSelectedListener(this);
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
        String subjectFilter = parent.getItemAtPosition(position).toString();

        if ("Alle".equals(subjectFilter)) {
            fillListView();
        } else {
            fillListViewFiltered(subjectFilter);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
