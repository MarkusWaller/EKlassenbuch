<?php

/*
 * Der folgende Code löscht ein Objekt aus der Tabelle "admin".
 * Das Objekt ist definiert mit der id (admin_id)
 */

// array for JSON response
$response = array();

// check for required fields
if (isset($_POST['admin_id'])) {
    $admin_id = $_POST['admin_id'];

    // include db connect class
    require_once __DIR__ . '/../db_connect.php';

    // connecting to db
    $db = new DB_CONNECT();

    // mysql update row with matched admin_id
    $result = mysql_query("DELETE FROM admin WHERE admin_id = $admin_id");

    // check if row deleted or not
    if (mysql_affected_rows() > 0) {
        // successfully updated
        $response["success"] = 1;
        $response["message"] = "Admin erfolgreich gelöscht.";

        // echoing JSON response
        echo json_encode($response);
    } else {
        // no admin found
        $response["success"] = 0;
        $response["message"] = "Admin nicht gefunden.";

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