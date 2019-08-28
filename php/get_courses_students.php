<?php
 
include_once 'connection.php';
//header('Content-Type: application/json');

class Courses{
		private $db;
		private $connection;
		
	function __construct() {
			$this -> db = new DB_Connection(); //connection made
			$this -> connection = $this->db->getConnection(); //reference to the active connection
	}
	public function getCourses($student){		
	$query = "select course,username
		from
    students_courses sc
        inner join
    courses c
        on sc.course_id = c.id 
    	inner join
    users u
        on sc.student_id = u.id where username = '$student' ORDER BY course ";
	$result = mysqli_query($this->connection, $query);

	$return_arr = array();
	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){	
		$row_array['course'] = $row['course'];
		 array_push($return_arr,$row_array);
				
		}
		echo json_encode($return_arr);
		// Free result set
        mysqli_free_result($result);
						
	} else{
		$row_array['error'] = 'no course';
		array_push($return_arr,$row_array);	
		echo json_encode($return_arr);
	}

	mysqli_close($this -> connection);
	}
}

$courses= new Courses();

if(isset($_POST['student'])){
	$student = $_POST['student'];
	
	$courses-> getCourses($student);
}
?>