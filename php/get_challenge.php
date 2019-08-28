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
	public function getQuestions(){		
	
	$query = "SELECT question,answerC, answerI, answerI2
            FROM challenge";
	$result = mysqli_query($this->connection, $query);

	$questions_arr = array();

	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){
		
		$json['question'] = $row['question'];
		$json['answerC'] = $row['answerC'];
		$json['answerI'] = $row['answerI'];
		$json['answerI2'] = $row['answerI2'];

		array_push($json['question'], $question);
		array_push($json['answerC'], $answerC);
		array_push($questions_arr, $json);	
						
	}
	
		//echo json_encode($answers_arr);
		echo json_encode($questions_arr);

		// Free result set
        mysqli_free_result($result);
					
	} else{
			//echo "0 results";
			$json['error'] = 'nothing found ';
			echo json_encode($json);
		}

	mysqli_close($this -> connection);
	}
}

$questions= new Questions();
		
	
		$questions -> getQuestions();



?>