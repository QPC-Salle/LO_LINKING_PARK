<?php
require_once 'db.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    echo json_encode(['success' => false, 'message' => 'Mètode no permès']); exit();
}

$data   = json_decode(file_get_contents('php://input'), true);
$action = isset($data['action']) ? $data['action'] : 'create';

if ($action === 'finish') {
    $sessionId = isset($data['session_id']) ? intval($data['session_id']) : 0;
    $tipus     = isset($data['tipus_finalitzacio']) ? $data['tipus_finalitzacio'] : 'manual';

    $conn = getConnection();
    $stmt = $conn->prepare("UPDATE sessions SET data_fi = NOW(), estat = 'finalitzat', tipus_finalitzacio = ? WHERE id = ?");
    $stmt->bind_param("si", $tipus, $sessionId);
    echo json_encode(['success' => $stmt->execute()]);
    $stmt->close(); $conn->close();
} else {
    $userId     = isset($data['user_id'])            ? intval($data['user_id'])            : 0;
    $vehicleId  = isset($data['vehicle_id'])         ? intval($data['vehicle_id'])         : 0;
    $salleId    = isset($data['salle_id'])            ? intval($data['salle_id'])           : null;
    $tempsMaxim = isset($data['temps_maxim_minuts']) ? intval($data['temps_maxim_minuts']) : 60;
    $avisoTemps = isset($data['aviso_temps_minuts']) ? intval($data['aviso_temps_minuts']) : 10;
    $lat        = isset($data['latitud_inici'])      ? floatval($data['latitud_inici'])    : 0;
    $lng        = isset($data['longitud_inici'])     ? floatval($data['longitud_inici'])   : 0;

    if ($userId <= 0 || $vehicleId <= 0) {
        echo json_encode(['success' => false, 'message' => 'Falten camps obligatoris']); exit();
    }

    $conn = getConnection();
    $stmt = $conn->prepare("INSERT INTO sessions (usuari_id, vehicle_id, salle_id, temps_maxim_minuts, aviso_temps_minuts, latitud_inici, longitud_inici) VALUES (?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("iiiidd", $userId, $vehicleId, $salleId, $tempsMaxim, $avisoTemps, $lat, $lng);
    if ($stmt->execute()) {
        echo json_encode(['success' => true, 'session_id' => $conn->insert_id]);
    } else {
        echo json_encode(['success' => false, 'message' => $conn->error]);
    }
    $stmt->close(); $conn->close();
}
?>