<?php
	// Model-View-Controller implementation of Task Manager
	
	require('TasksController.php');

	$controller = new TasksController();
	$controller->run();
?>