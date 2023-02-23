ALTER TABLE Exams
ADD COLUMN user_data json DEFAULT NULL AFTER exam_time;
