<?php

/*
 * Der folgende Code aktualisiert einen Eintrag in der Tabelle "teacher".
 * Der Eintrag ist definiert mit der id (teacher_id)
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['teacher_id']) && isset($_POST['last_name']) && isset($_POST['first_name']) && isset($_POST['email']) && isset($_POST['password'])) {

    $teacher_id = $_POST["teacher_id"];
    $last_name = $_POST["last_name"];
    $first_name = $_POST["first_name"];
    $email = $_POST["email"];
    $password = $_POST["password"];

    // include db connect class
    require_once __DIR__ . '/../db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql update row with matched teacher_id
    $result = mysql_query("UPDATE teacher SET last_name = '$last_name', first_name = '$first_name', email = '$email', password = '$password' WHERE teacher_id = '$teacher_id'");

    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Lehrer erfolgreich aktualisiert.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Nicht alle erforderlichen Parameter vorhanden.";

    // echoing JSON response
    echo json_encode($response);
}
?>