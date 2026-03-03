<?php
require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success' => false, 'message' => 'Mètode no permès']); exit();
}

$data = json_decode(file_get_contents('php://input'), true);
$nom      = isset($data['nom'])      ? trim($data['nom'])      : '';
$cognoms  = isset($data['cognoms'])  ? trim($data['cognoms'])  : '';
$email    = isset($data['email'])    ? trim($data['email'])    : '';
$telefon  = isset($data['telefon'])  ? trim($data['telefon'])  : '';
$password = isset($data['password']) ? $data['password']       : '';

if (empty($nom) || empty($cognoms) || empty($email) || empty($password)) {
    echo json_encode(['success' => false, 'message' => 'Tots els camps són obligatoris']); exit();
}
if (!filter_var($email, FILTER_VALIDATE_EMAIL)) {
    echo json_encode(['success' => false, 'message' => 'Email invàlid']); exit();
}
if (strlen($password) < 6) {
    echo json_encode(['success' => false, 'message' => 'Contrasenya mínim 6 caràcters']); exit();
}

$conn = getConnection();
$check = $conn->prepare("SELECT id FROM usuaris WHERE email = ?");
$check->bind_param("s", $email);
$check->execute();
if ($check->get_result()->num_rows > 0) {
    echo json_encode(['success' => false, 'message' => 'Email ja registrat']);
    $check->close(); $conn->close(); exit();
}
$check->close();

$hash = password_hash($password, PASSWORD_BCRYPT);
$stmt = $conn->prepare("INSERT INTO usuaris (nom, cognoms, email, telefon, password_hash) VALUES (?, ?, ?, ?, ?)");
$stmt->bind_param("sssss", $nom, $cognoms, $email, $telefon, $hash);

if ($stmt->execute()) {
    echo json_encode(['success' => true, 'user' => [
        'id' => $conn->insert_id, 'nom' => $nom, 'cognoms' => $cognoms, 'email' => $email
    ]]);
} else {
    echo json_encode(['success' => false, 'message' => 'Error: ' . $conn->error]);
}
$stmt->close(); $conn->close();
?>