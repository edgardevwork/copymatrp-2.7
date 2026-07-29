<?php

session_start();
require_once 'vendor/connect.php';

$nick_name = $_POST['name'];

$user_data = R::findOne('accounts', 'name = ?', [$nick_name]);

function get_frac_name($frac_id) {
  if($frac_id==0){
   return "Нет";
  }
  $frac = R::findOne('fractions', 'id = ?', [$frac_id]);
  return $frac->name;
}
function is_zero($num,$text) {
  if($num==0){
   return $text;
  }
  return $num;
}


if ($user_data) {
    $response = array();
    $response[0] = [
        "status" => true,
        "name" => $user_data->name,
        "skin" => $user_data->skin,
    "sex" => $user_data->sex,
    "lvl" => $user_data->lvl,
    "exp" => $user_data->exp,
    "needexp" => $user_data->needexp,
    "cash" => $user_data->cash,
    "bank" => $user_data->bank,
    "number" => is_zero($user_data->number,"отсутствует"),
    "fraction" => get_frac_name($user_data->fraction),
    "rank" => $user_data->rank,
    "promo" => $user_data->promo,
    "admin" => $user_data->admin,
    "house" => is_zero($user_data->house,"бездомный"),
    "donate" => $user_data->donate,
    "business" => is_zero($user_data->business,"отсутствует")
    ];
 echo json_encode($response, JSON_UNESCAPED_UNICODE);
    // Дополнительные поля могут быть добавлены в соответствии с вашей схемой базы данных
} else {
    $response = [
        "status" => false,
  "error" =>"Запись не найдена"
    ];
    echo json_encode($response, JSON_UNESCAPED_UNICODE);
}
?>