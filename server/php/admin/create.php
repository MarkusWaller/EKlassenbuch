<?php
 
/*
 * Following code will create a new admin row
 * All admin details are read from HTTP Post Request
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['first_name']) && isset($_POST['last_name']) && isset($_POST['password']) && isset($_POST['email'])) {
 
    $first_name = $_POST['first_name'];
    $last_name = $_POST['last_name'];
	$password = $_POST['password'];
	$email = $_POST['email'];
 
    // include db connect class
    require_once __DIR__ . '/../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql inserting a new row
    $result = mysql_query("INSERT INTO admin(first_name, last_name, email, password) VALUES('$first_name', '$last_name', '$email', '$password')");
 
    // check if row inserted or not
    if ($result) {
        // successfully inserted into database
        $response["success"] = 1;
        $response["message"] = "Admin successfully created.";
 
        // echoing JSON response
        echo json_encode($response);
    } else {
        // failed to insert row
        $response["success"] = 0;
        $response["message"] = "Oops! An error occurred.";
 
        // echoing JSON response
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