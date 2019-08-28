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
	public function getResults($course,$quiz){		
	
$query = "SELECT username, result
            FROM quiz_results
            INNER JOIN quizzes on quiz_results.quiz_id = quizzes.id
            INNER JOIN courses on courses.id = quizzes.course_id
			INNER JOIN users on users.id = quiz_results.student_id
            WHERE  course = '$course' AND quiz = '$quiz' ";
			
	$result = mysqli_query($this->connection, $query);

	$return_arr = array();
	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){
		$row_array['student'] = $row['username'];
		$row_array['result'] = $row['result'];	 
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

if(isset($_POST['course'],$_POST['quiz'])){
	$course = $_POST['course'];
	$quiz = $_POST['quiz'];
	
	$questions -> getResults($course,$quiz);

}


?>