<?php
include_once 'connection.php';
	
	class Department {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function insert_department($department_name)
		{	
			//should i check if this question exists already lol?
				$query = "insert into departments (department) values ('$department_name')";
				$inserted = mysqli_query($this -> connection, $query);
				if($inserted == 1 ){
					$json['success'] = 'department added';
				}
				else{
					$json['error'] = 'department couldn\'t be added';
				}		
				echo json_encode($json);
				mysqli_close($this->connection);
			
		}
		
	}
	
	
	$department= new Department();
	
	if(isset($_POST['department'])) {
		$department_name= $_POST['department'];
		

		//also should i check if these parameters are empty i mean i do that in java lol
		$department -> insert_department($department_name);
	}
?>