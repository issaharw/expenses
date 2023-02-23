CREATE TABLE `Clips` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `instance_uid` varchar(200) NOT NULL,
  `exam_id` bigint NOT NULL,
  `clip_time` datetime DEFAULT NULL,
  `clip_type` int NOT NULL,
  `status` int NOT NULL DEFAULT '0',
  `images_json` json DEFAULT NULL,
  `network_results` json DEFAULT NULL,
  `cine_rate` int DEFAULT '0',
  `creation_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `instance_uid_UNIQUE` (`instance_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Exams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `study_uid` varchar(200) NOT NULL,
  `patient_id` bigint NOT NULL,
  `status` int NOT NULL DEFAULT (0),
  `exam_time` timestamp(3) NULL DEFAULT NULL,
  `user_data` json DEFAULT NULL,
  `performing` varchar(200) DEFAULT NULL,
  `attending` varchar(200) DEFAULT NULL,
  `accession_number` varchar(200) DEFAULT NULL,
  `manufacturer_model` varchar(200) DEFAULT NULL,
  `transducer_data` varchar(200) DEFAULT NULL,
  `processing_function` varchar(200) DEFAULT NULL,
  `creation_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`),
  UNIQUE KEY `study_uid_UNIQUE` (`study_uid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Patients` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `mrn` varchar(255) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `middle_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `birth_date` datetime DEFAULT NULL,
  `creation_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;