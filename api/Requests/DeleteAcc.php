<?php
session_start();
require_once 'vendor/connect.php';

// Получаем параметры
$nick_name = $_POST['nick'] ?? '';

try {
    // Проверяем обязательные поля
    if ($nick_name === '') {
        echo 11; // Некорректные обязательные поля
        exit;
    }

    // Подключаемся к игровой БД
    $gameDb = $db->getConnection();

    // Проверяем обязательные поля
    if ($nick_name === '') {
        echo 11; // Некорректные обязательные поля
        exit;
    }

    // Проверяем наличие аккаунта
    $stmt = $gameDb->prepare("SELECT 1 FROM accounts WHERE name = ?");
    $stmt->execute([$nick_name]);
    if (!$stmt->fetch()) {
        echo 111; // Аккаунт не найден
        exit;
    }

    // Готовим запрос на удаление аккаунта
    $stmt = $gameDb->prepare("DELETE FROM accounts WHERE name = ?");

    // Выполняем запрос
    if ($stmt->execute([$nick_name])) {
        echo 1; // Удаление прошло успешно
    } else {
        echo 00000; // Ошибка при удалении
    }

} catch (Exception $e) {
    error_log('Error deleting account: ' . $e->getMessage());
    echo $e->getMessage();
}
?>