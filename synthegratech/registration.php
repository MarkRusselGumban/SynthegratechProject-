<?php
$servername = "localhost";
$username = "root";
$password = "";
$dbname = "synthegratech";
$conn = new mysqli($servername, $username, $password, $dbname);
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}
$name = $_POST['name'];
$email = $_POST['email'];
$password = $_POST['password'];
$stmt = $conn->prepare("INSERT INTO useraccounts (name, email, password) VALUES (?, ?, ?)");
$stmt->bind_param("sss", $name, $email, $password);

if ($stmt->execute() === TRUE) {
    echo "Registered! you can now login";
} else {
    echo "Error: " . $stmt->error;
}
$stmt->close();
$conn->close();
?>
