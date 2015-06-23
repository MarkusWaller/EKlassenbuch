package de.sap_project.e_klassenbuch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

import de.sap_project.e_klassenbuch.data.User;
import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.AppController;
import de.sap_project.e_klassenbuch.db.SessionManager;


public class TeacherClassActivity extends ActionBarActivity {
    // LogCat tag
    private static final String TAG = TeacherClassActivity.class.getSimpleName();

    private SessionManager session;
    private ProgressDialog pDialog;
    private TextView txtClass;
    private ListView listView;
    private List<HashMap<String, String>> classList = new ArrayList<>();
    private String[] from = new String[]{"col_1", "col_2"};
    private int[] to = new int[]{R.id.textViewCol1, R.id.textViewCol2};
    private String teacherName;
    private int teacher_id;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_class);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        TextView txtName = (TextView) findViewById(R.id.textViewTeachClassName);
        txtClass = (TextView) findViewById(R.id.textViewHTeacher);
        listView = (ListView) findViewById(R.id.listView);

        session = SessionManager.getInstance();

        user = session.getUser();
        teacherName = user.getFirstName() + " " + user.getLastName();
        txtName.setText(teacherName);
        teacher_id = user.getId();
    }

    @Override
    protected void onStart(){
        super.onStart();
        Log.d(TAG, "onStart");

        readDbClass(user);
        readDbBook(user);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher_class, menu);
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

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listView) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(classList.get(info.position).get(from[0]));
            String[] menuItems = getResources().getStringArray(R.array.teacher_class_context_array);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.teacher_class_context_array);
        String menuItemName = menuItems[menuItemIndex];
        String class_name = classList.get(info.position).get(from[0]);
        String subject = classList.get(info.position).get(from[1]);

        switch (menuItemName) {
            case "Neuer Eintrag":
                // Launch EditClass activity
                Intent intent = new Intent(TeacherClassActivity.this, EditBookActivity.class);
                intent.putExtra("class_name", class_name);
                intent.putExtra("subject", subject);
                intent.putExtra("teacherName", teacherName);
                intent.putExtra("teacher_id", teacher_id);

                startActivity(intent);
                break;
        }

        return super.onContextItemSelected(item);
    }

    /**
     * read class table from db
     */
    private void readDbClass(final User user) {
        // Tag used to cancel the request
        String tag_string_req = "req_readDbClass";

        pDialog.setMessage("Lese Klassen ...");
        showDialog();

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
                        // class data
                        JSONArray classData = jObj.getJSONArray("class");
                        for (int i = 0; i < classData.length(); i++) {
                            JSONObject c = classData.getJSONObject(i);
                            String name = c.getString("name");
                            int h_teacher = c.getInt("h_teacher");
                            if (h_teacher == user.getId()) {
                                user.setClassName(name);
                                txtClass.setText(user.getClassName());
                            }
                        }
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

        classList.clear();

        String url = AppConfig.URL_BOOK_GET_SUBJECT_CLASS_BY_TEACHER;

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
                            String subject = c.getString("subject");
                            String class_name = c.getString("class");

                            HashMap<String, String> map = new HashMap<>();
                            map.put(from[0], class_name);
                            map.put(from[1], subject);
                            classList.add(map);
                        }
                        fillListView();
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
                params.put("teacher", user.getId().toString());
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    /**
     * fill the data in the listview_two_column layout
     */
    private void fillListView() {
        SimpleAdapter adapter = new SimpleAdapter(this, classList, R.layout.listview_two_column, from, to);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
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
