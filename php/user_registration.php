<?php
include_once 'connection.php';
	
	class User {
		
		private $db;
		private $connection;
		
		function __construct() {
			$this -> db = new DB_Connection();
			$this -> connection = $this->db->getConnection();
		}
		
		public function register_user($email,$username,$password,$firstname,$lastname,$usertype)
		{	
						
				$sql=$this->connection->prepare("SELECT username,email FROM users WHERE username=? OR email=?");
				$sql->bind_param("ss",$username,$email);
				$sql->execute();
				$result=$sql->get_result();
				$row=$result->fetch_array(MYSQLI_ASSOC);
				if($row['username']==$username){
					$json['error'] = ' username not available ';
					echo json_encode($json);
					mysqli_close($this -> connection);
				}
				else if($row['email']==$email){
					$json['error'] = ' email already registered ';
					echo json_encode($json);
					mysqli_close($this -> connection);
				}else{
				$stmt = $this ->connection->prepare("INSERT INTO users (email, username, password, firstname, lastname) VALUES (?,?,?,?,?)");	
				if($stmt &&
				   $stmt -> bind_param("sssss", $email, $username, $password, $firstname, $lastname) &&
				   $stmt -> execute()){
					//account created  
					if($usertype=='student'){
							$query ="insert into students(student_id) values('$stmt->insert_id')";
							$inserted = mysqli_query($this -> connection, $query);
							if($inserted == 1 ){
								$json['success'] = 'student added';
							} else{
								$json['error'] = 'student could not be added';
							}
					} else if($usertype=='professor'){
							$query ="insert into professors(professor_id) values('$stmt->insert_id')";
							$inserted = mysqli_query($this -> connection, $query);
							if($inserted == 1 ){
								$json['success'] = 'professor added';
							} else{
								$json['error'] = 'professor could not be added';
							}		
						}		
					$stmt->close();
				}else{
					$json['error'] = 'Account couldn\'t be created';
				}			
				echo json_encode($json);
				mysqli_close($this->connection);
			}
			
		}
		
	}
	
	
	$user = new User();
	
	if(isset($_POST['email'],$_POST['username'],$_POST['password'],$_POST['firstname'],$_POST['lastname'],$_POST['usertype'])) {
		$email = $_POST['email'];
		$username = $_POST['username'];
		$password = $_POST['password'];
		$firstname = $_POST['firstname'];
		$lastname = $_POST['lastname'];
		$usertype = $_POST['usertype'];
		
		if(!empty($email) && !empty($username) && !empty($password) && !empty($firstname) && !empty($lastname) && !empty($usertype)){
			
			//$encrypted_password = md5($password);
			$encrypted_password = password_hash($password, PASSWORD_DEFAULT);
			$user-> register_user($email,$username,$encrypted_password,$firstname,$lastname,$usertype);
			
		}else{
			$json['error'] = 'fields must not be empty';
			echo json_encode($json);
		}
		
	}
?>