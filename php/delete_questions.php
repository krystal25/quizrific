<?php
include_once 'connection.php';
	
	class Quiz {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function delete_question($question)
		{	
				
				$query = "DELETE from questions where question = '$question'";
				$result= mysqli_query($this -> connection, $query);
				$deleted =mysqli_affected_rows($this -> connection);
				if($deleted >0 ){
					$json['success'] = 'question deleted';
				}
				else{
					$json['error'] = 'question couldn\'t be deleted';
				}		
				echo json_encode($json);
				mysqli_close($this->connection);
			
		}
		
	}
	
	
	$quiz  = new Quiz();
	
	if(isset($_POST['question'])) {
		$question = $_POST['question'];
		$quiz -> delete_question($question);
	}
?>