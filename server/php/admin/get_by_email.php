<?php

/*
 * Der folgende Code liefert alle Einträge der Tabelle "admin" für eine E-Mail Adresse.
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

    // get a admin from admin table
    $result = mysql_query("SELECT * FROM admin WHERE email = '$email'");

    if (!empty($result)) {
        // check for empty result
        if (mysql_num_rows($result) > 0) {

            $result = mysql_fetch_array($result);

            $admin = array();
            $admin["admin_id"] = $result["admin_id"];
            $admin["first_name"] = $result["first_name"];
            $admin["last_name"] = $result["last_name"];
            $admin["email"] = $result["email"];
            $admin["password"] = $result["password"];

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
            $response["message"] = "Kein Admin oder noch nicht registriert.";

            // echo no users JSON
            echo json_encode($response);
        }
    } else {
        // no admin found
        $response["success"] = 0;
        $response["message"] = "Kein Admin oder noch nicht registriert.";

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