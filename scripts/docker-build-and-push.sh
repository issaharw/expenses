export AWS_PROFILE=exp

./gradlew clean build

docker build . -t expenses-be

aws ecr get-login-password --region eu-central-1 | docker login --username AWS --password-stdin 060795920724.dkr.ecr.eu-central-1.amazonaws.com

docker tag expenses-be:latest 060795920724.dkr.ecr.eu-central-1.amazonaws.com/expenses:latest

docker push 060795920724.dkr.ecr.eu-central-1.amazonaws.com/expenses:latest