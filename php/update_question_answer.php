<?php
include_once 'connection.php';
	
	class Test {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function update_answer($question,$init_answer,$answer,$init_isCorrect, $isCorrect){
				
			$query = "SELECT id from questions where question = '$question'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)==1){
				$row=mysqli_fetch_array($result);
				$question_id = $row['id'];
				}else{
				$json['error'] ='question could not be found';
			}	
			if($init_answer != $answer){

				$query = "update answers set answer= '$answer' where question_id ='$question_id' and answer = '$init_answer'";
				$result = mysqli_query($this->connection, $query);
				if($result==1){
					$json['success'] ='answer updated';
				}else{
					$json['error'] ='answer could not be updated';
				}	
						
			}else if($init_isCorrect != $isCorrect){
				$query = "update answers set isCorrect= '$isCorrect' where question_id ='$question_id' and answer = '$answer'";
				$result = mysqli_query($this->connection, $query);
				if($result==1){
					$json['success'] ='isCorrect updated';
				}else{
					$json['error'] ='isCorrect could not be updated';
				}	
					
			}
			mysqli_close($this->connection);	
			echo json_encode($json); 
		}	
	}

	if(isset($_POST['question'],$_POST['init_answer'] , $_POST['answer'],$_POST['init_isCorrect'],$_POST['isCorrect'])) {
		
		$question = $_POST['question'];
		$init_answer= $_POST['init_answer'];
		$answer= $_POST['answer'];	
		$init_isCorrect= $_POST['init_isCorrect'];
		$isCorrect= $_POST['isCorrect'];	


		$init_isCorrect = (int) filter_var( $init_isCorrect, FILTER_VALIDATE_BOOLEAN ); 
		$isCorrect = (int) filter_var( $isCorrect, FILTER_VALIDATE_BOOLEAN ); 

		if(!empty($question) && !empty($init_answer) && !empty($answer)){
			$questionQuizz = new Test();
			$questionQuizz-> update_answer($question,$init_answer,$answer,$init_isCorrect, $isCorrect );				
		}else{
			$json['error']= 'param must not be empty';		
			echo json_encode($json); 
		}
	}
?>