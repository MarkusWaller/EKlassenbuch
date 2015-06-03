package de.sap_project.e_klassenbuch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import de.sap_project.e_klassenbuch.data.User;
import de.sap_project.e_klassenbuch.db.AppConfig;
import de.sap_project.e_klassenbuch.db.SessionManager;

/**
 * Main Activity
 * <p/>
 * Created by Markus
 */
public class MainActivity extends ActionBarActivity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // session manager
        session = SessionManager.getInstance();

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SessionManager
        User user = session.getUser();
        switch (user.getUserType()) {
            case STUDENT:
                setContentView(R.layout.activity_main);
                fillTextViews(user);
                TextView txtDate = (TextView) findViewById(R.id.textViewDate);
                txtDate.setText(AppConfig.formatter.format(user.getBirthDate()));
                break;
            case TEACHER:
                setContentView(R.layout.activity_main_teacher);
                fillTextViews(user);
                break;
            case ADMIN:
                setContentView(R.layout.activity_main_admin);
                fillTextViews(user);
                break;
            default:
                break;
        }
    }

    private void fillTextViews(User user) {
        TextView txtUserType = (TextView) findViewById(R.id.textViewUserType);
        TextView txtName = (TextView) findViewById(R.id.textViewName);
        TextView txtEmail = (TextView) findViewById(R.id.textViewEmail);
        // Displaying the user details on the screen
        txtUserType.setText(user.getUserType().toString());
        txtName.setText(user.getFirstName() + " " + user.getLastName());
        txtEmail.setText(user.getEmail());
    }

    public void onClickTeacher(View view) {
        Intent i = new Intent(getApplicationContext(), TeacherClassActivity.class);
        startActivity(i);
    }

    public void onClickAdmin(View view) {
        Intent i = new Intent(getApplicationContext(), AdminClassActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     */
    private void logoutUser() {
        session.setLogin(false);

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
