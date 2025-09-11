# 📝 MyBlog App BE

Aplikasi **Blog & Portfolio Management** yang dibangun dengan **Kotlin Spring Boot (backend)** dan **Vue/Nuxt (frontend)**.  
App ini berfungsi ganda: sebagai **media portofolio pribadi** untuk menampilkan karya, serta **blog management system** untuk mengelola artikel, project, dan konten lainnya.


sdk list java   
//build
./gradlew clean bootJar          # build dengan test

scp build/libs/myblog-0.0.1-SNAPSHOT.jar ringga-dev@103.175.221.41:~/BE/my-blog/app.jar
sudo rm -rf /home/ringga-dev/BE/my-blog/*

//running
nohup java -jar app.jar > app.log 2>&1 &

// cek online
ps aux | grep java
