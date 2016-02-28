<?php
$money= file_get_contents("http://www.reddit.com/r/emojipasta/new/");
$arr = array();

$emoji = explode("title may-blank", $money);

foreach ($emoji as $x) {
  $tmp = explode(">", $x);

  array_push($arr, $tmp[0]);
}

$arr1 = array();

for ($i = 1; $i < 6; $i++) {
$tmp = explode(" href=\"", $arr[$i]);
$url = explode('" tabindex="1"', $tmp[1]);
array_push($arr1, "http://reddit.com" . $url[0]);
}

unlink("cache.txt");
$file = "cache.txt";
$arr2 = array();

foreach ($arr1 as $x) {
$page = file_get_contents($x);
$tmp = explode('<div class="usertext-body may-blank-within md-container "><div class="md"><p>' , $page);
$final = explode('</p>', $tmp[2]);
array_push($arr2, html_entity_decode($final[0]));
}
$printr = print_r($arr2, true);
file_put_contents($file, $printr, FILE_APPEND);
?>
