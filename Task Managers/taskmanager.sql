CREATE TABLE tasks (
   id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
   title VARCHAR(255) NOT NULL,
   description MEDIUMTEXT,
   category ENUM('personal', 'school', 'work', 'uncategorized') DEFAULT 'uncategorized',
   addDate DATETIME NOT NULL,
   completedDate DATETIME,
   completed char(1) DEFAULT 'N'
);

INSERT INTO tasks (title, description, category, addDate) VALUES ('Task manager', 'Develop a web-based task manager.', 'work', NOW());
INSERT INTO tasks (title, description, category, addDate) VALUES ('Database homework', 'Complete database homework.', 'school', NOW());
INSERT INTO tasks (title, description, category, addDate) VALUES ('Get groceries', 'Get milk, eggs, and apples.', 'personal', NOW());
INSERT INTO tasks (title, description, category, addDate) VALUES ('Task manager', 'Develop a web-based task manager.', 'work', NOW());
INSERT INTO tasks (title, description, category, addDate) VALUES ('Database homework', 'Complete database homework.', 'school', NOW());
INSERT INTO tasks (title, description, category, addDate) VALUES ('Code Review', 'Perform code review of news app.', 'work', NOW());
INSERT INTO tasks (title, description, category, addDate) VALUES ('Buy Halloween Candy', 'Purchase halloween candy for trick-or-treat.', 'personal', NOW());
INSERT INTO tasks (title, description, category, addDate) VALUES ('Buy an Apple Watch', 'Buy a series 3 LTE Apple Watch.', 'school', NOW());
