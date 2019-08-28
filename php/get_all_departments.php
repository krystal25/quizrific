<?php
 
include_once 'connection.php';
//header('Content-Type: application/json');

class Departments{
		private $db;
		private $connection;
		
	function __construct() {
			$this -> db = new DB_Connection(); //connection made
			$this -> connection = $this->db->getConnection(); //reference to the active connection
	}
	public function getDepartments(){		
	$query ="SELECT department
    FROM
        departments
	ORDER BY department";

	$result = mysqli_query($this->connection, $query);

	$return_arr = array();
	
	if(mysqli_num_rows($result)>0){
		while($row=mysqli_fetch_array($result)){	
		/*$json['department'] = $row['department'];
		echo "\n";
		echo json_encode($json);*/
		 $row_array['department'] = $row['department'];
		 array_push($return_arr,$row_array);
				
		}
		echo json_encode($return_arr);
		// Free result set
        mysqli_free_result($result);
						
	} else{
	//echo "0 results";
	$json['error'] = 'nothing found';
		echo json_encode($json);
	}

	mysqli_close($this -> connection);
	}
}

$departments= new Departments();


	$departments-> getDepartments();

?>