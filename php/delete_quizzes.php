<?php
include_once 'connection.php';
	
	class Quiz {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function delete_quiz($professor, $course, $quiz_name)
		{	
				
				$query = "DELETE q.* FROM quizzes as q
					INNER JOIN courses ON q.course_id = courses.id 
					INNER JOIN users ON q.professor_id = users.id 
					WHERE username = '$professor' AND course = '$course' AND quiz = '$quiz_name'";
				$result= mysqli_query($this -> connection, $query);
				$deleted =mysqli_affected_rows($this -> connection);
				if($deleted >0 ){
					$json['success'] = 'quiz deleted';
				}
				else{
					$json['error'] = 'quiz couldn\'t be deleted';
					
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

		$quiz -> delete_quiz($professor,$course, $quiz_name);
	}
?>