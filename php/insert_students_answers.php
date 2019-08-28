<?php
include_once 'connection.php';
	
	class Test {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function insert_question($student,$quiz_name,$professor,$course,$question,$answer,$points_awarded)
		{		
			$query = "select id from users where username ='$student'";
			$result = mysqli_query($this->connection, $query);
			$row=mysqli_fetch_array($result);
			$student_id = $row['id'];
			
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
			$row=mysqli_fetch_array($result);
			$question_id = $row['id'];			
				$answer = mysqli_real_escape_string($this->connection,$answer);
			$query = "select id from answers where answer = '$answer' and question_id = '$question_id'";
			$result = mysqli_query($this->connection, $query);
			$row=mysqli_fetch_array($result);
			$answer_id = $row['id'];		
				$query = "insert into students_quizzes(student_id,quiz_id,question_id,answer_id,points_awarded) values ('$student_id','$quiz_id','$question_id','$answer_id','$points_awarded')";
				$insertedA = mysqli_query($this -> connection, $query) or die(mysqli_error($this->connection));
				if($insertedA == 1 ){
					$json['success'] = 'added successfully';
				}
				else{
					$json['error'] = 'error';
				}			
			echo json_encode($json);
			mysqli_close($this->connection);	
		}	
	}
	if(isset($_POST['student'],$_POST['quiz_name'], $_POST['course'],$_POST['professor'],$_POST['question'],$_POST['answer'],$_POST['points_awarded'])) {
		$student = $_POST['student'];
		$quiz_name = $_POST['quiz_name'];
		$course = $_POST['course'];
		$professor = $_POST['professor'];
		$question = $_POST['question'];
		$answer = $_POST['answer'];
		$points_awarded = $_POST['points_awarded'];
	
		if(!empty($student) && !empty($quiz_name) && !empty($course) && !empty($professor)&& !empty($question)&& !empty($answer)){
	
			for($i=0; $i<count($answer); $i++) {
				$questionQuiz[] = new Test();
				$questionQuiz[$i] -> insert_question($student,$quiz_name,$professor,$course,$question,$answer[$i],$points_awarded[$i]);
			}	
		}else{
			$json['error']= 'param must not be empty';
			echo json_encode($json); 
		}
	}
?>