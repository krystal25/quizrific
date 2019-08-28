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
	public function getResults($course,$quiz,$student){		
	
$query = "SELECT points_awarded
            FROM students_quizzes
            INNER JOIN quizzes on students_quizzes.quiz_id = quizzes.id
			INNER JOIN users on users.id = students_quizzes.student_id
            WHERE quiz = '$quiz' AND username = '$student'";
			
	$result = mysqli_query($this->connection, $query);
	$return_arr = array();
	if(mysqli_num_rows($result)>0){
		$row_cnt = mysqli_num_rows($result);
		while($row=mysqli_fetch_array($result)){
		if($row['points_awarded'] ==0)
			$wrong++;	
		}	
		
	$row_array['questionsNo'] = $row_cnt;
	$row_array['wrong'] = $wrong;
	array_push($return_arr,$row_array);	
	echo json_encode($return_arr);
	} else{
	$json['error'] = 'no results';
		echo json_encode($json);
	}

	mysqli_close($this -> connection);
	}
}

$questions= new Questions();

if(isset($_POST['course'],$_POST['quiz'],$_POST['student'])){
	$course = $_POST['course'];
	$quiz = $_POST['quiz'];
	$student = $_POST['student'];
	
	$questions -> getResults($course,$quiz,$student);

}


?>