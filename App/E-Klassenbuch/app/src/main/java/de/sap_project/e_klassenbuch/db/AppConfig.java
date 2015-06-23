package de.sap_project.e_klassenbuch.db;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * Application configuration variables
 * <p/>
 * Created by Markus on 24.04.2015.
 */
public class AppConfig {
    public static String URL_PRAEFIX = "http://markus.fam-waller.de/ecb";
    //public static String URL_PRAEFIX = "http://192.168.178.30/ecb";

    // Server Student urls
    public static String URL_STUDENT_LOGIN = URL_PRAEFIX + "/student/get_by_email.php";
    public static String URL_STUDENT_REGISTER = URL_PRAEFIX + "/student/create.php";
    //Server Teacher urls
    public static String URL_TEACHER_LOGIN = URL_PRAEFIX + "/teacher/get_by_email.php";
    public static String URL_TEACHER_REGISTER = URL_PRAEFIX + "/teacher/create.php";
    public static String URL_TEACHER_GET_ALL = URL_PRAEFIX + "/teacher/get_all.php";
    //server admin urls
    public static String URL_ADMIN_LOGIN = URL_PRAEFIX + "/admin/get_by_email.php";
    public static String URL_ADMIN_REGISTER = URL_PRAEFIX + "/admin/create.php";
    //server class urls
    public static String URL_CLASS_GET_ALL = URL_PRAEFIX + "/class/get_all.php";
    public static String URL_CLASS_CREATE = URL_PRAEFIX + "/class/create.php";
    public static String URL_CLASS_DELETE = URL_PRAEFIX + "/class/delete.php";
    public static String URL_CLASS_UPDATE = URL_PRAEFIX + "/class/update.php";

    //server book urls
    public static String URL_BOOK_GET_ALL = URL_PRAEFIX + "/book/get_all.php";
    public static String URL_BOOK_CREATE = URL_PRAEFIX + "/book/create.php";
    public static String URL_BOOK_GET_BY_CLASS = URL_PRAEFIX + "/book/get_by_class.php";
    public static String URL_BOOK_GET_BY_TEACHER = URL_PRAEFIX + "/book/get_by_teacher.php";
    public static String URL_BOOK_UPDATE = URL_PRAEFIX + "/book/update.php";
    public static String URL_BOOK_DELETE = URL_PRAEFIX + "/book/delete.php";
    public static String URL_BOOK_GET_SUBJECT_CLASS_BY_TEACHER = URL_PRAEFIX + "/book/get_subject_class_by_teacher";

    // Date Format for Database
    public static final DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");

    public enum UserType {
        STUDENT("Schüler"),
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
