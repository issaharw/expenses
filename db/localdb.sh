docker run --name exp-db -p 3306:3306 -v /Users/issahar/Work/expenses-db:/var/lib/mysql -e MYSQL_ROOT_PASSWORD=jalingA1 -e MYSQL_DATABASE=expenses -d mysql:8.2.0