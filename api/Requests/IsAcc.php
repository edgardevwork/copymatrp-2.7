<?php
session_start();
require_once 'vendor/connect.php';

$nick_name = $_POST['user_name'] ?? '';

$user_data = $db->findOne('accounts', 'name = ?', [$nick_name]);

if ($user_data){
    echo 1;
} else{
    echo 11;
}
?>