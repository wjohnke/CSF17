<?php

	class TasksModel {
		private $error = '';
		private $mysqli;
		private $orderBy = 'title';
		private $orderDirection = 'asc';
		
		public function __construct() {
			session_start();
			$this->initDatabaseConnection();
			$this->restoreOrdering();
		}
		
		public function __destruct() {
			if ($this->mysqli) {
				$this->mysqli->close();
			}
		}
		
		public function getError() {
			return $this->error;
		}
		
		private function initDatabaseConnection() {
			require('db_credentials.php');
			$this->mysqli = new mysqli($servername, $username, $password, $dbname);
			if ($this->mysqli->connect_error) {
				$this->error = $mysqli->connect_error;
			}
		}
		
		private function restoreOrdering() {
			$this->orderBy = $_SESSION['orderby'] ? $_SESSION['orderby'] : $this->orderBy;
			$this->orderDirection = $_SESSION['orderdirection'] ? $_SESSION['orderdirection'] : $this->orderDirection;
		
			$_SESSION['orderby'] = $this->orderBy;
			$_SESSION['orderdirection'] = $this->orderDirection;
		}
	
		public function toggleOrder($orderBy) {
			if ($this->orderBy == $orderBy)	{
				if ($this->orderDirection == 'asc') {
					$this->orderDirection = 'desc';
				} else {
					$this->orderDirection = 'asc';
				}
			} else {
				$this->orderDirection = 'asc';
			}
			$this->orderBy = $orderBy;
			
			$_SESSION['orderby'] = $this->orderBy;
			$_SESSION['orderdirection'] = $this->orderDirection;			
		}
		
		public function getOrdering() {
			return array($this->orderBy, $this->orderDirection);
		}
		
		public function getTasks() {
			$this->error = '';
			$tasks = array();
		
			if (! $this->mysqli) {
				$this->error = "No connection to database.";
				return array($tasks, $this->error);
			}
		
			$orderByEscaped = $this->mysqli->real_escape_string($this->orderBy);
			$orderDirectionEscaped = $this->mysqli->real_escape_string($this->orderDirection);
			$sql = "SELECT * FROM tasks ORDER BY $orderByEscaped $orderDirectionEscaped";
			if ($result = $this->mysqli->query($sql)) {
				if ($result->num_rows > 0) {
					while($row = $result->fetch_assoc()) {
						array_push($tasks, $row);
					}
				}
				$result->close();
			} else {
				$this->error = $mysqli->error;
			}
			
			return array($tasks, $this->error);
		}
		
		public function getTask($id) {
			$this->error = '';
			$task = null;
		
			if (! $this->mysqli) {
				$this->error = "No connection to database.";
				return array($task, $this->error);
			}
			
			if (! $id) {
				$this->error = "No id specified for task to retrieve.";
				return array($task, $this->error);
			}
			
			$idEscaped = $this->mysqli->real_escape_string($id);
		
			$sql = "SELECT * FROM tasks WHERE id = '$idEscaped'";
			if ($result = $this->mysqli->query($sql)) {
				if ($result->num_rows > 0) {
					$task = $result->fetch_assoc();
				}
				$result->close();
			} else {
				$this->error = $this->mysqli->error;
			}
			
			return array($task, $this->error);		
		}
		
		public function addTask($data) {
			$this->error = '';
			
			$title = $data['title'];
			$category = $data['category'];
			$description = $data['description'];
			
			if (! $title) {
				$this->error = "No title found for task to add. A title is required.";
				return $this->error;			
			}
			
			if (! $category) {
				$category = 'uncategorized';
			}
			
			$titleEscaped = $this->mysqli->real_escape_string($title);		
			$categoryEscaped = $this->mysqli->real_escape_string($category);
			$descriptionEscaped = $this->mysqli->real_escape_string($description);
	
			$sql = "INSERT INTO tasks (title, description, category, addDate) VALUES ('$titleEscaped', '$descriptionEscaped', '$categoryEscaped', NOW())";
	
			if (! $result = $this->mysqli->query($sql)) {
				$this->error = $this->mysqli->error;
			}
			
			return $this->error;
		}
		
		public function updateTaskCompletionStatus($id, $status) {
			$this->error = "";
		
			$completedDate = 'null';
			if ($status == 'completed') {
				$completedDate = 'NOW()';
			}
	
			if (!$id) {
				$this->error = "No task was specified to change completion status.";
			} else {
				$idEscaped = $this->mysqli->real_escape_string($id);
				$sql = "UPDATE tasks SET completedDate = $completedDate WHERE id = '$idEscaped'";
				if (! $result = $this->mysqli->query($sql) ) {
					$this->error = $this->mysqli->error;
				}
			}
	
			return $this->error;
		}
		
		public function updateTask($data) {
			$this->error = '';
			
			if (! $this->mysqli) {
				$this->error = "No connection to database.";
				return $this->error;
			}
			
			$id = $data['id'];
			if (! $id) {
				$this->error = "No id specified for task to update.";
				return $this->error;			
			}
			
			$title = $data['title'];
			if (! $title) {
				$this->error = "No title found for task to update. A title is required.";
				return $this->error;			
			}		
			
			$description = $data['description'];
			$category = $data['category'];
			
			$idEscaped = $this->mysqli->real_escape_string($id);
			$titleEscaped = $this->mysqli->real_escape_string($title);
			$descriptionEscaped = $this->mysqli->real_escape_string($description);
			$categoryEscaped = $this->mysqli->real_escape_string($category);
			$sql = "UPDATE tasks SET title='$titleEscaped', description='$descriptionEscaped', category='$categoryEscaped' WHERE id = $idEscaped";
			if (! $result = $this->mysqli->query($sql) ) {
				$this->error = $this->mysqli->error;
			} 
			
			return $this->error;
		}
		
		public function deleteTask($id) {
			$this->error = '';
			
			if (! $this->mysqli) {
				$this->error = "No connection to database.";
				return $this->error;
			}
			
			if (! $id) {
				$this->error = "No id specified for task to delete.";
				return $this->error;			
			}			
		
			$idEscaped = $this->mysqli->real_escape_string($id);
			$sql = "DELETE FROM tasks WHERE id = $idEscaped";
			if (! $result = $this->mysqli->query($sql) ) {
				$this->error = $this->mysqli->error;
			}
			
			return $this->error;
		}

	
	}

?>