<?php
 
/*
 * Following code will get single admin details
 * A admin is identified by admin id (admin_id)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/../db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["admin_id"])) {
    $admin_id = $_GET['admin_id'];
 
    // get a admin from admins table
    $result = mysql_query("SELECT *FROM admin WHERE admin_id = $admin_id");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $admin = array();
			$admin["admin_id"] = $result["admin_id"];
            $admin["first_name"] = $result["first_name"];
            $admin["last_name"] = $result["last_name"];
			$admin["password"] = $result["password"];
			$admin["email"] = $result["email"];
            // success
            $response["success"] = 1;
 
            // user node
            $response["admin"] = array();
 
            array_push($response["admin"], $admin);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no admin found
            $response["success"] = 0;
            $response["message"] = "No admin found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no admin found
        $response["success"] = 0;
        $response["message"] = "No admin found";
 
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