package de.sap_project.e_klassenbuch.data;

import java.util.Date;

import de.sap_project.e_klassenbuch.db.AppConfig;

/**
 * User data object. Used for all user types, admin, teacher and student.
 * <p/>
 * Created by Markus on 01.05.2015.
 */
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private Integer id;
    private String className;
    private Date birthDate;
    private AppConfig.UserType userType;

    /**
     * Constructor for the User object with all necessary parameters.
     *
     * @param id        The user id
     * @param firstName First name of the user
     * @param lastName  Last name of the user
     * @param email     Email address of the user
     * @param password  The users password
     * @param userType  The user type
     * @param className The class name of the user (only for students)
     * @param birthDate The birthdate of the user (only for students)
     */
    public User(Integer id, String firstName, String lastName, String email, String password, AppConfig.UserType userType, String className, Date birthDate) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.userType = userType;
        this.className = className;
        this.birthDate = birthDate;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AppConfig.UserType getUserType() {

        return userType;
    }

    public void setUserType(AppConfig.UserType userType) {
        this.userType = userType;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }
}
