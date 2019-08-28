<?php
include_once 'connection.php';
	
	class Test {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function update_points($question,$init_points,$points){
			if($init_points != $points){
			$query = "SELECT id from questions where question = '$question'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)==1){
				$row=mysqli_fetch_array($result);
				$question_id = $row['id'];

				$query = "update questions set points= '$points' where id ='$question_id'";
				$result = mysqli_query($this->connection, $query);
				if($result==1){
					$json['success'] ='points updated';
				}else{
					$json['error'] ='points could not be updated';
				}	
			}else{
					$json['error'] ='question could not be found';
				}				
			}
			mysqli_close($this->connection);	
			echo json_encode($json); 
		}	
	}

	if(isset($_POST['question'],$_POST['init_points'] , $_POST['points'])) {
		
		$question = $_POST['question'];
		$init_points= $_POST['init_points'];
		$points= $_POST['points'];	
	
		if(!empty($question) && !empty($init_points) && !empty($points)){
			$questionQuizz = new Test();
			$questionQuizz-> update_points($question,$init_points,$points);				
		}else{
			$json['error']= 'param must not be empty';		
			echo json_encode($json); 
		}
	}
?>