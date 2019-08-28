<?php
include_once 'connection.php';
	
	class Quiz {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function insert_quiz($professor, $course, $department)
		{	
			$query = "select id from courses where course ='$course'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$row=mysqli_fetch_array($result);
				$course_id = $row['id'];				
			}
			
			$query = "select id from users where username ='$professor'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$row=mysqli_fetch_array($result);
				$professor_id = $row['id'];				
			}
			
			$query = "select id from departments where department ='$department'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)>0){
				$row=mysqli_fetch_array($result);
				$department_id = $row['id'];				
			}
			
				$query = "insert into offerings( course_id, professor_id, department_id) values ('$course_id','$professor_id','$department_id')";
				$inserted = mysqli_query($this -> connection, $query);
				if($inserted == 1 ){
					$json['success'] = 'offering added';
				}
				else{
					$json['error'] = mysqli_error($this->connection);
				}		
				echo json_encode($json);
				mysqli_close($this->connection);
			
		}
		
	}
	
	
	$quiz  = new Quiz();
	
	if(isset($_POST['professor'],$_POST['course'],$_POST['department'])) {
		$professor = $_POST['professor'];
		$course = $_POST['course'];
		$department = $_POST['department'];

		//also should i check if these parameters are empty i mean i do that in java lol
		$quiz -> insert_quiz($professor,$course, $department);
	}
?>