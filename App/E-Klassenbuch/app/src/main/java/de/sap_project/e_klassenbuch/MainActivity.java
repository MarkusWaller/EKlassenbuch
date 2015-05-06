package de.sap_project.e_klassenbuch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
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
        setContentView(R.layout.activity_main);
        TextView txtTeacher = (TextView) findViewById(R.id.textViewIsTeacher);
        TextView txtName = (TextView) findViewById(R.id.textViewName);
        TextView txtEmail = (TextView) findViewById(R.id.textViewEmail);
        TextView txtDate = (TextView) findViewById(R.id.textViewDate);
        Button btnTeacherClass = (Button) findViewById(R.id.buttonTeacherClass);
        Button btnLogout = (Button) findViewById(R.id.buttonLogOut);

        // session manager
        session = SessionManager.getInstance();

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from SessionManager
        User user = session.getUser();

        // Displaying the user details on the screen
        txtTeacher.setText((user.getIsTeacher()) ? "Lehrer" : "Schüler");
        txtName.setText(user.getFirstName() + " " + user.getLastName());
        txtEmail.setText(user.getEmail());
        if (null != user.getBirthDate()) {
            txtDate.setText(AppConfig.formatter.format(user.getBirthDate()));
        }

        if(user.getIsTeacher()){
            btnTeacherClass.setVisibility(View.VISIBLE);
        }else{
            btnTeacherClass.setVisibility(View.INVISIBLE);
        }

        // Teacher Class button click event
        btnTeacherClass.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), TeacherClass.class);
                startActivity(i);
                finish();
            }
        });

        // Logout button click event
        btnLogout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });
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
