<?php

include 'db.php';

if ($_SERVER['REQUEST_METHOD'] === 'PUT') {
    $data = file_get_contents("php://input");
    $putData = json_decode($data, true);

    $hashedPassword = env('PASSWORD');

    if (!password_verify($putData["API_KEY"], $hashedPassword)) {
        echo "Invalid API KEY";
        die();
    }

    $student = [];

    if (isset($putData['name']) || isset($putData['wins']) ||
        isset($putData['second']) || isset($putData["third"]) ||
        isset($putData['fourth']) || isset($putData["fifth"]) ||
        isset($putData['sixth']) || isset($putData['avg_bust_round'])) {

        $name = filter_var($putData['name'], FILTER_SANITIZE_STRING);
        $wins = filter_var($putData['wins'], FILTER_SANITIZE_NUMBER_INT);
        $second = filter_var($putData['second'], FILTER_SANITIZE_NUMBER_INT);
        $third = filter_var($putData['third'], FILTER_SANITIZE_NUMBER_INT);
        $fourth = filter_var($putData['fourth'], FILTER_SANITIZE_NUMBER_INT);
        $fifth = filter_var($putData['fifth'], FILTER_SANITIZE_NUMBER_INT);
        $sixth = filter_var($putData['sixth'], FILTER_SANITIZE_NUMBER_INT);
        $avgBustRound = filter_var($putData['avg_bust_round'], FILTER_SANITIZE_NUMBER_FLOAT);

        $query = "SELECT * FROM students WHERE name = ?";
        $stmt = $conn->prepare($query);
        $stmt->bind_param('s', $name);
        $stmt->execute();
        $result = $stmt->get_result();
        $student = $result->fetch_assoc();

        if ($student) {
            $updateQuery = "UPDATE students SET wins = ?, second = ?, third = ?, fourth = ?, fifth = ?, sixth = ?, avg_bust_round = ? WHERE name = ?";
            $updateStmt = $conn->prepare($updateQuery);
            $updateStmt->bind_param('iiiiiids', $wins, $second, $third, $fourth, $fifth, $sixth, $avgBustRound, $name);

            if ($updateStmt->execute()) {
                echo "Results updated\n";
                echo json_encode($student);
            } else {
                echo "Error updating results: " . $updateStmt->error;
            }
            $updateStmt->close();
        } else {
            echo "Student not on record\n";
            $insertQuery = "INSERT INTO students (name, wins, second, third, fourth, fifth, sixth, avg_bust_round) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            $insertStmt = $conn->prepare($insertQuery);
            $insertStmt->bind_param('siiiiiid', $name, $wins, $second, $third, $fourth, $fifth, $sixth, $avgBustRound);

            if ($insertStmt->execute()) {
                echo "$name record inserted\n";
            } else {
                echo "Error updating results: " . $insertStmt->error;
            }
            $insertStmt->close();
        }

        $stmt->close();
    } else {
        echo "Missing information";
        die();
    }
}

$conn->close();

?>
