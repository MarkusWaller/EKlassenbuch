<?php
 
/*
 * Following code will get single teacher details
 * A teacher is identified by teacher_id 
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/../db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["teacher_id"])) {
    $teacher_id = $_GET['teacher_id'];
 
    // get a teacher from teacher table
    $result = mysql_query("SELECT *FROM teacher WHERE teacher_id = $teacher_id");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $teacher = array();
            $teacher["teacher_id"] = $result["teacher_id"];
			$teacher["last_name"] = $result["last_name"];
            $teacher["first_name"] = $result["first_name"];
            $teacher["email"] = $result["email"];
			$teacher["password"] = $result["password"];

 
            // success
            $response["success"] = 1;
 
            // user node
            $response["teacher"] = array();
 
            array_push($response["teacher"], $teacher);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no teacher found
            $response["success"] = 0;
            $response["message"] = "Keinen Eintrag gefunden.";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no teacher found
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