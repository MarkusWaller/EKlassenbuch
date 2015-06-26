<?php

/*
 * Der folgende Code liefert alle Eintraege aus der Tabelle "admin".
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/../db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// get all admin from admin table
$result = mysql_query("SELECT * FROM admin") or die(mysql_error());

// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // admin node
    $response["admin"] = array();

    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $admin = array();
        $admin["admin_id"] = $row["admin_id"];
        $admin["first_name"] = $row["first_name"];
        $admin["last_name"] = $row["last_name"];
        $admin["email"] = $row["email"];
        $admin["password"] = $row["password"];

        // push single admin into final response array
        array_push($response["admin"], $admin);
    }
    // success
    $response["success"] = 1;

    // echoing JSON response
    echo json_encode($response);
} else {
    // no admin found
    $response["success"] = 0;
    $response["message"] = "Kein Admin gefunden.";

    // echo no users JSON
    echo json_encode($response);
}
?>