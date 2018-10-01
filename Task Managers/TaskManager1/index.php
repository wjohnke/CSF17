<?php
	require ('db_credentials.php');
	require ('web_utils.php');
	
	$stylesheet = 'taskmanager.css';
	
	// Create connection
	$mysqli = new mysqli($servername, $username, $password, $dbname);

	// Check connection
	if ($mysqli->connect_error) {
		print generatePageHTML("Tasks (Error)", generateErrorPageHTML($mysqli->connect_error), $stylesheet);
		exit;
	}
	
	$sql = "SELECT * FROM tasks";
	$result = $mysqli->query($sql);
	$tasks = array();
	if ($result->num_rows > 0) {
		while($row = $result->fetch_assoc()) {
			array_push($tasks, $row);
		}
	}
	
	print generatePageHTML("Tasks", generateTaskTableHTML($tasks), $stylesheet);
	
	
	
	function generateTaskTableHTML($tasks) {
		$html = "<h1>Tasks</h1>\n";
		
		$html .= "<p><a class='taskButton' href='task_form.html'>+ Add Task</a></p>\n";
	
		if (count($tasks) < 1) {
			$html .= "<p>No tasks to display!</p>\n";
			return $html;
		}
	
		$html .= "<table>\n";
		$html .= "<tr><th>actions</th><th>add date</th><th>completed date</th><th>title</th><th>description</th><th>category</th></tr>\n";
	
		foreach ($tasks as $task) {
			$id = $task['id'];
			$addDate = $task['addDate'];
			$completedDate = ($task['completedDate']) ? $task['completedDate'] : '';
			$title = $task['title'];
			$description = ($task['description']) ? $task['description'] : '';
			$category = $task['category'];
			
			$html .= "<tr><td><form action='delete_task.php' method='post'><input type='hidden' name='id' value='$id' /><input type='submit' value='Delete'></form></td><td>$addDate</td><td>$completedDate</td><td>$title</td><td>$description</td><td>$category</td></tr>\n";
		}
		$html .= "</table>\n";
	
		return $html;
	}
	
	function generateErrorPageHTML($error) {
	$html = <<<EOT
<h1>Tasks</h1>
<p>An error occurred: $error</p>
EOT;

	return $html;
	}

?>