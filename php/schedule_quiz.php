<?php
include_once 'connection.php';
	
	class Quiz {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function schedule_quiz($professor,$course, $quizName,$quizDate,$quizHour,$duration)
		{	
			$query =  "SELECT quizzes.id
					FROM quizzes
					INNER JOIN courses ON quizzes.course_id = courses.id 
					INNER JOIN users ON quizzes.professor_id = users.id 
					WHERE username = '$professor' AND course = '$course' AND quiz = '$quizName'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$row=mysqli_fetch_array($result);
				$quiz_id = $row['id'];		
	
				//$query = "select id from scheduled_quizzes where quiz_id ='$quiz_id' and quiz_date";
				$query = "insert into scheduled_quizzes (quiz_id, quiz_date, quiz_hour,duration) values ('$quiz_id','$quizDate','$quizHour','$duration')";
				$inserted = mysqli_query($this -> connection, $query);
				if($inserted == 1 ){
					$json['success'] = 'quiz scheduled';
				}
				else{
					$json['error'] = 'quiz already scheduled at this date and time';
				}						
			}else{
				$json['error'] = 'quiz couldn\'t be found';
			}
				
			
				echo json_encode($json);
				mysqli_close($this->connection);
			
		}
		
	}
	
	
	$quiz  = new Quiz();
	
	if(isset($_POST['professor'],$_POST['course'],$_POST['quizName'],$_POST['quizDate'],$_POST['quizHour'],$_POST['duration'])) {
		$professor = $_POST['professor'];
		$course = $_POST['course'];
		$quizName = $_POST['quizName'];		
		$quizDate = $_POST['quizDate'];
		$date = DateTime::createFromFormat('m/d/y',$quizDate);
		//$date = DateTime::createFromFormat('d.m.Y',$quizDate);
		$quizDate = $date->format("Y-m-d");
		$quizHour = $_POST['quizHour'];
		$duration = $_POST['duration'];

		$quiz -> schedule_quiz($professor,$course, $quizName,$quizDate,$quizHour,$duration);
	}
?>