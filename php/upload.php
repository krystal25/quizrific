<?php
$target_dir = "/upload/images";
$image = $_POST["image"];

if(!file_exists($target_dir)){
	mkdir($target_dir,0777,true);
}

$target_dir = $target_dir."/".rand()."_".time()."jpeg";
if(file_put_contents($target_dir,base64_decode($image))){
	echo json_encode([
	"msg"=> "uploaded",
	"status" => "ok"]);
}
else{
	echo json_encode([
	"msg"=> "sorry there was an err",
	"status" => "error"]);
}
}

?>