<?php

/*
 * Der folgende Code liefert alle Eintr�ge der Tabelle "book" f�r einen Lehrer.
 */

// array for JSON response
$response = array();

// include db connect class
require_once __DIR__ . '/../db_connect.php';

// connecting to db
$db = new DB_CONNECT();

// check for post data
if (isset($_POST["teacher"])) {
    $teacher = $_POST['teacher'];

// get all book from book table
    $result = mysql_query("SELECT * FROM book WHERE teacher = '$teacher'") or die(mysql_error());

// check for empty result
    if (mysql_num_rows($result) > 0) {
        // looping through all results
        // book node
        $response["book"] = array();

        while ($row = mysql_fetch_array($result)) {
            // temp user array
            $book = array();
            $book["book_id"] = $row["book_id"];
            $book["date"] = $row["date"];
            $book["subject"] = $row["subject"];
            $book["teacher"] = $row["teacher"];
            $book["class"] = $row["class"];
            $book["info"] = $row["info"];

            // push single book into final response array
            array_push($response["book"], $book);
        }
        // success
        $response["success"] = 1;

        // echoing JSON response
        echo json_encode($response);
    } else {
        // no book found
        $response["success"] = 0;
        $response["message"] = "Kein Eintrag gefunden.";

        // echo no users JSON
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