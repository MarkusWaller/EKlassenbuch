<?php
 
/*
 * Der folgende code erstellt eine Zeile in der Tabelle class
 * Alle Informationen werden vom http Post Request gelesen
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['name']) && isset($_POST['h_teacher'])) {
 
    $name = $_POST['name'];
    $h_teacher = $_POST['h_teacher'];
 
    // include db connect class
    require_once __DIR__ . '/../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO class(name, h_teacher) VALUES('$name', '$h_teacher')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Klasse erfolgreich angelegt.";
 
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