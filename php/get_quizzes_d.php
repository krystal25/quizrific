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
	public function getQuizzes($professor,$course){		
	
	//show only quizzes that have stats, i.e. are in quiz_results
$query = "SELECT DISTINCT quizzes.quiz,courses.course, users.username
			FROM quizzes
			INNER JOIN courses ON quizzes.course_id = courses.id 
			INNER JOIN users ON quizzes.professor_id = users.id 
			INNER JOIN quiz_results ON quiz_results.quiz_id = quizzes.id 
			WHERE username = '$professor' and course = '$course'";
			
	$result = mysqli_query($this->connection, $query);

	$return_arr = array();
	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){

		$row_array['quizName'] = $row['quiz'];
		 
		array_push($return_arr,$row_array);	
		
		}
		
		echo json_encode($return_arr);
		// Free result set
        mysqli_free_result($result);
					
	} else{
	//echo "0 results";
	$row_array['error'] = 'no quiz ';
	array_push($return_arr,$row_array);	
	
		echo json_encode($return_arr);
	}

	mysqli_close($this -> connection);
	}
}

$questions= new Questions();

if(isset($_POST['professor'],$_POST['course'])){
	$professor = $_POST['professor'];
	$course = $_POST['course'];
	
	$questions -> getQuizzes($professor,$course);

}


?>