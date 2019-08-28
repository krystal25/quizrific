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
	public function getQuizzes($course){		
	$query = "SELECT DISTINCT quizzes.quiz
			FROM quizzes
			INNER JOIN courses ON quizzes.course_id = courses.id
			INNER JOIN students_quizzes ON students_quizzes.quiz_id = quizzes.id 	
			WHERE course = '$course' ORDER BY quiz";
	$result = mysqli_query($this->connection, $query);
	$return_arr = array();
	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){
		$row_array['quizName'] = $row['quiz'];
		array_push($return_arr,$row_array);		
		}
		echo json_encode($return_arr);
        mysqli_free_result($result);		
	} else{
		$row_array['error'] = 'no course';
		array_push($return_arr,$row_array);	
		echo json_encode($return_arr);
	}
	mysqli_close($this -> connection);
	}
}

$questions= new Questions();

if(isset($_POST['course'])){
	$course = $_POST['course'];

	$questions -> getQuizzes($course);

}


?>