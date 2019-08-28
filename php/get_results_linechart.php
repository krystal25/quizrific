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
	public function getResults($username,$course){		
	
$query = "
select quizzes.quiz, users.username, quiz_results.result, scheduled_quizzes.quiz_date
FROM quizzes
INNER JOIN quiz_results ON quiz_results.quiz_id = quizzes.id
INNER JOIN users ON users.id = quiz_results.student_id
INNER JOIN courses ON quizzes.course_id = courses.id
INNER JOIN scheduled_quizzes ON scheduled_quizzes.quiz_id = quizzes.id
WHERE username = '$username' AND course = '$course' ORDER BY scheduled_quizzes.quiz_date ";
			
	$result = mysqli_query($this->connection, $query);

	$return_arr = array();
	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){
		$row_array['student'] = $row['username'];
		$row_array['result'] = $row['result'];	 
		$row_array['quiz'] = $row['quiz'];	
		$row_array['quiz_date'] = $row['quiz_date'];	
		array_push($return_arr,$row_array);		
		}
		echo json_encode($return_arr);
		// Free result set
        mysqli_free_result($result);				
	} else{
	//echo "0 results";
	$json['error'] = 'no results';
		echo json_encode($json);
	}

	mysqli_close($this -> connection);
	}
}

$questions= new Questions();

if(isset($_POST['course'],$_POST['username'])){
	$course = $_POST['course'];
	$username = $_POST['username'];
	
	$questions -> getResults($username,$course);

}


?>