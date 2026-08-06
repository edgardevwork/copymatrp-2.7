<?php

session_start();
require_once 'vendor/connect.php';
// НАЧАЛО ОБРАБОТКИ ПОСТ-ЗАПРОСА ОТ APK

// Устанавливаем заголовок ответа JSON
header('Content-Type: application/json; charset=utf-8');

// Проверяем, что запрос пришел методом POST
if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    header('HTTP/1.1 405 Method Not Allowed');
    echo json_encode(["status" => "error", "message" => "Only POST requests allowed"]);
    exit;
}

// Получаем сырые данные из тела POST-запроса (input stream)
$rawInput = file_get_contents('php://input');
if (empty($rawInput)) {
    header('HTTP/1.1 400 Bad Request');
    echo json_encode(["status" => "error", "message" => "Empty request body"]);
    exit;
}

// Декодируем JSON в ассоциативный массив
$data = json_decode($rawInput, true);
if (json_last_error() !== JSON_ERROR_NONE) {
    header('HTTP/1.1 400 Bad Request');
    echo json_encode(["status" => "error", "message" => "Invalid JSON format"]);
    exit;
}

try {
    // Используем ваш метод dispense для создания объекта таблицы bug_reports
    $report = $db->dispense('bug_reports');

    // Наполняем данными из полученного от APK JSON пакета
    $report->uid             = isset($data['uid']) ? $data['uid'] : null;
    $report->location        = $data['location'] ?? 'Unknown location';
    $report->error_text      = $data['error_text'] ?? 'No text provided';
    $report->timestamp       = $data['timestamp'] ?? date('Y-m-d H:i:s');
    $report->device_brand    = $data['device_brand'] ?? 'Unknown';
    $report->device_model    = $data['device_model'] ?? 'Unknown';
    $report->android_version = $data['android_version'] ?? 'Unknown';
    $report->android_sdk     = isset($data['android_sdk']) ? intval($data['android_sdk']) : 0;
    $report->app_version     = $data['app_version'] ?? 'Unknown';
    $report->network_type    = $data['network_type'] ?? 'Unknown';

    // Сохраняем запись через метод store
    $insertedId = $db->store($report);

    // Возвращаем успешный статус клиенту
    echo json_encode([
        "status" => "success",
        "message" => "Report saved successfully",
        "report_id" => $insertedId
    ]);

} catch (Exception $e) {
    header('HTTP/1.1 500 Internal Server Error');
    echo json_encode([
        "status" => "error",
        "message" => "Failed to save log: " . $e->getMessage()
    ]);
}
?>