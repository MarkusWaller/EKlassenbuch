<?php

/*
 * Der folgende Code aktualisiert einen Eintrag in der Tabelle "book".
 * Der Eintrag ist definiert mit der id (book_id)
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['book_id']) && isset($_POST['date']) && isset($_POST['subject']) && isset($_POST['teacher']) && isset($_POST['class']) && isset($_POST['info'])) {

    $book_id = $_POST['book_id'];
    $date = $_POST['date'];
    $subject = $_POST['subject'];
    $teacher = $_POST['teacher'];
    $class = $_POST['class'];
    $info = $_POST['info'];

    // include db connect class
    require_once __DIR__ . '/../db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql update row with matched book_id
    $result = mysql_query("UPDATE book SET date = '$date', subject = '$subject', teacher = '$teacher', class = '$class', info = '$info'  WHERE book_id = $book_id");

    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Eintrag erfolgreich aktualisiert.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Nicht alle erforderlichen Parameter vorhanden.";

    // echoing JSON response
    echo json_encode($response);
}
?>