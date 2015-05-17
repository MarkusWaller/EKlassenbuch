<?php
 
/*
 * Following code will update a admin information
 * A admin is identified by admin id (admin_id)
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['admin_id']) && isset($_POST['first_name']) && isset($_POST['last_name']) && isset($_POST['password']) && isset($_POST['email'])) {
 
    $admin_id = $_POST['admin_id'];
    $first_name = $_POST['first_name'];
    $last_name = $_POST['last_name'];
	$password = $_POST['password'];
	$email = $_POST['email'];
 
    // include db connect class
    require_once __DIR__ . '/../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched admin_id
    $result = mysql_query("UPDATE admin SET first_name = '$first_name', last_name = '$last_name', password = '$password', email = '$email' WHERE admin_id = $admin_id");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "admin successfully updated.";
 
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