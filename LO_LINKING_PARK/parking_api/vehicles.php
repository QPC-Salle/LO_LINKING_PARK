<?php
require_once 'db.php';

$method = $_SERVER['REQUEST_METHOD'];

if ($method === 'GET') {
    $userId = isset($_GET['user_id']) ? intval($_GET['user_id']) : 0;
    if ($userId <= 0) { echo json_encode(['success' => false, 'message' => 'user_id invàlid']); exit(); }

    $conn = getConnection();
    $stmt = $conn->prepare("SELECT id, matricula, marca, model, color, any_fabricacio FROM vehicles WHERE usuari_id = ? AND actiu = 1 ORDER BY creat_el DESC");
    $stmt->bind_param("i", $userId);
    $stmt->execute();
    $result = $stmt->get_result();
    $vehicles = [];
    while ($row = $result->fetch_assoc()) { $vehicles[] = $row; }
    echo json_encode(['success' => true, 'vehicles' => $vehicles]);
    $stmt->close(); $conn->close();

} elseif ($method === 'POST') {
    $data = json_decode(file_get_contents('php://input'), true);
    $action = isset($data['action']) ? $data['action'] : 'add';

    if ($action === 'delete') {
        $id = isset($data['id']) ? intval($data['id']) : 0;
        $conn = getConnection();
        $stmt = $conn->prepare("UPDATE vehicles SET actiu = 0 WHERE id = ?");
        $stmt->bind_param("i", $id);
        echo json_encode(['success' => $stmt->execute()]);
        $stmt->close(); $conn->close();
    } else {
        $userId      = isset($data['user_id'])       ? intval($data['user_id'])              : 0;
        $matricula   = isset($data['matricula'])      ? strtoupper(trim($data['matricula']))  : '';
        $marca       = isset($data['marca'])          ? trim($data['marca'])                  : '';
        $model       = isset($data['model'])          ? trim($data['model'])                  : '';
        $color       = isset($data['color'])          ? trim($data['color'])                  : '';
        $anyFab      = isset($data['any_fabricacio']) ? intval($data['any_fabricacio'])       : 0;

        if ($userId <= 0 || empty($matricula) || empty($marca) || empty($model)) {
            echo json_encode(['success' => false, 'message' => 'Falten camps obligatoris']); exit();
        }

        $conn = getConnection();
        // Màxim 5 vehicles
        $cnt = $conn->prepare("SELECT COUNT(*) as total FROM vehicles WHERE usuari_id = ? AND actiu = 1");
        $cnt->bind_param("i", $userId); $cnt->execute();
        if ($cnt->get_result()->fetch_assoc()['total'] >= 5) {
            echo json_encode(['success' => false, 'message' => 'Màxim 5 vehicles per usuari']);
            $cnt->close(); $conn->close(); exit();
        }
        $cnt->close();

        $stmt = $conn->prepare("INSERT INTO vehicles (usuari_id, matricula, marca, model, color, any_fabricacio) VALUES (?, ?, ?, ?, ?, ?)");
        $stmt->bind_param("issssi", $userId, $matricula, $marca, $model, $color, $anyFab);
        if ($stmt->execute()) {
            echo json_encode(['success' => true, 'id' => $conn->insert_id]);
        } else {
            echo json_encode(['success' => false, 'message' => $conn->error]);
        }
        $stmt->close(); $conn->close();
    }
}
?>