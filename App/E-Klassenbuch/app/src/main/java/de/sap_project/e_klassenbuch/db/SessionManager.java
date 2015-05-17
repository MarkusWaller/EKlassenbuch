package de.sap_project.e_klassenbuch.db;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import java.text.ParseException;

import de.sap_project.e_klassenbuch.data.User;

/**
 * notice the last login user
 * <p/>
 * Created by Markus on 24.04.2015.
 */
public class SessionManager {
    // LogCat tag
    private static String TAG = SessionManager.class.getSimpleName();

    // Singleton pattern
    private static final SessionManager SESSION_MANAGER = new SessionManager();

    // Shared Preferences
    SharedPreferences pref;

    Editor editor;
    Context _context;
    private User user;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private SessionManager() {
        Log.d(TAG, "SessionManager created as Singleton ...");
        this._context = AppController.getInstance().getApplicationContext();
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public static SessionManager getInstance() {
        return SESSION_MANAGER;
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public User getUser() {
        if (user == null) {
            try {
                user = new User(pref.getInt("UserId", 0), pref.getString("UserFirstName", "UserFirstName"),
                        pref.getString("UserLastName", "UserLastName"),
                        pref.getString("UserEmail", "UserEmail"),
                        pref.getString("UserPassword", "UserPassword"),
                        AppConfig.UserType.valueOf(pref.getString("UserType", AppConfig.UserType.STUDENT.name())),
                        pref.getString("UserClass", "UserClass"),
                        AppConfig.formatter.parse(pref.getString("UserBirthDate", "0000-00-00"))
                );
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return user;
    }

    public void setUser(User user) {
        this.user = user;

        //save User in Preferences
        editor.putInt("UserId", user.getId());
        editor.putString("UserFirstName", user.getFirstName());
        editor.putString("UserLastName", user.getLastName());
        editor.putString("UserPassword", user.getPassword());
        editor.putString("UserEmail", user.getEmail());
        editor.putString("UserType", user.getUserType().name());
        editor.putString("UserClass", user.getClassName());
        if (null != user.getBirthDate()) {
            editor.putString("UserBirthDate", AppConfig.formatter.format(user.getBirthDate()));
        }
        // commit changes
        editor.commit();
    }
}
