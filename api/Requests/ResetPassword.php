<?php
require_once 'vendor/firebase-php/autoload.php';  // Путь к autoload.php из скачанного архива

use Kreait\Firebase\Factory;
use Kreait\Firebase\Auth\UserRecordNotFoundException;

// Создаём фабрику с вашим файлом ключей
$factory = (new Factory())
    ->withServiceAccount('zakaz-matrp-firebase-adminsdk-fbsvc-c0c4dea3ef.json');

$auth = $factory->createAuth();

$userEmail = $_POST['user_email']; // Например, "user@example.com"
$newPassword = $_POST['new_pass'];

try {
    // Ищем пользователя по почте
    $user = $auth->getUserByEmail("mail".$userEmail);
    
    if ($user) {
        try {
            // Смена пароля по UID найденного пользователя
            $auth->updateUser($user->uid, [
                'password' => $newPassword,
            ]);
            
            echo "11";
        } catch (\Throwable $e) {
            http_response_code(500);
            echo json_encode(['error' => 'Ошибка изменения пароля: ' . $e->getMessage()]);
        }
    } else {
        http_response_code(404);
        echo json_encode(['error' => 'Пользователь с такой почтой не найден']);
    }
} catch (UserRecordNotFoundException $e) {
    http_response_code(404);
    echo json_encode(['error' => 'Пользователь с почтой "' . $userEmail . '" не существует']);
}
?>