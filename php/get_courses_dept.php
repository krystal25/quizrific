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
	public function getCourses($department){		
	$query = "select course
from
    dept_courses dc
        inner join
    departments d
        on dc.department_id = d.id
        inner join 
    courses c
        on dc.course_id = c.id 
      where department ='$department' ORDER BY course ";
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

if(isset($_POST['department'])){
	$department =$_POST['department'];
	
	$courses-> getCourses($department);
}
?>