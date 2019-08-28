<?php
include_once 'connection.php';
	
	class Test {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function insert_question($quiz_name,$professor,$course,$question,$points,$answer,$is_correct,$target_dir)
		{		
			$query = "SELECT quizzes.id
					FROM quizzes
					INNER JOIN courses ON quizzes.course_id = courses.id 
					INNER JOIN users ON quizzes.professor_id = users.id 
					WHERE username = '$professor' AND course = '$course' AND quiz = '$quiz_name'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$row=mysqli_fetch_array($result);
				$quiz_id = $row['id'];			
			}else{
				$json['error'] = 'quiz not found';
			}
		
			$query = "select id from questions where question ='$question'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				//question already exists so we need to add its answers
				$row=mysqli_fetch_array($result);
				$question_id = $row['id'];
				
				$query = "insert into answers (question_id, answer, isCorrect) values ('$question_id','$answer','$is_correct')";
				$insertedA = mysqli_query($this -> connection, $query);
				if($insertedA == 1 ){
					$json['success'] = 'answer added';
				}
				else{
					$json['error'] = 'answer couldn\'t be added when already existing quetsion';
				}	
				
			}else if(mysqli_num_rows($result)==0){
					//insert the actual question
					if($target_dir=="NULL")
					$query = "insert into questions (question, points,image) values ('$question','$points','$target_dir')";	
				else{
					$target_dir = mysqli_real_escape_string($this->connection,$target_dir);
					$query = "insert into questions (question, points,image) values ('$question','$points','$target_dir')";	
					$insertedQ = mysqli_query($this -> connection, $query);
					if($insertedQ == 1 ){
						$json['success'] = 'question added';
						$last_question_id = mysqli_insert_id($this -> connection); //id of the question i just inserted
						//echo $last_question_id;
					}
					else{
						//$json['error'] = 'question couldn\'t be added';
					}
				}
							
					//insert into quizzes_questions
					$query = "insert into quizzes_questions(quiz_id, question_id) values ('$quiz_id','$last_question_id')";	
					$insertedQQ = mysqli_query($this -> connection, $query);
					if($insertedQQ == 1 ){
						$json['success'] = 'queestionQuizz added';
					}
					else{
					//	$json['error'] = 'questionQuizz couldn\'t be added';
					}
					
					$query = "insert into answers (question_id, answer, isCorrect) values ('$last_question_id','$answer','$is_correct')";
					$insertedA = mysqli_query($this -> connection, $query);
					if($insertedA == 1 ){
						$json['success'] = 'answer added';
					}
					else{
					//$json['error'] = 'answer couldn\'t be added';
					}	
								
				}else{
				//$json['error'] = 'this whole thing is erroneous';
			}
			
				
				echo json_encode($json);
				
			
				
				mysqli_close($this->connection);
			
		}
		
	}
	

	
	if(isset($_POST['quiz_name'],$_POST['professor'],$_POST['course'],$_POST['question'],$_POST['points'],$_POST['answer'], $_POST['is_correct'])) {
		$quiz_name = $_POST['quiz_name'];
		$professor = $_POST['professor'];
		$course = $_POST['course'];	
		$question = $_POST['question'];
		$points = $_POST['points'];	
		$answer = $_POST['answer'];
		$is_correct = $_POST['is_correct'];
		$target_dir = "NULL";
		
		$image = $_POST["image"];
		if(!empty($image)){
			$target_dir = "/var/www/html/quizrific/images";
			if(!file_exists($target_dir)){
				mkdir($target_dir,0777,true);
			}
			$unique_name = rand()."_".time().".jpeg";
			$target_dir = $target_dir."/".$unique_name;
			if(file_put_contents($target_dir,base64_decode($image))){
				$json['success'] = 'image uploaded';
			}else{
				$json['error'] = 'image not uploaded';
			}
		}
			$target_dir = "http://35.204.4.163/quizrific/images/".$unique_name;

		//var_dump($answer);
		//var_dump($is_correct);
	

		if(!empty($quiz_name) && !empty($professor)&& !empty($course)&& !empty($question)&& !empty($points)&& !empty($answer) && !empty($is_correct)){
	
			for($i=0; $i<count($answer); $i++) {
			
				//echo $answer[$i].'-'.$is_correct[$i];
		
				$var  = filter_var ($is_correct[$i], FILTER_VALIDATE_BOOLEAN);
				$var = $var?1:0;
		
		
				$questionQuiz[] = new Test();
				$questionQuiz[$i] -> insert_question($quiz_name,$professor,$course,$question,$points,$answer[$i],$var,$target_dir);
			}
			//$questionQuiz1 -> insert_question($quiz_name,$professor,$course,$question,$points,$answer[0],$is_correct[0]);
			//$questionQuiz2-> insert_question($quiz_name,$professor,$course,$question,$points,$answer[1],$is_correct[1]);
			
		}else{
			$json['error']= 'param must not be empty'.$quiz_name.' '.$professor.' '.$course.' '.$question.' '.$points.$answer[0].' '.$is_correct[0];
			echo json_encode($json); 
		}
	}
?>