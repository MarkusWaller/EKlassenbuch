<?php

/*
 * Der folgende Code aktualisiert einen Eintrag in der Tabelle "class".
 * Der Eintrag ist definiert mit der id (name)
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['h_teacher']) && isset($_POST['name'])) {

    $h_teacher = $_POST['h_teacher'];
    $name = $_POST['name'];

    // include db connect class
    require_once __DIR__ . '/../db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql update row with matched name
    $result = mysql_query("UPDATE class SET h_teacher = '$h_teacher' WHERE name = '$name'");

    // check if row inserted or not
    if ($result) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Klasse erfolgreich aktualisiert.";

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