
<?php
 
/*
 * Following code will get single class details
 * A class is identified by name
 */
 
// array for JSON response
$response = array();
 
// include db connect class
require_once __DIR__ . '/../db_connect.php';
 
// connecting to db
$db = new DB_CONNECT();
 
// check for post data
if (isset($_GET["name"])) {
    $name = $_GET['name'];
 
    // get a class from class table
    $result = mysql_query("SELECT * FROM class WHERE name = '$name'");
 
    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {
 
            $result = mysql_fetch_array($result);
 
            $class = array();
            $class["name"] = $result["name"];
            $class["h_teacher"] = $result["h_teacher"];
 
            // success
            $response["success"] = 1;
 
            // user node
            $response["class"] = array();
 
            array_push($response["class"], $class);
 
            // echoing JSON response
            echo json_encode($response);
        } else {
            // no class found
            $response["success"] = 0;
            $response["message"] = "No class found";
 
            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no class found
        $response["success"] = 0;
        $response["message"] = "No class found";
 
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