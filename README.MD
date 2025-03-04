mvn clean package

mvn spring-boot:run

http://localhost:8084/swagger-ui/index.html

java -jar target/pv212.jar

mvn clean verify

mvn test

mvn dependency:resolve

mvn clean

java -jar target/pv212.jar --server.port=8084

```
docker build -t pv212-java . 
docker images --all
docker run -it --rm -p 5068:8084 --name pv212_container pv212-java
docker run -d --restart=always --name pv212_container -p 5068:8084 pv212-java
docker run -d --restart=always -v d:/volumes/pv212/images:/app/images --name pv212_container -p 5068:8084 pv212-java
docker run -d --restart=always -v /volumes/pv212/images:/app/images --name pv212_container -p 5068:8084 pv212-java
docker ps -a
docker stop pv212_container
docker rm pv212_container

docker images --all
docker rmi pv212-java

docker login
docker tag pv212-java:latest novakvova/pv212-java:latest
docker push novakvova/pv212-java:latest

docker pull novakvova/pv212-java:latest
docker ps -a
docker run -d --restart=always -v /volumes/pv212/images:/app/images --name pv212_container -p 5068:8084 novakvova/pv212-java


docker pull novakvova/pv212-java:latest
docker images --all
docker ps -a
docker stop pv212_container
docker rm pv212_container
docker run -d --restart=always --name pv212_container -p 5068:8084 novakvova/pv212-java

---------------/etc/nginx/sites-available/--------------------------

server {
    server_name   slush.itstep.click *.slush.itstep.click;
    location / {
       proxy_pass         http://localhost:5068;
       proxy_http_version 1.1;
       proxy_set_header   Upgrade $http_upgrade;
       proxy_set_header   Connection keep-alive;
       proxy_set_header   Host $host;
       proxy_cache_bypass $http_upgrade;
       proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
       proxy_set_header   X-Forwarded-Proto $scheme;
    }
}

sudo nginx -t
sudo systemctl restart nginx
sudo systemctl status nginx
```mvn clean package

mvn spring-boot:run