
<?php
 
/*
 * Der folgende code löscht ein objekt
 * Das objekt ist definiert mit der id (book_id)
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['book_id'])) {
    $book_id = $_POST['book_id'];
 
    // include db connect class
    require_once __DIR__ . '/../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched pid
    $result = mysql_query("DELETE FROM book WHERE book_id = '$book_id'");
 
    // check if row deleted or not
    if (mysql_affected_rows() > 0) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "book successfully deleted";
 
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