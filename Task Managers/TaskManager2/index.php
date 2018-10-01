<?php
	require('web_utils.php');
	
	session_start();
	
	$message = '';
	
	$target = $_GET['target'];
	$action = $_POST['action'];
	$data = null;
	
	switch($action) {
		case 'delete':
			$message = deleteTask();
			break;
		case 'add':
			list($target, $message, $data) = addTask();
			break;
		case 'set_completed':
			$message = setCompletionStatus('completed');
			break;
		case 'set_not_completed':
			$message = setCompletionStatus('not completed');
			break;
		case 'edit':
			list($target, $message, $data) = editTask();
			break;
		case 'update':
			list($target, $message, $data) = updateTask();
	}
	
	switch($target) {
		case 'taskform':
			presentTaskForm($message, $data);
			break;
		default:
			presentTaskList($message);
	}
	
	

	// functions are defined below
	// eventually these will be moved to individual php files
	
	function presentTaskList($message = "") {
		$stylesheet = 'taskmanager.css';
		
		$orderBy = $_SESSION['orderby'] ? $_SESSION['orderby'] : 'title';
		$orderDirection = $_SESSION['orderdirection'] ? $_SESSION['orderdirection'] : 'asc';
		
		if ($_GET['orderby']) {
			if ($orderBy == $_GET['orderby']) {
				if ($orderDirection == 'asc') {
					$orderDirection = 'desc';
				} else {
					$orderDirection = 'asc';
				}
			} else {
				$orderDirection = 'asc';
			}
			$orderBy = $_GET['orderby'];
		}
		
		$_SESSION['orderby'] = $orderBy;
		$_SESSION['orderdirection'] = $orderDirection;
	
		$tasks = array();

		// Create connection
		require('db_credentials.php');
		$mysqli = new mysqli($servername, $username, $password, $dbname);
	
		if ($mysqli->connect_error) {
			$message = $mysqli->connect_error;
		} else {
			$orderBy = $mysqli->real_escape_string($orderBy);
			$orderDirection = $mysqli->real_escape_string($orderDirection);
			$sql = "SELECT * FROM tasks ORDER BY $orderBy $orderDirection";
			if ($result = $mysqli->query($sql)) {
				if ($result->num_rows > 0) {
					while($row = $result->fetch_assoc()) {
						array_push($tasks, $row);
					}
				}
				$result->close();
			} else {
				$message = $mysqli->error;
			}
			$mysqli->close();
		}
	
		print generatePageHTML("Tasks", generateTaskTableHTML($tasks, $message, $orderBy, $orderDirection), $stylesheet);
	}
	
	function generateTaskTableHTML($tasks, $message, $orderBy, $orderDirection) {
		$html = "<h1>Tasks</h1>\n";
		
		if ($message) {
			$html .= "<p class='message'>$message</p>\n";
		}
		
		$html .= "<p><a class='taskButton' href='index.php?target=taskform'>+ Add Task</a></p>\n";
	
		if (count($tasks) < 1) {
			$html .= "<p>No tasks to display!</p>\n";
			return $html;
		}
	
		$html .= "<table>\n";
		$html .= "<tr><th>delete</th><th>edit</th><th>completed</th>";
		
		$columns = array(array('name' => 'addDate', 'label' => 'add date'), 
						 array('name' => 'completedDate', 'label' => 'completed date'), 
						 array('name' => 'title', 'label' => 'title'), 
						 array('name' => 'description', 'label' => 'description'), 
						 array('name' => 'category', 'label' => 'category'));
		
		// geometric shapes in unicode
		// http://jrgraphix.net/r/Unicode/25A0-25FF
		foreach ($columns as $column) {
			$name = $column['name'];
			$label = $column['label'];
			if ($name == $orderBy) {
				if ($orderDirection == 'asc') {
					$label .= " &#x25BC;";  // ▼
				} else {
					$label .= " &#x25B2;";  // ▲
				}
			}
			$html .= "<th><a class='order' href='index.php?orderby=$name'>$label</a></th>";
		}
	
		foreach ($tasks as $task) {
			$id = $task['id'];
			$addDate = $task['addDate'];
			$completedDate = ($task['completedDate']) ? $task['completedDate'] : '';
			$title = $task['title'];
			$description = ($task['description']) ? $task['description'] : '';
			$category = $task['category'];
			
			$completedAction = 'set_completed';
			$completedLabel = 'not completed';
			if ($completedDate) {
				$completedAction = 'set_not_completed';
				$completedLabel = 'completed';
			}
			
			$html .= "<tr>";
			$html .= "<td><form action='index.php' method='post'><input type='hidden' name='action' value='delete' /><input type='hidden' name='id' value='$id' /><input type='submit' value='Delete'></form></td>";
			$html .= "<td><form action='index.php' method='post'><input type='hidden' name='action' value='edit' /><input type='hidden' name='id' value='$id' /><input type='submit' value='Edit'></form></td>";
			$html .= "<td><form action='index.php' method='post'><input type='hidden' name='action' value='$completedAction' /><input type='hidden' name='id' value='$id' /><input type='submit' value='$completedLabel'></form></td>";
			$html .= "<td>$addDate</td><td>$completedDate</td><td>$title</td><td>$description</td><td>$category</td>";
			$html .= "</tr>\n";
		}
		$html .= "</table>\n";
	
		return $html;
	}
	
	function deleteTask() {
		$id = $_POST['id'];
	
		$message = "";
	
		if (!$id) {
			$message = "No task was specified to delete.";
		} else {
			// Create connection
			require('db_credentials.php');
			$mysqli = new mysqli($servername, $username, $password, $dbname);
			// Check connection
			if ($mysqli->connect_error) {
				$message = $mysqli->connect_error;
			} else {
				$id = $mysqli->real_escape_string($id);
				$sql = "DELETE FROM tasks WHERE id = $id";
				if ( $result = $mysqli->query($sql) ) {
					$message = "Task was deleted.";
				} else {
					$message = $mysqli->error;
				}
				$mysqli->close();
			}
		}
	
		return $message;
	}
	
	function setCompletionStatus($status) {
		$id = $_POST['id'];
	
		$message = "";
		
		$completedDate = 'null';
		if ($status == 'completed') {
			$completedDate = 'NOW()';
		}
	
		if (!$id) {
			$message = "No task was specified to change completion status.";
		} else {
			// Create connection
			require('db_credentials.php');
			$mysqli = new mysqli($servername, $username, $password, $dbname);
			// Check connection
			if ($mysqli->connect_error) {
				$message = $mysqli->connect_error;
			} else {
				$id = $mysqli->real_escape_string($id);
				$sql = "UPDATE tasks SET completedDate = $completedDate WHERE id = '$id'";
				if ( $result = $mysqli->query($sql) ) {
					$message = "Task was updated to $status.";
				} else {
					$message = $mysqli->error;
				}
				$mysqli->close();
			}
		}
	
		return $message;
	}
	
	function presentTaskForm($message = "", $data = null) {
		$category = '';
		$title = '';
		$description = '';
		$selected = array('personal' => '', 'school' => '', 'work' => '', 'uncategorized' => '');
		if ($data) {
			$category = $data['category'] ? $data['category'] : 'uncategorized';
			$title = $data['title'];
			$description = $data['description'];
			$selected[$category] = 'selected';
		} else {
			$selected['uncategorized'] = 'selected';
		}
	
		$html = <<<EOT1
<!DOCTYPE html>
<html>
<head>
<title>Task Manager</title>
<link rel="stylesheet" type="text/css" href="taskmanager.css">
</head>
<body>
<h1>Tasks</h1>
EOT1;

		if ($message) {
			$html .= "<p class='message'>$message</p>\n";
		}
		
		$html .= "<form action='index.php' method='post'>";
		
		if ($data['id']) {
			$html .= "<input type='hidden' name='action' value='update' />";
			$html .= "<input type='hidden' name='id' value='{$data['id']}' />";
		} else {
			$html .= "<input type='hidden' name='action' value='add' />";
		}
		
		$html .= <<<EOT2
  <p>Category<br />
  <select name="category">
	  <option value="personal" {$selected['personal']}>personal</option>
	  <option value="school" {$selected['school']}>school</option>
	  <option value="work" {$selected['work']}>work</option>
	  <option value="uncategorized" {$selected['uncategorized']}>uncategorized</option>
  </select>
  </p>

  <p>Title<br />
  <input type="text" name="title" value="$title" placeholder="title" maxlength="255" size="80"></p>

  <p>Description<br />
  <textarea name="description" rows="6" cols="80" placeholder="description">$description</textarea></p>
  <input type="submit" name='submit' value="Submit"> <input type="submit" name='cancel' value="Cancel">
</form>
</body>
</html>
EOT2;

		print $html;
	}
	
	function addTask() {
		$message = '';
		
		if ($_POST['cancel']) {
			$message = 'Adding new task was cancelled.';
			return array('', $message);
		}
		
		if (! $_POST['title']) {
			$message = 'A title is required.';
			return array('taskform', $message, $_POST);
		}
	
		$title = $_POST['title'];
		$category = $_POST['category'] ? $_POST['category'] : 'uncategorized';
		$description = $_POST['description'] ? $_POST['description'] : "";

		// Create connection
		require('db_credentials.php');
		$mysqli = new mysqli($servername, $username, $password, $dbname);

		// Check connection
		if ($mysqli->connect_error) {
			$message = $mysqli->connect_error;
		} else {
			$category = $mysqli->real_escape_string($category);
			$title = $mysqli->real_escape_string($title);
			$description = $mysqli->real_escape_string($description);
	
			$sql = "INSERT INTO tasks (title, description, category, addDate) VALUES ('$title', '$description', '$category', NOW())";
	
			if ($result = $mysqli->query($sql)) {
				$message = "Task was added";
			} else {
				$message = $mysqli->error;
			}

		}
		
		return array('', $message);
	}
	
	function editTask() {
		$id = $_POST['id'];
	
		$message = "";
	
		if (!$id) {
			$message = "No task was specified to edit.";
			return array('', $message);
		} else {
			// Create connection
			require('db_credentials.php');
			$mysqli = new mysqli($servername, $username, $password, $dbname);
			// Check connection
			if ($mysqli->connect_error) {
				$message = $mysqli->connect_error;
				return array('', $message);
			} else {
				$id = $mysqli->real_escape_string($id);
				$sql = "SELECT * FROM tasks WHERE id = $id";
				if ( $result = $mysqli->query($sql) ) {
					if ($result->num_rows > 0) {
						$data = $result->fetch_assoc();
						$result->close();
						$mysqli->close();
						return array('taskform', '', $data);
					} else {
						$message = "No task was found to edit.";
						$mysqli->close();
						return array('', $message);					
					}
				} else {
					$message = $mysqli->error;
					return array('', $message);
				}
				
			}
		}
	
	}
	
	function updateTask() {
		$message = "";
		
		if ($_POST['cancel']) {
			$message = 'Editing task was cancelled.';
			return array('', $message);
		}
		
		$id = $_POST['id'];

		if (!$id) {
			$message = "No task was specified to update.";
			return array('', $message);
		}		
		
		$title = $_POST['title'];
		$description = $_POST['description'];
		$category = $_POST['category'];
		
		if (!$title) {
			$message = 'A title is required.';
			return array('taskform', $message, $_POST);
		}
	
		// Create connection
		require('db_credentials.php');
		$mysqli = new mysqli($servername, $username, $password, $dbname);
		// Check connection
		if ($mysqli->connect_error) {
			$message = $mysqli->connect_error;
			return array('', $message);
		} else {
			$id = $mysqli->real_escape_string($id);
			$title = $mysqli->real_escape_string($title);
			$description = $mysqli->real_escape_string($description);
			$category = $mysqli->real_escape_string($category);
			$sql = "UPDATE tasks SET title='$title', description='$description', category='$category' WHERE id = $id";
			if ( $result = $mysqli->query($sql) ) {
				$message = 'Task was updated.';	
			} else {
				$message = $mysqli->error;
			}
			return array('', $message);
			$mysqli->close();
		}
	
	}
	
?>