
<?php
 
/*
 * Following code will list all the class
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/../db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// get all class from class table
$result = mysql_query("SELECT *FROM class") or die(mysql_error());
 
// check for empty result
if (mysql_num_rows($result) > 0) {
    // looping through all results
    // class node
    $response["class"] = array();
 
    while ($row = mysql_fetch_array($result)) {
        // temp user array
        $class = array();
        $class["name"] = $row["name"];
        $class["h_teacher"] = $row["h_teacher"];
 
        // push single class into final response array
        array_push($response["class"], $class);
    }
    // success
    $response["success"] = 1;
 
    // echoing JSON response
    echo json_encode($response);
} else {
    // no class found
    $response["success"] = 0;
    $response["message"] = "No class found";
 
    // echo no users JSON
    echo json_encode($response);
}
?>