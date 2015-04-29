<?php
 
/*
 * Der folgende code erstellt eine Zeile in der Tabelle student
 * Alle Informationen werden vom http Post Request gelesen
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['first_name']) && isset($_POST['last_name']) && isset($_POST['class']) && isset($_POST['birth_date']) && isset($_POST['email']) && isset($_POST['password'])) {
 
    $first_name = $_POST['first_name'];
	$last_name = $_POST['last_name'];
    $class = $_POST['class'];
	$birth_date = $_POST['birth_date'];
    $email = $_POST['email'];
	$password = $_POST['password'];
 
    // include db connect class
    require_once __DIR__ . '/../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO student(first_name, last_name, class, birth_date, email, password) VALUES('$first_name', '$last_name', '$class', '$birth_date', '$email', '$password')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Schüler erfolgreich angelegt.";
 
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