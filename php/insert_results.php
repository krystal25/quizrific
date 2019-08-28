<?php
include_once 'connection.php';
	
	class Grade {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function insert_result($student,$quiz,$course,$professor,$final_result)
		{		
				$query = "SELECT quizzes.id
					FROM quizzes
					INNER JOIN courses ON quizzes.course_id = courses.id 
					INNER JOIN users ON quizzes.professor_id = users.id 
					WHERE username = '$professor' AND course = '$course' AND quiz = '$quiz'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$row=mysqli_fetch_array($result);
				$quiz_id = $row['id'];			
			}else{
				$json['error'] = 'quiz not found';
			}
	
			$query = "select id from users where username ='$student'";
			$result2 = mysqli_query($this->connection, $query);
			$row2=mysqli_fetch_array($result2);
			$student_id = $row2['id'];	
	
				$query = "insert into quiz_results (student_id,quiz_id,result) values ('$student_id','$quiz_id','$final_result')";
				$inserted = mysqli_query($this -> connection, $query);
				if($inserted == 1 ){
					$json['success'] = 'result added';
				}
				else{
					$json['error'] = 'result couldn\'t be added';
				}		
				echo json_encode($json);
				mysqli_close($this->connection);		
		}	
	}
	
	$grade= new Grade();
	
	if(isset($_POST['student'], $_POST['quiz'], $_POST['course'],$_POST['professor'],$_POST['result'] )) {
		$student= $_POST['student'];
		$quiz= $_POST['quiz'];
		$course = $_POST['course'];
		$professor = $_POST['professor'];
		$result= $_POST['result'];
			
		
		$grade -> insert_result($student, $quiz, $course, $professor, $result);
	}
?>