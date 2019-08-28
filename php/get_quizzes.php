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
	
	$query = "SELECT quizzes.id,quizzes.quiz,courses.course, users.username
			FROM quizzes
			INNER JOIN courses ON quizzes.course_id = courses.id 
			INNER JOIN users ON quizzes.professor_id = users.id WHERE username = '$professor' ORDER BY quiz";
	$result = mysqli_query($this->connection, $query);

	$return_arr = array();
	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){

		$row_array['quizName'] = $row['quiz'];
		$row_array['course'] = $row['course'];
		 
		/*$json['quizName'] = $row['quiz'];
		echo "\n";
		echo json_encode($json);*/
		$quiz_id = $row['id'];
		
		//count number of questions in each quiz
		$query2 = "SELECT * FROM quizzes_questions where quiz_id = '$quiz_id' ";
		$result2 = mysqli_query($this->connection, $query2);
		 // determine number of rows result set 
		$row_cnt = mysqli_num_rows($result2);
		//echo $row_cnt;
		$row_array['questionsNo'] = $row_cnt;
		array_push($return_arr,$row_array);	
		
		}
		
		echo json_encode($return_arr);
		// Free result set
        mysqli_free_result($result);
		//mysqli_free_result($result2);
			
			
	} else{
	//echo "0 results";
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