<?php
session_start();
require_once 'vendor/connect.php';

$nick_name = $_POST['name'] ?? '';

$user_data = $db->findOne('accounts', 'name = ?', [$nick_name]);

if ($user_data) {
    $response = array();
    $response[0] = [
        "skin" => $user_data['skin'],
        "cash" => $user_data['money'],
        "donate" => $user_data['rub']
    ];
    echo json_encode($response, JSON_UNESCAPED_UNICODE);
} else {
    $response[0] = [
        "skin" => 0,
        "cash" => 0,
        "donate" => 0
    ];
    echo json_encode($response, JSON_UNESCAPED_UNICODE);
}
?>