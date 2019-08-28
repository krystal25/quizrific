<?php
include_once 'connection.php';
	
	class Course {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function insert_course($course_name, $course_year)
		{	
			//should i check if this question exists already lol?
				$query = "insert into courses(course,course_year) values ('$course_name', '$course_year')";
				$inserted = mysqli_query($this -> connection, $query);
				if($inserted == 1 ){
					$json['success'] = 'course added';
				}
				else{
					$json['error'] = 'course couldn\'t be added';
				}		
				echo json_encode($json);
				mysqli_close($this->connection);
			
		}
		
	}
	
	
	$course= new Course();
	
	if(isset($_POST['course'], $_POST['course_year'])) {
		$course_name= $_POST['course'];
		$course_year = $_POST['course_year'];
		

		//also should i check if these parameters are empty i mean i do that in java lol
		$course -> insert_course($course_name, $course_year);
	}
?>