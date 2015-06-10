<?php
 
/*
 * Der folgende code erstellt eine Zeile in der Tabelle book
 * Alle Informationen werden vom http Post Request gelesen
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['date']) && isset($_POST['subject']) && isset($_POST['teacher']) && isset($_POST['class']) && isset($_POST['info'])) {
 
    $date = $_POST['date'];
	$subject = $_POST['subject'];
    $teacher = $_POST['teacher'];
	$class = $_POST['class'];
	$info = $_POST['info'];

 
    // include db connect book
    require_once __DIR__ . '/../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO book(date, subject, teacher, class, info) VALUES('$date', '$subject', '$teacher', '$class', '$info')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Eintrag erfolgreich angelegt.";
 
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