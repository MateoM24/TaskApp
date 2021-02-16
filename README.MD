### Para poner la aplicación en marcha:
1) Descarga archivo jar (TaskApp.jar) de la aplicación TaskApp.
2) Asegurate que tienes java versión 11 instalado y que se encuentra en variable PATH de su sistema
3) Asegurate que puerto 8080 está libre.
4) Dirigite a donde has descargado archivo TaskApp.jar
5) En la terminal pon:
   ```java -jar TaskApp.jar```
6) La aplicación debe estar lista en poco tiempo.


### La mejor manera de descubrir como funciona REST API de la aplicación es a través de:
http://localhost:8080/swagger-ui.html

Swagger-UI facilita todos los métodos que la aplicación expone para manejar tareas.
Ademas, abajo hay descripción de DTOs (Data Transfer Objects).

Descripción de REST Api en formato json se encuentra aquí:  
http://localhost:8080/v2/api-docs

#### La base de datos
La aplicación hace usu de base de datos H2 que corre en memoria.
Se puede acceder a consola de H2:
http://localhost:8080/h2-console

El accesso:
Driver Class: org.h2.Driver
JDBC URL: jdbc:h2:mem:tasksapp
User Name: sa
Password:

*Teniendo códico fuente a disposición se puede generar archivo jar poniendo en la terminal:
mvn clean package spring-boot:repackage
El archivo ejecutable aparece en subcarpeta /target.

### Para cualquier pregunta no dudéis en contactar conmigo!