<?php
 
include_once 'connection.php';
//header('Content-Type: application/json');

class Questions{
		private $db;
		private $connection;
		
	function __construct() {
			$this -> db = new DB_Connection(); //connection made
			$this -> connection = $this->db->getConnection(); //reference to the active connection
	}
	public function getQuizzes($student){		
	
	$query = "SELECT quizzes.quiz, courses.course, users.username,quizzes.professor_id, scheduled_quizzes.quiz_date,scheduled_quizzes.quiz_hour, scheduled_quizzes.duration
           FROM courses
            INNER JOIN quizzes ON quizzes.course_id = courses.id
            INNER JOIN scheduled_quizzes ON quizzes.id = scheduled_quizzes.quiz_id
            INNER JOIN students ON students.student_year = courses.course_year
            INNER JOIN users ON users.id = students.student_id       
            WHERE username = '$student'
            ORDER BY scheduled_quizzes.quiz_date, scheduled_quizzes.quiz_hour ";
	$result = mysqli_query($this->connection, $query);

	$return_arr = array();
	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){

		$row_array['quizName'] = $row['quiz'];
		$row_array['course'] = $row['course'];
		
		$professor_id = $row['professor_id'];
		$query = "select username from users where id ='$professor_id'";
		$result2 = mysqli_query($this->connection, $query);
		$row2=mysqli_fetch_array($result2);
		$professor = $row2['username'];	
			
		$row_array['professor'] = $professor;
		$row_array['quiz_date'] = $row['quiz_date'];
		$row_array['quiz_hour'] = $row['quiz_hour'];
		$row_array['duration'] = $row['duration'];
		
		array_push($return_arr,$row_array);	
		}
		
		echo json_encode($return_arr);
        mysqli_free_result($result);				
	} else{
		$json['error'] = 'no quiz ';
		echo json_encode($json);
	}

	mysqli_close($this -> connection);
	}
}

$questions= new Questions();

if(isset($_POST['student'])){
	$student = $_POST['student'];

	$questions -> getQuizzes($student);

}


?>