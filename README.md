El backend de este proyecto e-commerce está desarrollado con Java usando el framework Spring Boot, que proporciona una arquitectura modular y escalable para la construcción de APIs REST eficientes.

La aplicación utiliza Spring Security junto con JWT (JSON Web Tokens) para gestionar la autenticación y autorización de usuarios de forma segura y sin estado (stateless). Los usuarios pueden tener roles definidos, principalmente USER y ADMIN, que controlan el acceso a distintos endpoints y funcionalidades dentro de la API.

Para la persistencia de datos se emplea una base de datos MySQL, integrada mediante Hibernate JPA, lo que facilita el mapeo objeto-relacional y la gestión automática del esquema de base. En entornos de desarrollo también está habilitada la consola H2 para pruebas rápidas con base de datos en memoria.

El backend aplica una política CORS configurada para permitir acceso desde el frontend React alojado localmente (puertos 5173, 5137, etc.), garantizando la interoperabilidad entre cliente y servidor.

Al iniciar la aplicación, se inicializan datos base como categorías de productos y usuarios de prueba con roles ADMIN y USER, utilizando contraseñas cifradas con BCrypt para aumentar la seguridad.

En resumen, el backend de este e-commerce combina tecnologías modernas de Java y Spring Boot para ofrecer una API segura, escalable y fácil de mantener, con enfoque en autenticación con JWT, control de acceso basado en roles, y buena integración con el frontend React y la base de datos MySQL.
