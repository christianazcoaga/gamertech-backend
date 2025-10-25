# JWT Implementation - Guía de Uso

## 🔐 Autenticación JWT Implementada

Se ha implementado autenticación basada en **JWT (JSON Web Tokens)** en la aplicación Spring Boot.

## 📋 Cambios Realizados

### 1. **Dependencias Agregadas** (pom.xml)
- `jjwt-api` v0.12.3
- `jjwt-impl` v0.12.3
- `jjwt-jackson` v0.12.3

### 2. **Nuevos Componentes**

#### `JwtService.java`
Servicio para generar y validar tokens JWT:
- `generateToken()` - Genera un token para el usuario
- `extractUsername()` - Extrae el email del token
- `isTokenValid()` - Valida si el token es válido

#### `JwtAuthenticationFilter.java`
Filtro que intercepta todas las peticiones HTTP para validar el token JWT en el header `Authorization`.

#### `SecurityConfig.java` (Actualizado)
Configuración de seguridad actualizada para:
- Integrar el filtro JWT
- Mantener sesiones STATELESS
- Configurar endpoints públicos y protegidos

### 3. **Configuración** (application.properties)
```properties
jwt.secret.key=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
jwt.expiration=86400000  # 24 horas en milisegundos
```

⚠️ **IMPORTANTE**: En producción, cambia la `jwt.secret.key` por una clave segura única.

### 4. **DTOs Actualizados**

#### `AuthenticationResponse.java`
Ahora incluye el campo `token`:
```json
{
  "message": "Login exitoso",
  "user": { ... },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## 🚀 Cómo Usar

### 1. **Registro de Usuario**
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "name": "John",
  "apellido": "Doe"
}
```

**Respuesta:**
```json
{
  "message": "Usuario registrado exitosamente",
  "user": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "name": "John",
    "apellido": "Doe",
    "role": "USER",
    "createdAt": "2025-10-25T10:30:00"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 2. **Login**
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}
```

**Respuesta:**
```json
{
  "message": "Login exitoso",
  "user": { ... },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

### 3. **Usar el Token JWT**

Para acceder a endpoints protegidos, incluye el token en el header `Authorization`:

```http
GET /api/categories
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

```http
POST /api/categories
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json

{
  "name": "Gaming",
  "description": "Productos gaming"
}
```

## 🔒 Endpoints Protegidos

### Públicos (No requieren token):
- ✅ `POST /api/auth/register`
- ✅ `POST /api/auth/login`
- ✅ `GET /api/productos/**`
- ✅ `GET /api/categories/**`

### Requieren Autenticación (Token JWT):
- 🔐 `POST /api/productos/**`
- 🔐 `PUT /api/productos/**`
- 🔐 `DELETE /api/productos/**`
- 🔐 `POST /api/categories/**`
- 🔐 `PUT /api/categories/**`
- 🔐 `DELETE /api/categories/**`
- 🔐 `POST /api/pedidos`
- 🔐 `GET /api/pedidos/mis-pedidos`

### Requieren Rol ADMIN:
- 👑 `GET /api/pedidos/**`
- 👑 `PUT /api/pedidos/*/estado`
- 👑 `DELETE /api/pedidos/**`

## 🧪 Probar con Postman/Thunder Client

1. **Registrarse o hacer login** → Guarda el `token` de la respuesta
2. **En las siguientes peticiones**, agrega el header:
   - **Key:** `Authorization`
   - **Value:** `Bearer <tu-token-aquí>`

## 🔑 Expiración del Token

- **Duración:** 24 horas
- Después de expirar, el usuario debe hacer login nuevamente
- Puedes cambiar la duración en `application.properties` (valor en milisegundos)

## 🛡️ Seguridad

- Las contraseñas se encriptan con **BCrypt**
- Los tokens están firmados con **HS256**
- Las sesiones son **STATELESS** (no se almacenan en el servidor)
- Cada petición debe incluir el token JWT válido

## 📝 Notas Adicionales

- El token incluye el **email del usuario** como `subject`
- La validación del token ocurre automáticamente en cada petición
- Si el token es inválido o expiró, se devolverá `401 Unauthorized`
