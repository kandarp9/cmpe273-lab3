
cd ..

start cmd /k java -jar target/server-0.0.1-SNAPSHOT.jar server config/server_A_config.yml

start cmd /k java -jar target/server-0.0.1-SNAPSHOT.jar server config/server_B_config.yml

start cmd /k java -jar target/server-0.0.1-SNAPSHOT.jar server config/server_C_config.yml