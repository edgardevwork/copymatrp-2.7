<?php
session_start();
require_once 'vendor/connect.php';

$nick_name = $_POST['nick'] ?? $_GET['nick'] ?? '';
$sex = $_POST['sex'] ?? $_GET['sex'] ?? '';
$skin = $_POST['skin'] ?? $_GET['skin'] ?? '';
$promo = $_POST['promo'] ?? $_GET['promo'] ?? '';


// Check if login exists
$check_login = $db->findOne('accounts', 'name = ?', [$nick_name]);
if ($check_login) {
    echo 111;
    die();
}

$error_fields = [];
if ($nick_name === '') $error_fields[] = 'nick_name';
if ($sex === '') $error_fields[] = 'sex';
if ($skin === '') $error_fields[] = 'skin';

if (!empty($error_fields)) {
    echo 11;
    die();
}

try {
    // Create account
    $user = $db->dispense('accounts');
    $user->name = $nick_name;
    $user->sex = $sex;
    $user->skin = $skin;
    $user->money = 1000;
    if($promo) {
        //$user->promo = $promo;
    }
    
    $user->password = 'EDGARSPACERP'; // Обязательное поле
    $user->salt = '0xEDGAR'; // Обязательное поле
    
    $db->store($user);
    echo 1;
    
} catch (Exception $e) {
    error_log('Error creating character: ' . $e->getMessage());
    echo 0;
}
?>