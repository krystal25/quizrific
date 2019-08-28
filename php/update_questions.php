<?php
include_once 'connection.php';
$question_id;

	
	class Test {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function update_question($init_question, $question, $init_points, $points,$target_dir){
			global $question_id;
			if($init_question != $question){
				//question needs to be updated
			$query = "SELECT id from questions where question = '$init_question'";
			$result = mysqli_query($this->connection, $query);
			$row=mysqli_fetch_array($result);
			$question_id = $row['id'];
			//echo $question_id;
			
			
				$query = "update questions set question= '$question' where id ='$question_id'";
				$result = mysqli_query($this->connection, $query);
					if($result==1){
						$json['success'] ='question updated';
						$updated = true;
					}else{
					$json['error'] ='question already exists';
					}
				
			}else{
			$query = "SELECT id from questions where question = '$question'";
			$result = mysqli_query($this->connection, $query);
			$row=mysqli_fetch_array($result);
			$question_id = $row['id'];
			//echo $question_id;		
			}
		
			if($init_points != $points){
				$query = "update questions set points= '$points' where id ='$question_id'";
				$result = mysqli_query($this->connection, $query);
				if($result==1){
					$updated = true;
				}
			}
			//adding image path
			$query = "SELECT image from questions where id = '$question_id'";
			$result = mysqli_query($this->connection, $query);
			$row=mysqli_fetch_array($result);
			$image_path = $row['image'];
			//echo $image_path;
			if($image_path == "NULL"){
				$query = "insert into questions (image) values ('$target_dir')";	
					$insertedQ = mysqli_query($this -> connection, $query);
					if($insertedQ == 1 ){
						$json['success'] = 'image added';
					}			
			}else{
				if($image_path !== $target_dir){
					$query = "update questions set image = '$target_dir' where id  ='$question_id' ";	
					$updatedQ = mysqli_query($this -> connection, $query);
					if($updatedQ == 1 ){
						$json['success'] = 'image updated';
					}
				}
			}
			
		}
		
		public function update_answers($init_answer,$answer,$init_is_correct,$is_correct)
		{	
			global $question_id;
			
			//echo $question_id;	
			if($init_answer != $answer){
				$query = "update answers set answer= '$answer' where answer='$init_answer' && question_id = '$question_id'";
				$result = mysqli_query($this->connection, $query);
				$init_answer = $answer;
				if($result==1){
					$updated = true;
				}
			}
			//daca rasp e acelasi, daca nu, ia valoarea noului rasp
			if($init_is_correct != $is_correct){
				$query = "update answers set isCorrect= '$is_correct' where answer='$init_answer' && question_id = '$question_id'";
				$result = mysqli_query($this->connection, $query);
				if($result==1){
					$updated = true;
				}
			}
				
				//echo json_encode($json);
				
				mysqli_close($this->connection);
			
		}
		
			public function insert_answer($answer,$is_correct)
		{	
			global $question_id;
			
			//echo $question_id;	
			
				$query = "insert into answers (question_id,answer,isCorrect) values ('$question_id','$answer', '$is_correct')";	
					$insertedA = mysqli_query($this -> connection, $query);
					if($insertedA == 1 ){
						$json['success'] = 'answer added';
					}				
				//echo json_encode($json);
				
				mysqli_close($this->connection);
			
		}
		
	}
	
	$updated;
	$updated =false;
	
	if(isset($_POST['init_question'],$_POST['question'],$_POST['init_points'],$_POST['points'],$_POST['init_answer'],$_POST['answer'],$_POST['init_is_correct'], $_POST['is_correct'])) {
		
		$init_question= $_POST['init_question'];
		$question = $_POST['question'];
		$init_points= $_POST['init_points'];
		$points = $_POST['points'];	
		$init_answer= $_POST['init_answer'];
		$answer = $_POST['answer'];
		$init_is_correct= $_POST['init_is_correct'];
		$is_correct = $_POST['is_correct'];
		
			$target_dir = "NULL";
		
		$image = $_POST["image"];
		if(!empty($image)){
			$target_dir = "/var/www/html/quizrific/images";
			if(!file_exists($target_dir)){
				mkdir($target_dir,0777,true);
			}
			$target_dir = $target_dir."/".rand()."_".time().".jpeg";
			if(file_put_contents($target_dir,base64_decode($image))){
				$json['success'] = 'image uploaded';
			}else{
				$json['error'] = 'image not uploaded';
			}
		}

		//var_dump($answer);
		//var_dump($is_correct);
	

		if(!empty($init_question) && !empty($question)&&!empty($init_points) && !empty($points) && !empty($init_answer) && !empty($answer) && !empty($init_is_correct) &&!empty($is_correct)){
			$questionQuizz = new Test();
			$questionQuizz-> update_question($init_question, $question, $init_points,$points,$target_dir);
			
			$init_size = sizeof($init_answer); 
			$answ_size = sizeof($answer); 
			if($init_size <= $answ_size){
		//if initial size is equal or smaller than actual size
				for($i=0; $i<$init_size; $i++) {
					$var  = filter_var ($is_correct[$i], FILTER_VALIDATE_BOOLEAN);
					$var = $var?1:0;
						
					$var2  = filter_var ($init_is_correct[$i], FILTER_VALIDATE_BOOLEAN);
					$var2 = $var2?1:0;
					
					$questionQuiz = new Test();
					$questionQuiz-> update_answers($init_answer[$i],$answer[$i],$var2,$var);
					$updated= true;
				}
					//user added another answer that needs to be inserted
				$need_insert = $answ_size-$init_size;
				for( $i=0; $i < $need_insert; $i++){
					//for example if init size is 2 and user adds one more option, it's gonna be in index "2" of new answ array, so equal to the size of the init array
					$var  = filter_var ($is_correct[$init_size+$i], FILTER_VALIDATE_BOOLEAN);
					$var = $var?1:0;
					$answerQuizz = new Test();
					$answerQuizz-> insert_answer($answer[$init_size+$i], $var);
				}
			}else{
				//find the init_answer which doesnt have an equivalent actual answ?
				
			}
					
		}else{
			$json['error']= 'param must not be empty';		
		}
		if($updated){
			$json['success'] = 'updated successfully';
		}else{
			$json['error']= 'some error';	
		}
		
		echo json_encode($json); 
	}
?>