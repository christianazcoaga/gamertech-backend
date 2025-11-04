# Build stage (Etapa de Construcción)
# ----------------------------------------------------------------------
# 1. FROM: Inicia la primera etapa usando una imagen que tiene Maven y JDK 17. 
#    Se le asigna el nombre 'build' para referenciarla más tarde.
FROM maven:3.8.4-openjdk-17-slim AS build

# 2. WORKDIR: Define el directorio de trabajo dentro del contenedor.
WORKDIR /app

# 3. COPY: Copia el archivo de configuración de Maven (pom.xml) al contenedor.
COPY pom.xml .

# 4. COPY: Copia el código fuente (carpeta src) al contenedor.
COPY src ./src

# 5. RUN: Ejecuta Maven para compilar, resolver dependencias y empaquetar 
#    la aplicación en un .jar. Se omiten las pruebas para acelerar el proceso.
RUN mvn clean package -DskipTests

# Run stage (Etapa de Ejecución)
# ----------------------------------------------------------------------
# 6. FROM: Inicia la etapa final con una imagen mínima que solo tiene el JRE 17.
#    Esto reduce drásticamente el tamaño de la imagen final.
FROM openjdk:17-jdk-slim

# 7. WORKDIR: Define el directorio de trabajo por defecto para la ejecución.
WORKDIR /app

# 8. COPY --from=build: Copia SOLAMENTE el archivo JAR final (generado en la etapa 'build')
#    y lo nombra como app.jar en esta nueva imagen ligera.
COPY --from=build /app/target/e_commerce-0.0.1-SNAPSHOT.jar app.jar

# 9. EXPOSE: Documenta el puerto en el que la aplicación Spring Boot estará escuchando.
EXPOSE 8080

# 10. ENTRYPOINT: Define el comando que se ejecutará al iniciar el contenedor,
#     lanzando la aplicación Java.
ENTRYPOINT ["java", "-jar", "app.jar"]
