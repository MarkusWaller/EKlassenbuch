package de.sap_project.e_klassenbuch.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Application configuration variables
 * <p/>
 * Created by Markus on 24.04.2015.
 */
public class AppConfig {
    // public static String URL_PRAEFIX = "http://markus.fam-waller.de/ecb";
    public static String URL_PRAEFIX = "http://192.168.178.30/ecb";

    // Server user login url
    public static String URL_STUDENT_LOGIN = URL_PRAEFIX + "/student/get_by_email.php";
    public static String URL_TEACHER_LOGIN = URL_PRAEFIX + "/teacher/get_by_email.php";
    public static String URL_ADMIN_LOGIN = URL_PRAEFIX + "/admin/get_by_email.php";

    // Server user register url
    public static String URL_STUDENT_REGISTER = URL_PRAEFIX + "/student/create.php";
    public static String URL_TEACHER_REGISTER = URL_PRAEFIX + "/teacher/create.php";
    public static String URL_ADMIN_REGISTER = URL_PRAEFIX + "/admin/create.php";

    public static String URL_CLASS_GET_ALL = URL_PRAEFIX + "/class/get_all.php";
    public static String URL_BOOK_GET_ALL = URL_PRAEFIX + "/book/get_all.php";

    // Date Format for Database
    public static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public enum UserType {
        STUDENT("Sch\u00fcler"),
        TEACHER("Lehrer"),
        ADMIN("Admin");

        private final String text;

        private UserType(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }
}
