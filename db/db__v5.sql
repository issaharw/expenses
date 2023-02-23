ALTER TABLE Exams
ADD COLUMN `performing` varchar(200) DEFAULT NULL AFTER user_data;
ALTER TABLE Exams
ADD COLUMN `attending` varchar(200) DEFAULT NULL AFTER performing;
ALTER TABLE Exams
ADD COLUMN `accession_number` varchar(200) DEFAULT NULL AFTER attending;
ALTER TABLE Exams
ADD COLUMN `manufacturer_model` varchar(200) DEFAULT NULL AFTER accession_number;
ALTER TABLE Exams
ADD COLUMN `transducer_data` varchar(200) DEFAULT NULL AFTER manufacturer_model;
ALTER TABLE Exams
ADD COLUMN `processing_function` varchar(200) DEFAULT NULL AFTER transducer_data;