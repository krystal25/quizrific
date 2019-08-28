<?php
include_once 'connection.php';
	
	class Test {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function update_question($init_question, $question){
			if($init_question != $question){
				//question needs to be updated
				$query = "SELECT id from questions where question = '$init_question'";
				$result = mysqli_query($this->connection, $query);
				if(mysqli_num_rows($result)==1){
					$row=mysqli_fetch_array($result);
					$question_id = $row['id'];
					$query = "update questions set question= '$question' where id ='$question_id'";
					$result = mysqli_query($this->connection, $query);
						if($result==1){
							$json['success'] ='question updated';
						}else{
							$json['error'] ='question already exists';
						}			
				}else{
					$json['error'] ='initial question could not be found';
				}
			}
			mysqli_close($this->connection);	
			echo json_encode($json); 
		}	
	}

	if(isset($_POST['init_question'],$_POST['question'])) {
		
		$init_question= $_POST['init_question'];
		$question = $_POST['question'];
	
		if(!empty($init_question) && !empty($question)){
			$questionQuizz = new Test();
			$questionQuizz-> update_question($init_question, $question);				
		}else{
			$json['error']= 'param must not be empty';		
			echo json_encode($json); 
		}
	}
?>