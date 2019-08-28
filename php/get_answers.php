<?php
 
include_once 'connection.php';
//header('Content-Type: application/json');

class Answers{
		private $db;
		private $connection;
		
	function __construct() {
			$this -> db = new DB_Connection(); //connection made
			$this -> connection = $this->db->getConnection(); //reference to the active connection
	}
	public function getAnswers($question_id){		
	$query = "SELECT answer,isCorrect FROM answers WHERE question_id = '$question_id' ";
	$result = mysqli_query($this->connection, $query);

	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){	
		$json['answer'] = $row['answer'];
		echo "\n";
		$json['isCorrect'] = $row['isCorrect'];
		echo json_encode($json);
				
		}
		// Free result set
        mysqli_free_result($result);
			
			
	} else{
	//echo "0 results";
	$json['error'] = 'no question found for question_id: ' .$questionID;
		echo json_encode($json);
	}

	mysqli_close($this -> connection);
	}
}

$answers= new Answers();
if(isset($_POST['question_id'])){
	$question_id =$_POST['question_id'];
	
	$answers-> getAnswers($question_id);
}
?>