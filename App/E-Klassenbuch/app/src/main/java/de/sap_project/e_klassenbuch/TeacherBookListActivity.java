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

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.sap_project.e_klassenbuch.data.User;
import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.AppController;
import de.sap_project.e_klassenbuch.db.SessionManager;


public class TeacherBookListActivity extends ActionBarActivity {
    // LogCat tag
    private static final String TAG = TeacherBookListActivity.class.getSimpleName();

    private SessionManager session;
    private ProgressDialog pDialog;
    private ListView listView;
    private User user;
    private HashMap<Integer, String> teacherMap = new HashMap<>();
    private List<HashMap<String, String>> bookList = new ArrayList<>();
    private String[] from = new String[]{"col_1", "col_2", "col3", "teacher", "info", "book_id"};
    private int[] to = new int[]{R.id.textViewCol1, R.id.textViewCol2, R.id.textViewCol3};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_book_list);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        listView = (ListView) findViewById(R.id.listViewTeacherBook);

        session = SessionManager.getInstance();
        user = session.getUser();

        Log.d(TAG, "onCreate()");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        readDbTeacher();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId() == R.id.listViewTeacherBook) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            menu.setHeaderTitle(bookList.get(info.position).get(from[0]));
            String[] menuItems = getResources().getStringArray(R.array.teacher_book_context_array);
            for (int i = 0; i < menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int menuItemIndex = item.getItemId();
        String[] menuItems = getResources().getStringArray(R.array.teacher_book_context_array);
        String menuItemName = menuItems[menuItemIndex];

        String date = bookList.get(menuInfo.position).get(from[0]);
        String subject = bookList.get(menuInfo.position).get(from[1]);
        String class_name = bookList.get(menuInfo.position).get(from[2]);
        String teacher = bookList.get(menuInfo.position).get(from[3]);
        String info = bookList.get(menuInfo.position).get(from[4]);
        String book_id = bookList.get(menuInfo.position).get(from[5]);

        switch (menuItemName) {
            case "Ansehen":
                // Launch EditBook activity
                Intent intent = new Intent(TeacherBookListActivity.this, EditBookActivity.class);
                intent.putExtra("date", date);
                intent.putExtra("subject", subject);
                intent.putExtra("teacherName", teacher);
                intent.putExtra("class_name", class_name);
                intent.putExtra("info", info);
                intent.putExtra("view", true);
                startActivity(intent);
                break;
            case "Löschen":
                deleteBook(book_id);
                break;
            case "Ändern":
                // Launch EditBook activity
                Intent intentEdit = new Intent(TeacherBookListActivity.this, EditBookActivity.class);
                intentEdit.putExtra("date", date);
                intentEdit.putExtra("subject", subject);
                intentEdit.putExtra("teacherName", teacher);
                intentEdit.putExtra("class_name", class_name);
                intentEdit.putExtra("book_id", book_id);
                intentEdit.putExtra("teacher_id", user.getId());
                intentEdit.putExtra("info", info);
                intentEdit.putExtra("edit", true);
                startActivity(intentEdit);
                break;
        }
        return super.onContextItemSelected(item);
    }

    private void deleteBook(final String book_id) {
        // Tag used to cancel the request
        String tag_string_req = "req_deleteBook";

        pDialog.setMessage("Lösche Eintrag ...");
        showDialog();
        String url = AppConfig.URL_BOOK_DELETE;
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
                        // book successfully deleted
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
                params.put("book_id", book_id);
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_teacher_book_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_addBook) {
            // Launch EditClass activity
            Intent intent = new Intent(TeacherBookListActivity.this, EditBookActivity.class);
            intent.putExtra("teacherName", user.getFirstName() + user.getLastName());
            intent.putExtra("teacher_id", user.getId());

            startActivity(intent);
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

        bookList.clear();

        String url = AppConfig.URL_BOOK_GET_BY_TEACHER;

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
                            Integer book_id = c.getInt("book_id");
                            Date date = AppConfig.formatter.parse(c.getString("date"));
                            String subject = c.getString("subject");
                            int teacher = c.getInt("teacher");
                            String class_name = c.getString("class");
                            String info = c.getString("info");

                            HashMap<String, String> map = new HashMap<>();
                            map.put(from[0], AppConfig.formatter.format(date));
                            map.put(from[1], subject);
                            map.put(from[2], class_name);
                            map.put(from[3], teacherMap.get(teacher));
                            map.put(from[4], info);
                            map.put(from[5], book_id.toString());
                            bookList.add(map);
                        }
                        fillListView();
                    } else {
                        // Error in get by teacher. Get the error message
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
                // Posting parameters to get by teacher url
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
        SimpleAdapter adapter = new SimpleAdapter(this, bookList, R.layout.listview_three_column, from, to);
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
