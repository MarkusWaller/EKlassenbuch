
<?php
 
/*
 * Following code will update a student information
 * A student is identified by student_id 
 */
 
// array for JSON response
$response = array();
 
// check for required fields
if (isset($_POST['student_id']) && isset($_POST['first_name']) && isset($_POST['last_name']) && isset($_POST['class']) && isset($_POST['birth_date']) && isset($_POST['email'])&& isset($_POST['password']) ) {
 
    $student_id = $_POST['student_id'];
    $first_name = $_POST['first_name'];
	$last_name = $_POST['last_name'];
    $class = $_POST['class'];
	$birth_date = $_POST['birth_date'];
    $email = $_POST['email'];
	$password = $_POST['password'];


 
    // include db connect class
    require_once __DIR__ . '/../db_connect.php';
 
    // connecting to db
    $db = new DB_CONNECT();
 
    // mysql update row with matched pid
    $result = mysql_query("UPDATE student SET last_name = '$last_name', first_name = '$first_name', class = '$class', birth_date = '$birth_date', email = '$email', password = '$password' WHERE student_id = '$student_id'");
 
    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "student successfully updated.";
 
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