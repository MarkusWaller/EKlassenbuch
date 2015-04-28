<?php
 
/*
 * Following code will get single student details
 * A student is identified by student_id (student_id)
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/../db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_POST["email"])) {
    $email = $_POST['email'];
 
    // get a student from student table
    $result = mysql_query("SELECT * FROM student WHERE email = '$email'");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $student = array();
            $student["student_id"] = $result["student_id"];
			$student["first_name"] = $result["first_name"];
            $student["last_name"] = $result["last_name"];
            $student["class"] = $result["class"];
			$student["birth_date"] = $result["birth_date"];
			$student["email"] = $result["email"];
			$student["password"] = $result["password"];
			
            // success
            $response["success"] = 1;
 
            // user node
            $response["student"] = array();
 
            array_push($response["student"], $student);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no student found
            $response["success"] = 0;
            $response["message"] = "Keinen Eintrag gefunden.";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no student found
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