<?php

/*
 * Der folgende Code erstellt eine Zeile in der Tabelle "teacher".
 * Alle Informationen werden vom http Post Request gelesen
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['last_name']) && isset($_POST['first_name']) && isset($_POST['email']) && isset($_POST['password'])) {

    $last_name = $_POST['last_name'];
    $first_name = $_POST['first_name'];
    $email = $_POST['email'];
    $password = $_POST['password'];

    // include db connect class
    require_once __DIR__ . '/../db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql inserting a new row
    $result = mysql_query("INSERT INTO teacher(last_name, first_name, email, password) VALUES('$last_name', '$first_name', '$email', '$password')");

    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Lehrer erfolgreich angelegt.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! Es ist ein Fehler aufgetreten.";

        // echoing JSON response
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Nicht alle erforderlichen Parameter vorhanden.";

    // echoing JSON response
    echo json_encode($response);
}
?>