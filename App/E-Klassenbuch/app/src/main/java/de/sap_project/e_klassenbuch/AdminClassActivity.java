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

import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.AppController;

/**
 * Activity for user admin to add, change and delete classes.
 *
 * Created by Markus on 03.06.2015.
 */
public class AdminClassActivity extends ActionBarActivity {
    // LogCat tag
    private static final String TAG = AdminClassActivity.class.getSimpleName();

    private ProgressDialog pDialog;
    private ListView listView;
    private List<HashMap<String, String>> listMap = new ArrayList<>();
    private HashMap<Integer, String> teacherMap = new HashMap<>();
    private String[] from = new String[]{"col_1", "col_2"};
    private int[] to = new int[]{R.id.textViewCol1, R.id.textViewCol2};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_class);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        listView = (ListView) findViewById(R.id.listViewAdminClass);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        readDbTeacher();
    }

    /**
     * Read 'teacher' table from database.
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
                        // Class data
                        JSONArray teacherData = jObj.getJSONArray("teacher");
                        for (int i = 0; i < teacherData.length(); i++) {
                            JSONObject c = teacherData.getJSONObject(i);
                            int teacher_id = c.getInt("teacher_id");
                            String first_name = c.getString("first_name");
                            String last_name = c.getString("last_name");
                            teacherMap.put(teacher_id, first_name + " " + last_name);
                        }
                        readDbClass();
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
     * Read 'class' table from database.
     */
    private void readDbClass() {
        // Tag used to cancel the request
        String tag_string_req = "req_readDbClass";

        pDialog.setMessage("Lese Klassen ...");
        showDialog();

        listMap.clear();

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
                            int h_teacher = c.getInt("h_teacher");

                            HashMap<String, String> map = new HashMap<>();
                            map.put(from[0], name);
                            map.put(from[1], teacherMap.get(h_teacher));
                            listMap.add(map);
                        }
                        fillListView();
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
     * Fill the data in the listview_two_column layout.
     */
    private void fillListView() {
        SimpleAdapter adapter = new SimpleAdapter(this, listMap, R.layout.listview_two_column, from, to);
        listView.setAdapter(adapter);
        registerForContextMenu(listView);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listViewAdminClass) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(listMap.get(info.position).get(from[0]));
            String[] menuItems = getResources().getStringArray(R.array.admin_class_context_array);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.admin_class_context_array);
        String menuItemName = menuItems[menuItemIndex];
        String className = listMap.get(info.position).get(from[0]);
        String h_teacherName = listMap.get(info.position).get(from[1]);

        switch (menuItemName) {
            case "Löschen":
                deleteClass(className);
                break;
            case "Ändern":
                // Launch EditClass activity
                Intent intent = new Intent(AdminClassActivity.this, EditClassActivity.class);
                intent.putExtra("teacherMap", teacherMap);
                intent.putExtra("className", className);
                intent.putExtra("h_teacherName", h_teacherName);
                intent.putExtra("edit", true);
                startActivity(intent);
                break;
        }

        return super.onContextItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_admin_class, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addClass) {
            // Launch EditClass activity
            Intent intent = new Intent(AdminClassActivity.this, EditClassActivity.class);
            intent.putExtra("teacherMap", teacherMap);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Delete the given class from the table 'class' in the database.
     *
     * @param class_name Name of the class to delete
     */
    private void deleteClass(final String class_name) {
        // Tag used to cancel the request
        String tag_string_req = "req_deleteClass";

        pDialog.setMessage("Lösche Klasse ...");
        showDialog();
        String url = AppConfig.URL_CLASS_DELETE;
        StringRequest strReq = new StringRequest(Request.Method.POST,
                url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Delete Response: " + response);
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    int success = jObj.getInt("success");

                    // Check for error node in json
                    if (success == 1) {
                        // class successfully deleted
                        String successMsg = jObj.getString("message");
                        Toast.makeText(getApplicationContext(),
                                successMsg, Toast.LENGTH_LONG).show();
                        readDbTeacher();
                    } else {
                        // Error in delete. Get the error message
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
                Log.e(TAG, "Delete Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();

            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to delete url
                Map<String, String> params = new HashMap<>();
                params.put("name", class_name);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
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
}
