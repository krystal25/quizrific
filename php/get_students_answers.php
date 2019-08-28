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
	public function getQuestions($quiz,$student){		
	
	$query = "SELECT users.id,questions.question, questions.points, questions.image
            FROM questions
            INNER JOIN students_quizzes ON students_quizzes.question_id = questions.id 
            INNER JOIN users ON students_quizzes.student_id = users.id
            INNER JOIN answers ON answers.id = students_quizzes.answer_id
			INNER JOIN quizzes ON quizzes.id = students_quizzes.quiz_id
            WHERE username = '$student' AND quiz = '$quiz'
			GROUP BY questions.question";
	$result = mysqli_query($this->connection, $query);
	
	$questions_arr = array();

	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){
		
		$json['question'] = $row['question'];
		$question = $row['question'];
		$json['points'] = $row['points'];
		$json['image'] = $row['image'];
		$student_id = $row['id'];
		//$json['answer'] = $row['answer'];
		//$json['points_awarded'] = $row['points_awarded'];
		
		$query_question = "SELECT id FROM questions where question= '$question'";
		$result_question = mysqli_query($this->connection, $query_question);
		$row_question = mysqli_fetch_array($result_question);
		$question_id = $row_question['id'];	
		//check if answers pertain to question
		$query2 = " SELECT answers.answer, students_quizzes.points_awarded FROM students_quizzes 
					INNER JOIN answers on answers.id = students_quizzes.answer_id
					where  students_quizzes.question_id = '$question_id'  AND   students_quizzes.student_id = '$student_id'";
		
		$result2 = mysqli_query($this->connection, $query2);	
		
		$json['students_answers'] = array();
		$json['points_awarded'] = array();
		
		while($row2=mysqli_fetch_array($result2)){			
			$answer = $row2['answer'];
			$points_awarded = $row2['points_awarded'];				
			array_push($json['students_answers'], $answer);		
			array_push($json['points_awarded'], $points_awarded);		
		}
		 array_push($questions_arr, $json);		
	}
		echo json_encode($questions_arr);
        mysqli_free_result($result);
				
	} else{
			$json['error'] = 'nothing found ';
			echo json_encode($json);
		}

	mysqli_close($this -> connection);
	}
}

$questions= new Questions();

if(isset($_POST['quiz_name'],$_POST['student'])){
	$quiz = $_POST['quiz_name'];
	$student = $_POST['student'];
			
		if(!empty($quiz) && !empty($student)){
			$questions -> getQuestions($quiz,$student);
		}else{
			$json['error']= 'param must not be empty';
			echo json_encode($json); 
		}

}


?>