<?php
    $conn = new mysqli("localhost", "root", "", "poker");

    if ($conn->connect_error) {
        die("Connection failed: " . $conn->connect_error);
    }
?>