<?php
	require ('db_credentials.php');
	require ('web_utils.php');
	
	$stylesheet = 'taskmanager.css';
	
	$category = $_POST['category'];
	$title = $_POST['title'] ? $_POST['title'] : "untitled";
	$description = $_POST['description'] ? $_POST['description'] : "";
	
	
	// Create connection
	$mysqli = new mysqli($servername, $username, $password, $dbname);

	// Check connection
	if ($mysqli->connect_error) {
		print generatePageHTML("Tasks (Error)", generateErrorPageHTML($mysqli->connect_error), $stylesheet);
		exit;
	}
	
	$category = $mysqli->real_escape_string($category);
	$title = $mysqli->real_escape_string($title);
	$description = $mysqli->real_escape_string($description);
	
	$sql = "INSERT INTO tasks (title, description, category, addDate) VALUES ('$title', '$description', '$category', NOW())";
	
	$result = $mysqli->query($sql);
	if ($result) {
		// insert successfull, redirect browser to index.php to see list of tasks
		redirect("index.php");
	} else {
		print generatePageHTML("Tasks (Error)", generateErrorPageHTML($mysqli->error . " using SQL: $sql"), $stylesheet);
		exit;
	}
	
	
	function generateErrorPageHTML($error) {
	$html = <<<EOT
<h1>Tasks</h1>
<p>An error occurred: $error</p>
<p><a class='taskButton' href='task_form.html'>Add Task</a><a class='taskButton' href='view_tasks.php'>View Tasks</a></p>
EOT;

	return $html;
	}
?>