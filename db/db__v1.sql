CREATE TABLE `Expenses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expense_date` date NOT NULL,
  `charge_date` date NOT NULL,
  `amount` float(2) NOT NULL,
  `name` varchar(300) NOT NULL,
  `asmachta` bigint DEFAULT NULL,
  `original_amount` float(2) DEFAULT NULL,
  `details` varchar(300) DEFAULT NULL,
  `expense_type` int NOT NULL,
  `creation_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


CREATE TABLE `Categories` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `category` varchar(200) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


CREATE TABLE `ExpenseCategory` (
  `expense_name` varchar(300) NOT NULL
  `category_name` varchar(200) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
