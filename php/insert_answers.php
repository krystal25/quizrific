<?php
include_once 'connection.php';
	
	class Answer {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function insert_answer($question_id, $answer, $isCorrect)
		{	
			//should i check if this question exists already lol?
				$query = "insert into answers(question_id, answer, isCorrect) values ('$question_id','$answer','$isCorrect')";
				$inserted = mysqli_query($this -> connection, $query);
				if($inserted == 1 ){
					$json['success'] = 'answer added';
				}
				else{
					$json['error'] = 'answer couldn\'t be added';
				}		
				echo json_encode($json);
				mysqli_close($this->connection);
			
		}
		
	}
	
	
	$answerQuiz  = new Answer();
	
	if(isset($_POST['question_id'],$_POST['answer'],$_POST['isCorrect'])) {
		$question_id = $_POST['question_id'];
		$answer = $_POST['answer'];
		$isCorrect = $_POST['isCorrect'];

		//also should i check if these parameters are empty i mean i do that in java lol
		$answerQuiz -> insert_answer($question_id,$answer, $isCorrect);
	}
?>