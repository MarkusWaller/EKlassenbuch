package de.sap_project.e_klassenbuch;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Register Activity
 *
 * Created by Markus
 */
public class RegisterActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        EditText regEmail = (EditText) findViewById(R.id.reg_email);
        EditText regFirst = (EditText) findViewById(R.id.reg_first_name);
        EditText regLast = (EditText) findViewById(R.id.reg_last_name);
        EditText regDate = (EditText) findViewById(R.id.reg_date);
        EditText regPwd = (EditText) findViewById(R.id.reg_password);

        Button btnRegister = (Button) findViewById(R.id.button_register);
        Button btnLinkToLogin = (Button) findViewById(R.id.button_link_to_login);

        // Link to Login Screen
        btnLinkToLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_regristrierung, menu);
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
}
