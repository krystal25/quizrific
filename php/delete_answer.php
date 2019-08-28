<?php
include_once 'connection.php';
	
	class Quiz {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}	
		public function delete_quiz($question,$answer)
		{		
				$query = "DELETE a.* FROM answers as a
					INNER JOIN questions ON a.question_id = questions.id
					WHERE question = '$question' AND answer = '$answer' ";
				$result= mysqli_query($this -> connection, $query);
				$deleted =mysqli_affected_rows($this -> connection);
				if($deleted >0 ){
					$json['success'] = 'answer deleted';
				}
				else{
					$json['error'] = 'answer couldn\'t be deleted';
					
				}		
				echo json_encode($json);
				mysqli_close($this->connection);		
		}
		
	}
	
	$quiz  = new Quiz();
	
	if(isset($_POST['question'],$_POST['answer'])) {
		$question = $_POST['question'];
		$answer= $_POST['answer'];

		$quiz -> delete_quiz($question,$answer);
	}
?>