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
	public function getQuestions($quiz,$professor,$course){		
	
	$query = "SELECT questions.id,questions.question, questions.points, questions.image
            FROM quizzes_questions
            INNER JOIN questions ON quizzes_questions.question_id = questions.id 
            INNER JOIN quizzes ON quizzes_questions.quiz_id = quizzes.id
            INNER JOIN courses ON quizzes.course_id = courses.id
            INNER JOIN users ON quizzes.professor_id = users.id
            WHERE quiz = '$quiz' AND username = '$professor' and course ='$course'";
	$result = mysqli_query($this->connection, $query);
	
	$questions_arr = array();

	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){
		
		$json['question'] = $row['question'];
		$question_id = $row['id'];
		$json['points'] = $row['points'];
		$json['image'] = $row['image'];
		
		//echo json_encode($json);
		//array_push($questions_arr,$json);
		//array_push($return_arr,$row_array);	
			
		//check if answers pertain to question
		$query2 = "SELECT * FROM answers where question_id = '$question_id' ";
		$result2 = mysqli_query($this->connection, $query2);	
		
		$json['correct_answers'] = array();
		$json['incorrect_answers'] = array();
		
		while($row2=mysqli_fetch_array($result2)){
				
			$answer = $row2['answer'];
			$correct = $row2['isCorrect'];	
			
			if($correct == 1){
				array_push($json['correct_answers'], $answer);
			}else{
				array_push($json['incorrect_answers'], $answer);
			}
			
		}
		 array_push($questions_arr, $json);	
	}
		echo json_encode($questions_arr);
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

if(isset($_POST['quiz_name'],$_POST['professor'],$_POST['course'])){
	$quiz = $_POST['quiz_name'];
	$professor = $_POST['professor'];
	$course = $_POST['course'];
			
		if(!empty($quiz) && !empty($professor) && !empty($course)){
			$questions -> getQuestions($quiz,$professor,$course);
		}else{
			$json['error']= 'param must not be empty';
			echo json_encode($json); 
		}

}


?>