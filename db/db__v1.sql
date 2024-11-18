CREATE TABLE `Users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `is_active` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Categories` (
  `user_id` bigint NOT NULL,
  `name` varchar(200) NOT NULL,
  `parent_category` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `BudgetItems` (
  `user_id` bigint NOT NULL,
  `budget_month` varchar(10) NOT NULL,
  `category_name` varchar(200) NOT NULL,
  `amount` bigint DEFAULT NULL,
  PRIMARY KEY (`user_id`, `budget_month`, `category_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `Expenses` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `expense_date` date NOT NULL,
  `name` varchar(300) NOT NULL,
  `amount` float(2) NOT NULL,
  `budget_month` varchar(10) NOT NULL,
  `asmachta` bigint DEFAULT NULL,
  `original_amount` float(2) DEFAULT NULL,
  `details` varchar(300) DEFAULT NULL,
  `expense_type` int NOT NULL,
  `category_name` varchar(200) DEFAULT NULL,
  `creation_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

CREATE TABLE `ExpenseNameCategory` (
  `user_id` bigint NOT NULL,
  `expense_name` varchar(300) NOT NULL,
  `category_name` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;


