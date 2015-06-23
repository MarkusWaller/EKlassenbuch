<?php

/*
 * Der folgende Code löscht ein Objekt aus der Tabelle "class".
 * Das Objekt ist definiert mit der id (name)
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['name'])) {
    $name = $_POST['name'];

    // include db connect class
    require_once __DIR__ . '/../db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql update row with matched name
    $result = mysql_query("DELETE FROM class WHERE name = '$name'");

    // check if row deleted or not
    if (mysql_affected_rows() > 0) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Klasse erfolgreich gelöscht.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // no class found
        $response["success"] = 0;
        $response["message"] = "Klasse nicht gefunden.";

        // echo no users JSON
        echo json_encode($response);
    }
} else {
    // required field is missing
    $response["success"] = 0;
    $response["message"] = "Nicht alle erforderlichen Parameter vorhanden.";

    // echoing JSON response
    echo json_encode($response);
}
?>