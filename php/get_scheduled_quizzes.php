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
	public function getQuizzes($professor){		
	
	$query = "SELECT quizzes.id,quizzes.quiz,courses.course, users.username,scheduled_quizzes.quiz_date,scheduled_quizzes.quiz_hour, scheduled_quizzes.duration
			FROM quizzes
			INNER JOIN courses ON quizzes.course_id = courses.id 
			INNER JOIN users ON quizzes.professor_id = users.id 
			INNER JOIN scheduled_quizzes ON quizzes.id = scheduled_quizzes.quiz_id
			WHERE username = '$professor' ORDER BY scheduled_quizzes.quiz_date ";
	$result = mysqli_query($this->connection, $query);

	$return_arr = array();
	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){

		$row_array['quizName'] = $row['quiz'];
		$row_array['course'] = $row['course'];
		$row_array['professor'] = $row['username'];
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

if(isset($_POST['professor'])){
	$professor = $_POST['professor'];

	$questions -> getQuizzes($professor);

}


?>