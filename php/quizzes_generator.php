<?php


class Quizzes{
	public function generate_quizzes($amount, $category, $difficulty){
	//$url = "https://opentdb.com/api.php?amount=5&category=10&difficulty=easy";
	$url = "https://opentdb.com/api.php?amount=".$amount."&category=".$category."&difficulty=".$difficulty;
	$jsonData = file_get_contents($url);
	$json=json_decode($jsonData,true);
	
	$arrQuestions =array();
	foreach($json['results'] as $data){
		$question.= $data['question']."\n";
		$correct_answers.= $data['correct_answer']."\n";
		if($data['type']=="boolean"){
			$incorrect_answers.=$data['incorrect_answers'][0]."\n";
		} else{
			$incorrect_answers.=$data['incorrect_answers'][0]."\n";
			$incorrect_answers.=$data['incorrect_answers'][1]."\n";
			$incorrect_answers.=$data['incorrect_answers'][2]."\n";
			$incorrect_answers.=$data['incorrect_answers'][3]."\n";
		}
		
		$converted_question = mb_convert_encoding($question, "UTF-8", "HTML-ENTITIES"); //converts html encoding
		array_push($arrQuestions,array('question'=>$converted_question));
		
	}
	echo $converted_question;
	echo $correct_answers;
	echo $incorrect_answers;
	//echo json_encode($arrQuestions);
	//echo json_encode($arrQuestions[0]);
	
	}
}

	$quiz= new Quizzes();
	if(isset($_POST['amount'],$_POST['category'],$_POST['difficulty'])){
	$amount =$_POST['amount'];
	$category =$_POST['category'];
	$difficulty =$_POST['difficulty'];
	
	$quiz -> generate_quizzes($amount,$category,$difficulty);
	}



?>