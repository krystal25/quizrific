<?php
include_once 'connection.php';
	
	class User {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection(); //connection made
			$this -> connection = $this->db->getConnection(); //reference to the active connection
		}
		
		public function login_user($username,$password) 
		{
			$query = "Select id,password from users where username='$username'";
			$result = mysqli_query($this->connection, $query);
			if(mysqli_num_rows($result)==1){
				$row=mysqli_fetch_array($result);
				//$json['success'] = ' Welcome '.$username;
				//$json['success'] = $row['id'];
				//echo $user_id;
				//echo json_encode($json);
				if(password_verify($password, $row['password'])){
					$user_id = $row['id'];
					$query = "Select student_id from students where student_id ='$user_id' ";
					$result = mysqli_query($this->connection, $query);
					if(mysqli_num_rows($result)==1){
						$row=mysqli_fetch_array($result);
						$json['success'] = 'student';
						
					}else{
					
						$query = "Select professor_id from professors where professor_id ='$user_id' ";
						$result = mysqli_query($this->connection, $query);
						if(mysqli_num_rows($result)==1){
							$row=mysqli_fetch_array($result);
							$json['success'] = 'professor';
						}
					}	
				}else{
					$json['error'] = 'Wrong password';
				}
				echo json_encode($json);			
				mysqli_close($this -> connection);
			}
			else if(mysqli_num_rows($result)==0) {			
				$json['error'] = 'Account not found';			
				echo json_encode($json); 
				mysqli_close($this->connection);
			}		
		}
		
	}
	
	
	$user = new User(); //reference to the user clas
	if(isset($_POST['username'],$_POST['password'])) {
		$username = $_POST['username'];
		$password = $_POST['password'];

		if(!empty($username) && !empty($password)){
			$user-> login_user($username,$password);
			
		}else{
			echo json_encode("you must type both inputs"); //print out the message in json format
		}
		
	}
?>