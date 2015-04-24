<?php
 
/*
 * Following code will get single book details
 * A book is identified by book_id 
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/../db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["book_id"])) {
    $book_id = $_GET['book_id'];
	
    // get a book from book table
    $result = mysql_query("SELECT * FROM book WHERE book_id = '$book_id'");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $book = array();
            $book["book_id"] = $result["book_id"];
			$book["date"] = $result["date"];
            $book["subject"] = $result["subject"];
            $book["teacher"] = $result["teacher"];
			$book["info"] = $result["info"];

 
            // success
            $response["success"] = 1;
 
            // user node
            $response["book"] = array();
 
            array_push($response["book"], $book);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no book found
            $response["success"] = 0;
            $response["message"] = "Keinen Eintrag gefunden.";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no book found
        $response["success"] = 0;
        $response["message"] = "Keinen Eintrag gefunden.";
 
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