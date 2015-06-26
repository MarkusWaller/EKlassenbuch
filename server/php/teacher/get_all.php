<?php

/*
 * Der folgende Code liefert alle Eintraege aus der Tabelle "teacher".
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/../db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// get all teacher from teacher table
$result = mysql_query("SELECT * FROM teacher") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // teacher node
    $response["teacher"] = array();

    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $teacher = array();
        $teacher["teacher_id"] = $row["teacher_id"];
        $teacher["last_name"] = $row["last_name"];
        $teacher["first_name"] = $row["first_name"];
        $teacher["email"] = $row["email"];
        $teacher["password"] = $row["password"];

        // push single teacher into final response array
        array_push($response["teacher"], $teacher);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no teacher found
    $response["success"] = 0;
    $response["message"] = "Kein Lehrer gefunden.";

    // echo no users JSON
    echo json_encode($response);
}
?>