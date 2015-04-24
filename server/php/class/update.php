
<?php
 
/*
 * Following code will update a class information
 * A class is identified by name 
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['h_teacher']) && isset($_POST['name']) ) {
 
    $h_teacher = $_POST['h_teacher'];
    $name = $_POST['name'];


 
    // include db connect class
    require_once __DIR__ . '/../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched pid
    $result = mysql_query("UPDATE class SET h_teacher = '$h_teacher' WHERE name = '$name'");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "class successfully updated.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
 
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Required field(s) is missing";
 
    // echoing JSON response
    echo json_encode($response);
}
?>