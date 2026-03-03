<?php
require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success' => false, 'message' => 'Mètode no permès']);
    exit();
}

$data = json_decode(file_get_contents('php://input'), true);
$email = isset($data['email']) ? trim($data['email']) : '';
$password = isset($data['password']) ? $data['password'] : '';

if (empty($email) || empty($password)) {
    echo json_encode(['success' => false, 'message' => 'Email i contrasenya requerits']);
    exit();
}

$conn = getConnection();
$stmt = $conn->prepare("SELECT id, nom, cognoms, email, password_hash FROM usuaris WHERE email = ? AND actiu = 1");
$stmt->bind_param("s", $email);
$stmt->execute();
$result = $stmt->get_result();

if ($result->num_rows === 0) {
    echo json_encode(['success' => false, 'message' => 'Credencials incorrectes']);
    exit();
}

$user = $result->fetch_assoc();
if (!password_verify($password, $user['password_hash'])) {
    echo json_encode(['success' => false, 'message' => 'Credencials incorrectes']);
    exit();
}

echo json_encode([
    'success' => true,
    'user' => [
        'id' => $user['id'],
        'nom' => $user['nom'],
        'cognoms' => $user['cognoms'],
        'email' => $user['email']
    ]
]);

$stmt->close();
$conn->close();
?>