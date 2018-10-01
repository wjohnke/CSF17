<?php

	class TasksViews {
		private $stylesheet = 'taskmanager.css';
		private $pageTitle = 'Tasks';
		
		public function __construct() {

		}
		
		public function __destruct() {

		}
		
		public function taskListView($tasks, $orderBy = 'title', $orderDirection = 'asc', $message = '') {
			$body = "<h1>Tasks</h1>\n";
		
			if ($message) {
				$body .= "<p class='message'>$message</p>\n";
			}
		
			$body .= "<p><a class='taskButton' href='index.php?view=taskform'>+ Add Task</a></p>\n";
	
			if (count($tasks) < 1) {
				$body .= "<p>No tasks to display!</p>\n";
				return $body;
			}
	
			$body .= "<table>\n";
			$body .= "<tr><th>delete</th><th>edit</th><th>completed</th>";
		
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
				$body .= "<th><a class='order' href='index.php?orderby=$name'>$label</a></th>";
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
			
				$body .= "<tr>";
				$body .= "<td><form action='index.php' method='post'><input type='hidden' name='action' value='delete' /><input type='hidden' name='id' value='$id' /><input type='submit' value='Delete'></form></td>";
				$body .= "<td><form action='index.php' method='post'><input type='hidden' name='action' value='edit' /><input type='hidden' name='id' value='$id' /><input type='submit' value='Edit'></form></td>";
				$body .= "<td><form action='index.php' method='post'><input type='hidden' name='action' value='$completedAction' /><input type='hidden' name='id' value='$id' /><input type='submit' value='$completedLabel'></form></td>";
				$body .= "<td>$addDate</td><td>$completedDate</td><td>$title</td><td>$description</td><td>$category</td>";
				$body .= "</tr>\n";
			}
			$body .= "</table>\n";
	
			return $this->page($this->pageTitle, $body, $this->stylesheet);
		}
		
		public function taskFormView($data = null, $message = '') {
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
		
		public function errorView($message) {
			$body = "<h1>Tasks</h1>\n";
			$body .= "<p>$message</p>\n";
			
			return $this->page($this->pageTitle, $body, $this->stylesheet);
		}
		
		private function page($title, $body, $stylesheet) {
			$html = <<<EOT
<!DOCTYPE html>
<html>
<head>
<title>$title</title>
<link rel="stylesheet" type="text/css" href="{$this->stylesheet}">
</head>
<body>
$body
<p>&copy; 2017 Dale Musser. All rights reserved.</p>
</body>
</html>
EOT;
			return $html;
		}

}