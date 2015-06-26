<?php

/*
 * Der folgende Code liefert alle Eintraege aus der Tabelle "student".
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/../db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// get all student from student table
$result = mysql_query("SELECT * FROM student") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // student node
    $response["student"] = array();

    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $student = array();
        $student["student_id"] = $row["student_id"];
        $student["first_name"] = $row["first_name"];
        $student["last_name"] = $row["last_name"];
        $student["class"] = $row["class"];
        $student["birth_date"] = $row["birth_date"];
        $student["email"] = $row["email"];
        $student["password"] = $row["password"];

        // push single student into final response array
        array_push($response["student"], $student);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no student found
    $response["success"] = 0;
    $response["message"] = "Kein Schueler gefunden.";

    // echo no users JSON
    echo json_encode($response);
}
?>