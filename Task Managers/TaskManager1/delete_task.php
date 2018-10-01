<?php
	require ('db_credentials.php');
	require ('web_utils.php');
	
	$id = $_POST['id'] ? $_POST['id'] : redirect("index.php");
	
	// Create connection
	$mysqli = new mysqli($servername, $username, $password, $dbname);

	// Check connection
	if ($mysqli->connect_error) {
		redirect("index.php");
	}
	
	$id = $mysqli->real_escape_string($id);
	
	$sql = "DELETE FROM tasks WHERE id = $id";
	
	$result = $mysqli->query($sql);
	
	redirect("index.php");
?>