<?php
 
/*
 * Following code will get single book details
 * A book is identified by book_id (book_id)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/../db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();

// check for post data
if (isset($_POST["class"])) {
    $class = $_POST['class'];
 
// get all book from book table
$result = mysql_query("SELECT * FROM book WHERE class = '$class'") or die(mysql_error());
 
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
    $response["message"] = "No book found";
 
    // echo no users JSON
    echo json_encode($response);
}
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>