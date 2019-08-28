<?php
include_once 'connection.php';
	
	class Quiz {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function insert_quiz($professor, $course, $quiz_name)
		{	
			$query = "select id from courses where course ='$course'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$row=mysqli_fetch_array($result);
				$course_id = $row['id'];				
			}
			
			$query = "select id from users where username ='$professor'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$row=mysqli_fetch_array($result);
				$professor_id = $row['id'];				
			}
			
			//should i check if this question exists already lol?
				$query = "insert into quizzes (professor_id, course_id, quiz) values ('$professor_id','$course_id','$quiz_name')";
				$inserted = mysqli_query($this -> connection, $query);
				if($inserted == 1 ){
					$json['success'] = 'quiz added';
				}
				else{
					//echo("Error description: " . mysqli_error($this -> connection));
					$json['error'] = 'quiz couldn\'t be added';
				}		
				echo json_encode($json);
				mysqli_close($this->connection);
			
		}
		
	}
	
	
	$quiz  = new Quiz();
	
	if(isset($_POST['professor'],$_POST['course'],$_POST['quiz_name'])) {
		$professor = $_POST['professor'];
		$course = $_POST['course'];
		$quiz_name = $_POST['quiz_name'];

		//also should i check if these parameters are empty i mean i do that in java lol
		$quiz -> insert_quiz($professor,$course, $quiz_name);
	}
?>