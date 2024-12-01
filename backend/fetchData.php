<?php
    include 'db.php';

    if($_SERVER['REQUEST_METHOD'] !== 'GET'){
        die();
    } else {
        $query = "SELECT name, wins, second, third, fourth, fifth, avg_bust_round, year, last_update, (wins*0.7 + second*0.3) AS points FROM students ORDER BY points DESC";
        $result = $conn->query($query);
        if ($result) {
            $data = [];
            if ($result->num_rows > 0) {
                while ($row = $result->fetch_assoc()) {
                    array_push($data, $row);
                }
                echo json_encode($data);
            } else {
                echo json_encode(["message" => "No records found in the 'students' table."]);
            }
        } else {
            echo json_encode(["message" => "Error executing the SQL query: " . $conn->error]);
        }
    }

    $conn->close();
?>