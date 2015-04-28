package de.sap_project.e_klassenbuch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

import de.sap_project.e_klassenbuch.db.SessionManager;

/**
 * Main Activity
 *
 * Created by Markus
 */
public class MainActivity extends Activity {

    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView txtName = (TextView) findViewById(R.id.textViewName);
        TextView txtEmail = (TextView) findViewById(R.id.textViewEmail);
        Button btnLogout = (Button) findViewById(R.id.button);

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from ?
        //HashMap<String, String> user = db.getUserDetails();

        //String name = user.get("name");
        //String email = user.get("email");

        // Displaying the user details on the screen
        //txtName.setText(name);
        //txtEmail.setText(email);

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
     * */
    private void logoutUser() {
        session.setLogin(false);

        //db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
