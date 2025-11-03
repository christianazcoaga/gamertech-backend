# 📡 API Documentation - Frontend Integration Guide

## 📑 Tabla de Contenidos

1. [Base URL](#-base-url)
2. [Resumen de Endpoints](#-resumen-de-endpoints)
3. [Configuración de CORS](#-configuración-de-cors)
4. [Autenticación](#-autenticación)
5. [Endpoints API Detallados](#-endpoints-api-detallados)
   - [Autenticación (`/api/auth`)](#1-autenticación-apiauth)
   - [Categorías (`/api/categories`)](#2-categorías-apicategories)
   - [Productos (`/api/products`)](#3-productos-apiproducts)
   - [Pedidos (`/api/pedidos`)](#4-pedidos-apipedidos)
6. [Manejo del Token JWT](#-manejo-del-token-jwt)
7. [Códigos de Estado HTTP](#-códigos-de-estado-http)
8. [Solución de Problemas Comunes](#️-solución-de-problemas-comunes)
9. [Formato de Errores](#️-formato-de-errores)
10. [Ejemplos de Flujo Completo](#-ejemplo-de-flujo-completo)
11. [Checklist de Integración](#-checklist-de-integración-frontend)
12. [Configuración del Backend](#-configuración-del-backend)
13. [Seguridad y Mejores Prácticas](#-seguridad-y-mejores-prácticas)
14. [Recursos Adicionales](#-recursos-adicionales)
15. [Soporte](#-soporte)

---

## 🌐 Base URL
```
http://localhost:8080
```

---

## 📋 Resumen de Endpoints

### 🔓 Endpoints Públicos (No requieren autenticación)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| `POST` | `/api/auth/register` | Registrar nuevo usuario |
| `POST` | `/api/auth/login` | Iniciar sesión (obtener JWT) |
| `GET` | `/api/products` | Listar todos los productos |
| `GET` | `/api/products/{id}` | Obtener producto por ID |
| `GET` | `/api/products/category/{category}` | Filtrar productos por categoría |
| `GET` | `/api/categories` | Listar todas las categorías |
| `GET` | `/api/categories/{id}` | Obtener categoría por ID |

### 🔒 Endpoints Protegidos (Requieren autenticación)

| Método | Endpoint | Rol | Descripción |
|--------|----------|-----|-------------|
| **PRODUCTOS** |
| `POST` | `/api/products` | USER | Crear nuevo producto |
| `PUT` | `/api/products/{id}` | USER | Actualizar producto |
| `DELETE` | `/api/products/{id}` | USER | Eliminar producto |
| **CATEGORÍAS** |
| `POST` | `/api/categories` | USER | Crear nueva categoría |
| `PUT` | `/api/categories/{id}` | USER | Actualizar categoría |
| `DELETE` | `/api/categories/{id}` | USER | Eliminar categoría |
| **PEDIDOS (Usuario)** |
| `POST` | `/api/pedidos` | USER | Crear nuevo pedido |
| `GET` | `/api/pedidos/mis-pedidos` | USER | Ver mis pedidos |
| `PUT` | `/api/pedidos/{id}/cancelar` | USER | Cancelar mi pedido |
| **PEDIDOS (Admin)** |
| `GET` | `/api/pedidos` | ADMIN | Listar todos los pedidos |
| `GET` | `/api/pedidos/{id}` | ADMIN | Obtener pedido por ID |
| `GET` | `/api/pedidos/estado/{estado}` | ADMIN | Filtrar pedidos por estado |
| `PUT` | `/api/pedidos/{id}/estado?estado={estado}` | ADMIN | Actualizar estado del pedido |
| `DELETE` | `/api/pedidos/{id}` | ADMIN | Eliminar pedido |

### 🔑 Headers Requeridos

**Para endpoints públicos:**
```http
Content-Type: application/json
```

**Para endpoints protegidos:**
```http
Content-Type: application/json
Authorization: Bearer <tu_token_jwt>
```

### 📊 Estados de Pedido Válidos
- `PENDIENTE` - Pedido creado, esperando confirmación
- `PROCESANDO` - Pedido en proceso
- `ENVIADO` - Pedido enviado al cliente
- `ENTREGADO` - Pedido entregado exitosamente
- `CANCELADO` - Pedido cancelado

---

## 🌍 Configuración de CORS

La API está configurada para aceptar peticiones desde los siguientes orígenes:
- `http://localhost:5173` (Vite default)
- `http://localhost:5137`
- `http://127.0.0.1:5173`

### Configuración Implementada

**Métodos HTTP permitidos:**
- GET
- POST
- PUT
- DELETE
- OPTIONS

**Headers permitidos:** Todos (`*`)

**Credenciales:** Habilitadas (permite envío de cookies y tokens de autorización)

### ⚙️ Configuraciones de CORS Disponibles

#### **Opción 1: Configuración Global (Implementada)** ✅
La configuración global está definida en `SecurityConfig.java` y se aplica a todos los endpoints automáticamente. Esta es la configuración recomendada y ya está activa.

**Ubicación:** `com.api.e_commerce.config.SecurityConfig`

**Ventajas:**
- Configuración centralizada
- Más fácil de mantener
- Se aplica a toda la aplicación

#### **Opción 2: Configuración por Controlador** (También implementada)
Cada controlador tiene la anotación `@CrossOrigin` para mayor flexibilidad. Esto permite configuraciones específicas por endpoint si es necesario.

**Ejemplo:**
```java
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5137"})
@RestController
@RequestMapping("/api/products")
public class ProductController { ... }
```

**Ventajas:**
- Control granular por endpoint
- Útil para restricciones específicas

### 🔧 Agregar Nuevo Origen

Si tu frontend corre en un puerto diferente, actualiza la lista de orígenes permitidos en `SecurityConfig.java`:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:5173", 
    "http://localhost:5137",
    "http://tu-nuevo-origen:puerto"
));
```

---

## 🔐 Autenticación

Todos los endpoints protegidos requieren el header:
```http
Authorization: Bearer <tu_token_jwt>
```

---

## 📋 Endpoints API Detallados

### 1. AUTENTICACIÓN (`/api/auth`)

#### 1.1 Registro de Usuario
**POST** `/api/auth/register`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "password123",
  "name": "John",
  "apellido": "Doe"
}
```

**Respuesta Exitosa (201):**
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
    "createdAt": "2025-10-28T10:30:00"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Errores:**
- `400` - Email o username ya existe
- `400` - Validación fallida (campos requeridos)

---

#### 1.2 Login
**POST** `/api/auth/login`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "email": "john@example.com",
  "password": "password123"
}
```

**Respuesta Exitosa (200):**
```json
{
  "message": "Login exitoso",
  "user": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com",
    "name": "John",
    "apellido": "Doe",
    "role": "USER",
    "createdAt": "2025-10-28T10:30:00"
  },
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Errores:**
- `400` - Credenciales inválidas

---

### 2. CATEGORÍAS (`/api/categories`)

#### 2.1 Obtener todas las categorías
**GET** `/api/categories`

**Headers:**
```
Ninguno requerido (endpoint público)
```

**Respuesta Exitosa (200):**
```json
[
  {
    "id": 1,
    "name": "Gaming",
    "description": "Productos para gamers",
    "createdAt": "2025-10-28T10:00:00",
    "updatedAt": "2025-10-28T10:00:00"
  },
  {
    "id": 2,
    "name": "Hardware",
    "description": "Componentes de PC",
    "createdAt": "2025-10-28T10:05:00",
    "updatedAt": "2025-10-28T10:05:00"
  }
]
```

---

#### 2.2 Obtener categoría por ID
**GET** `/api/categories/{id}`

**Headers:**
```
Ninguno requerido (endpoint público)
```

**URL Params:**
- `id` (Long): ID de la categoría

**Ejemplo:** `/api/categories/1`

**Respuesta Exitosa (200):**
```json
{
  "id": 1,
  "name": "Gaming",
  "description": "Productos para gamers",
  "createdAt": "2025-10-28T10:00:00",
  "updatedAt": "2025-10-28T10:00:00"
}
```

**Errores:**
- `404` - Categoría no encontrada

---

#### 2.3 Crear categoría
**POST** `/api/categories`

🔒 **Requiere autenticación**

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Body:**
```json
{
  "name": "Gaming",
  "description": "Productos para gamers"
}
```

**Validaciones:**
- `name`: Requerido, mínimo 3 caracteres
- `description`: Opcional, máximo 1000 caracteres

**Respuesta Exitosa (201):**
```json
{
  "id": 1,
  "name": "Gaming",
  "description": "Productos para gamers",
  "createdAt": "2025-10-28T10:00:00",
  "updatedAt": "2025-10-28T10:00:00"
}
```

**Errores:**
- `400` - Nombre duplicado
- `400` - Validación fallida
- `401` - No autenticado

---

#### 2.4 Actualizar categoría
**PUT** `/api/categories/{id}`

🔒 **Requiere autenticación**

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**URL Params:**
- `id` (Long): ID de la categoría

**Body:**
```json
{
  "name": "Gaming Pro",
  "description": "Productos gaming profesionales"
}
```

**Respuesta Exitosa (200):**
```json
{
  "id": 1,
  "name": "Gaming Pro",
  "description": "Productos gaming profesionales",
  "createdAt": "2025-10-28T10:00:00",
  "updatedAt": "2025-10-28T11:00:00"
}
```

**Errores:**
- `400` - Nombre duplicado
- `404` - Categoría no encontrada
- `401` - No autenticado

---

#### 2.5 Eliminar categoría
**DELETE** `/api/categories/{id}`

🔒 **Requiere autenticación**

**Headers:**
```
Authorization: Bearer <token>
```

**URL Params:**
- `id` (Long): ID de la categoría

**Respuesta Exitosa (204):**
```
No Content
```

**Errores:**
- `404` - Categoría no encontrada
- `409` - Categoría tiene productos asociados
- `401` - No autenticado

---

### 3. PRODUCTOS (`/api/products`)

#### 3.1 Obtener todos los productos
**GET** `/api/products`

**Headers:**
```
Ninguno requerido (endpoint público)
```

**Query Params (Opcionales):**
- `page` (int): Número de página (default: 0)
- `size` (int): Tamaño de página (default: 10)

**Ejemplo:** `/api/products?page=0&size=20`

**Respuesta Exitosa (200):**
```json
[
  {
    "id": 1,
    "name": "RTX 4090",
    "description": "Tarjeta gráfica gaming",
    "price": 1599.99,
    "stock": 10,
    "imageUrl": "https://example.com/rtx4090.jpg",
    "category": {
      "id": 1,
      "name": "Hardware"
    },
    "createdAt": "2025-10-28T10:00:00",
    "updatedAt": "2025-10-28T10:00:00"
  }
]
```

---

#### 3.2 Obtener producto por ID
**GET** `/api/products/{id}`

**Headers:**
```
Ninguno requerido (endpoint público)
```

**URL Params:**
- `id` (Long): ID del producto

**Respuesta Exitosa (200):**
```json
{
  "id": 1,
  "name": "RTX 4090",
  "description": "Tarjeta gráfica gaming",
  "price": 1599.99,
  "stock": 10,
  "imageUrl": "https://example.com/rtx4090.jpg",
  "category": {
    "id": 1,
    "name": "Hardware"
  },
  "createdAt": "2025-10-28T10:00:00",
  "updatedAt": "2025-10-28T10:00:00"
}
```

**Errores:**
- `404` - Producto no encontrado

---

#### 3.3 Crear producto
**POST** `/api/products`

🔒 **Requiere autenticación**

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Body:**
```json
{
  "name": "RTX 4090",
  "description": "Tarjeta gráfica gaming",
  "price": 1599.99,
  "stock": 10,
  "imageUrl": "https://example.com/rtx4090.jpg",
  "categoryId": 1
}
```

**Validaciones:**
- `name`: Requerido
- `price`: Requerido, > 0
- `stock`: Requerido, >= 0
- `categoryId`: Requerido, debe existir

**Respuesta Exitosa (201):**
```json
{
  "id": 1,
  "name": "RTX 4090",
  "description": "Tarjeta gráfica gaming",
  "price": 1599.99,
  "stock": 10,
  "imageUrl": "https://example.com/rtx4090.jpg",
  "category": {
    "id": 1,
    "name": "Hardware"
  },
  "createdAt": "2025-10-28T10:00:00",
  "updatedAt": "2025-10-28T10:00:00"
}
```

**Errores:**
- `400` - Validación fallida
- `404` - Categoría no encontrada
- `401` - No autenticado

---

#### 3.4 Actualizar producto
**PUT** `/api/products/{id}`

🔒 **Requiere autenticación**

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**URL Params:**
- `id` (Long): ID del producto

**Body:**
```json
{
  "name": "RTX 4090 Ti",
  "description": "Tarjeta gráfica gaming mejorada",
  "price": 1799.99,
  "stock": 5,
  "imageUrl": "https://example.com/rtx4090ti.jpg",
  "categoryId": 1
}
```

**Respuesta Exitosa (200):**
```json
{
  "id": 1,
  "name": "RTX 4090 Ti",
  "description": "Tarjeta gráfica gaming mejorada",
  "price": 1799.99,
  "stock": 5,
  "imageUrl": "https://example.com/rtx4090ti.jpg",
  "category": {
    "id": 1,
    "name": "Hardware"
  },
  "createdAt": "2025-10-28T10:00:00",
  "updatedAt": "2025-10-28T11:30:00"
}
```

**Errores:**
- `400` - Validación fallida
- `404` - Producto o categoría no encontrada
- `401` - No autenticado

---

#### 3.5 Eliminar producto
**DELETE** `/api/products/{id}`

🔒 **Requiere autenticación**

**Headers:**
```
Authorization: Bearer <token>
```

**URL Params:**
- `id` (Long): ID del producto

**Respuesta Exitosa (204):**
```
No Content
```

**Errores:**
- `404` - Producto no encontrado
- `401` - No autenticado

---

#### 3.6 Obtener productos por categoría
**GET** `/api/products/category/{category}`

**Headers:**
```
Ninguno requerido (endpoint público)
```

**URL Params:**
- `category` (String): Nombre de la categoría

**Ejemplo:** `/api/products/category/Hardware`

**Respuesta Exitosa (200):**
```json
[
  {
    "id": 1,
    "name": "RTX 4090",
    "description": "Tarjeta gráfica gaming",
    "price": 1599.99,
    "stock": 10,
    "image": "https://example.com/rtx4090.jpg",
    "category": "Hardware",
    "userId": 2,
    "username": "vendedor123",
    "createdAt": "2025-10-28T10:00:00",
    "updatedAt": "2025-10-28T10:00:00"
  }
]
```

**Errores:**
- `404` - No se encontraron productos para esa categoría

---

### 4. PEDIDOS (`/api/pedidos`)

#### 4.1 Crear pedido
**POST** `/api/pedidos`

🔒 **Requiere autenticación**

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**Body:**
```json
{
  "direccionEnvio": "Calle Falsa 123",
  "telefonoContacto": "+54 11 1234-5678",
  "notas": "Entregar por la tarde",
  "items": [
    {
      "productoId": 1,
      "cantidad": 2
    },
    {
      "productoId": 3,
      "cantidad": 1
    }
  ]
}
```

**Validaciones:**
- `direccionEnvio`: Requerido
- `items`: Requerido, no vacío
- `cantidad`: > 0

**Respuesta Exitosa (201):**
```json
{
  "id": 1,
  "usuario": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com"
  },
  "fechaPedido": "2025-10-28T12:00:00",
  "estado": "PENDIENTE",
  "total": 3299.98,
  "direccionEnvio": "Calle Falsa 123",
  "telefonoContacto": "+54 11 1234-5678",
  "notas": "Entregar por la tarde",
  "items": [
    {
      "id": 1,
      "producto": {
        "id": 1,
        "name": "RTX 4090"
      },
      "cantidad": 2,
      "precioUnitario": 1599.99,
      "subtotal": 3199.98
    },
    {
      "id": 2,
      "producto": {
        "id": 3,
        "name": "Mouse Gaming"
      },
      "cantidad": 1,
      "precioUnitario": 100.00,
      "subtotal": 100.00
    }
  ],
  "fechaActualizacion": "2025-10-28T12:00:00"
}
```

**Errores:**
- `400` - Validación fallida
- `404` - Producto no encontrado
- `400` - Stock insuficiente
- `401` - No autenticado

---

#### 4.2 Ver mis pedidos
**GET** `/api/pedidos/mis-pedidos`

🔒 **Requiere autenticación**

**Headers:**
```
Authorization: Bearer <token>
```

**Respuesta Exitosa (200):**
```json
[
  {
    "id": 1,
    "fechaPedido": "2025-10-28T12:00:00",
    "estado": "PENDIENTE",
    "total": 3299.98,
    "direccionEnvio": "Calle Falsa 123",
    "items": [...]
  }
]
```

---

#### 4.3 Ver todos los pedidos (ADMIN)
**GET** `/api/pedidos`

👑 **Requiere rol ADMIN**

**Headers:**
```
Authorization: Bearer <token>
```

**Respuesta Exitosa (200):**
```json
[
  {
    "id": 1,
    "usuario": {...},
    "fechaPedido": "2025-10-28T12:00:00",
    "estado": "PENDIENTE",
    "total": 3299.98,
    "items": [...]
  }
]
```

**Errores:**
- `403` - No tiene rol ADMIN

---

#### 4.4 Obtener pedido por ID (ADMIN)
**GET** `/api/pedidos/{id}`

👑 **Requiere rol ADMIN**

**Headers:**
```
Authorization: Bearer <token>
```

**URL Params:**
- `id` (Long): ID del pedido

**Respuesta Exitosa (200):**
```json
{
  "id": 1,
  "usuario": {
    "id": 1,
    "username": "johndoe",
    "email": "john@example.com"
  },
  "fechaPedido": "2025-10-28T12:00:00",
  "estado": "PENDIENTE",
  "total": 3299.98,
  "direccionEnvio": "Calle Falsa 123",
  "items": [...]
}
```

**Errores:**
- `404` - Pedido no encontrado
- `403` - No tiene rol ADMIN

---

#### 4.5 Obtener pedidos por estado (ADMIN)
**GET** `/api/pedidos/estado/{estado}`

👑 **Requiere rol ADMIN**

**Headers:**
```
Authorization: Bearer <token>
```

**URL Params:**
- `estado` (String): Estado del pedido

**Ejemplo:** `/api/pedidos/estado/PENDIENTE`

**Estados válidos:**
- `PENDIENTE`
- `PROCESANDO`
- `ENVIADO`
- `ENTREGADO`
- `CANCELADO`

**Respuesta Exitosa (200):**
```json
[
  {
    "id": 1,
    "usuario": {...},
    "estado": "PENDIENTE",
    "total": 3299.98,
    "items": [...]
  }
]
```

**Errores:**
- `403` - No tiene rol ADMIN

---

#### 4.6 Actualizar estado del pedido (ADMIN)
**PUT** `/api/pedidos/{id}/estado`

👑 **Requiere rol ADMIN**

**Headers:**
```
Content-Type: application/json
Authorization: Bearer <token>
```

**URL Params:**
- `id` (Long): ID del pedido

**Query Params:**
- `estado` (String): Nuevo estado

**Ejemplo:** `/api/pedidos/1/estado?estado=ENVIADO`

**Estados válidos:**
- `PENDIENTE`
- `CONFIRMADO`
- `ENVIADO`
- `ENTREGADO`
- `CANCELADO`

**Respuesta Exitosa (200):**
```json
{
  "id": 1,
  "estado": "ENVIADO",
  "fechaActualizacion": "2025-10-28T14:00:00",
  ...
}
```

**Errores:**
- `404` - Pedido no encontrado
- `400` - Estado inválido
- `403` - No tiene rol ADMIN

---

#### 4.7 Cancelar mi pedido
**PUT** `/api/pedidos/{id}/cancelar`

🔒 **Requiere autenticación** (debe ser el dueño del pedido)

**Headers:**
```
Authorization: Bearer <token>
```

**URL Params:**
- `id` (Long): ID del pedido

**Respuesta Exitosa (200):**
```json
{
  "id": 1,
  "estado": "CANCELADO",
  ...
}
```

**Errores:**
- `404` - Pedido no encontrado
- `403` - No es tu pedido
- `400` - Pedido ya está en un estado final

---

## 🔑 Manejo del Token JWT

### Almacenamiento en Frontend

**Opción 1: Context API (React) - Recomendado**
```javascript
const [token, setToken] = useState(null);

// Después del login
const response = await fetch('/api/auth/login', {...});
const data = await response.json();
setToken(data.token);
```

**Opción 2: localStorage (Simple pero menos seguro)**
```javascript
localStorage.setItem('token', data.token);
```

### Uso del Token en Peticiones

```javascript
// Con fetch
const response = await fetch('http://localhost:8080/api/categories', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    name: 'Gaming',
    description: 'Productos gaming'
  })
});

// Con axios
axios.defaults.headers.common['Authorization'] = `Bearer ${token}`;
```

### Interceptor Axios (Recomendado)
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080'
});

// Interceptor para agregar el token automáticamente
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Uso
api.post('/api/categories', { name: 'Gaming' });
```

---

## 📊 Códigos de Estado HTTP

| Código | Significado |
|--------|-------------|
| `200` | OK - Petición exitosa |
| `201` | Created - Recurso creado exitosamente |
| `204` | No Content - Eliminación exitosa |
| `400` | Bad Request - Error de validación |
| `401` | Unauthorized - No autenticado |
| `403` | Forbidden - No autorizado (falta rol) |
| `404` | Not Found - Recurso no encontrado |
| `409` | Conflict - Conflicto de estado |
| `500` | Internal Server Error - Error del servidor |

---

## ⚠️ Solución de Problemas Comunes

### Error de CORS en el Navegador

**Síntoma:**
```
Access to fetch at 'http://localhost:8080/api/...' from origin 'http://localhost:5173' 
has been blocked by CORS policy: No 'Access-Control-Allow-Origin' header is present
```

**Soluciones:**

1. **Verificar que el origen está permitido**: Asegúrate de que tu frontend está corriendo en uno de los orígenes configurados (`http://localhost:5173`, `http://localhost:5137`, o `http://127.0.0.1:5173`).

2. **Reiniciar el backend**: Después de cualquier cambio en la configuración CORS, reinicia la aplicación Spring Boot.

3. **Verificar la URL exacta**: CORS es sensible al protocolo, dominio y puerto. `http://localhost:5173` es diferente de `http://127.0.0.1:5173`.

4. **Headers correctos**: Asegúrate de enviar el header `Content-Type: application/json` en peticiones POST/PUT.

**Ejemplo de petición correcta desde React:**
```javascript
// ✅ Correcto
const response = await fetch('http://localhost:8080/api/products', {
  method: 'GET',
  headers: {
    'Content-Type': 'application/json'
  }
});

// ✅ Con autenticación
const response = await fetch('http://localhost:8080/api/categories', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({ name: 'Gaming' })
});
```

### Credenciales y CORS

Si necesitas enviar cookies o credenciales:
```javascript
fetch('http://localhost:8080/api/products', {
  method: 'GET',
  credentials: 'include', // Incluye cookies
  headers: {
    'Authorization': `Bearer ${token}`
  }
});
```

---

## 🛡️ Formato de Errores

Todos los errores siguen este formato:
```json
{
  "error": "Mensaje descriptivo del error",
  "timestamp": "2025-10-28T12:00:00",
  "status": 400
}
```

**Errores de validación:**
```json
{
  "error": "Validación fallida",
  "fields": {
    "name": "El nombre es obligatorio",
    "email": "Email inválido"
  },
  "timestamp": "2025-10-28T12:00:00",
  "status": 400
}
```

---

## 🎯 Ejemplo de Flujo Completo

### Ejemplo con Fetch (Vanilla JavaScript / React)

```javascript
// 1. Login
const loginResponse = await fetch('http://localhost:8080/api/auth/login', {
  method: 'POST',
  headers: { 
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    email: 'john@example.com',
    password: 'password123'
  })
});
const { token, user } = await loginResponse.json();

// 2. Guardar token
localStorage.setItem('token', token);
localStorage.setItem('user', JSON.stringify(user));

// 3. Crear categoría (con token)
const categoryResponse = await fetch('http://localhost:8080/api/categories', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    name: 'Gaming',
    description: 'Productos gaming'
  })
});
const category = await categoryResponse.json();

// 4. Obtener productos (público, sin token)
const productsResponse = await fetch('http://localhost:8080/api/products');
const products = await productsResponse.json();

// 5. Crear pedido (con token)
const orderResponse = await fetch('http://localhost:8080/api/pedidos', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json',
    'Authorization': `Bearer ${token}`
  },
  body: JSON.stringify({
    direccionEnvio: 'Calle Falsa 123',
    telefonoContacto: '+54 11 1234-5678',
    items: [
      { productoId: 1, cantidad: 2 }
    ]
  })
});
const order = await orderResponse.json();
```

### Ejemplo con Axios

```javascript
import axios from 'axios';

// Configuración base
const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: {
    'Content-Type': 'application/json'
  }
});

// Interceptor para agregar token automáticamente
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Interceptor para manejar errores (incluyendo CORS)
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.message === 'Network Error') {
      console.error('Error de red - Verifica CORS y que el backend esté corriendo');
    }
    if (error.response?.status === 401) {
      // Token expirado o inválido
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

// Uso
async function ejemploCompleto() {
  try {
    // 1. Login
    const { data: loginData } = await api.post('/api/auth/login', {
      email: 'john@example.com',
      password: 'password123'
    });
    localStorage.setItem('token', loginData.token);
    
    // 2. Crear categoría
    const { data: category } = await api.post('/api/categories', {
      name: 'Gaming',
      description: 'Productos gaming'
    });
    
    // 3. Obtener productos
    const { data: products } = await api.get('/api/products');
    
    // 4. Crear pedido
    const { data: order } = await api.post('/api/pedidos', {
      direccionEnvio: 'Calle Falsa 123',
      telefonoContacto: '+54 11 1234-5678',
      items: [{ productoId: 1, cantidad: 2 }]
    });
    
    console.log('Pedido creado:', order);
  } catch (error) {
    console.error('Error:', error.response?.data || error.message);
  }
}
```

### Ejemplo React Hook Personalizado

```javascript
// useApi.js
import { useState, useEffect } from 'react';
import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080',
  headers: { 'Content-Type': 'application/json' }
});

export const useAuth = () => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token'));

  useEffect(() => {
    if (token) {
      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
    }
  }, [token]);

  const login = async (email, password) => {
    const { data } = await api.post('/api/auth/login', { email, password });
    setToken(data.token);
    setUser(data.user);
    localStorage.setItem('token', data.token);
    return data;
  };

  const logout = () => {
    setToken(null);
    setUser(null);
    localStorage.removeItem('token');
    delete api.defaults.headers.common['Authorization'];
  };

  return { user, token, login, logout };
};

// Uso en componente
function App() {
  const { user, login, logout } = useAuth();

  const handleLogin = async () => {
    try {
      await login('john@example.com', 'password123');
      console.log('Login exitoso');
    } catch (error) {
      console.error('Error de login:', error);
    }
  };

  return (
    <div>
      {user ? (
        <>
          <p>Bienvenido {user.name}</p>
          <button onClick={logout}>Cerrar Sesión</button>
        </>
      ) : (
        <button onClick={handleLogin}>Iniciar Sesión</button>
      )}
    </div>
  );
}
```

---

## 🚀 Checklist de Integración Frontend

Antes de comenzar a desarrollar tu frontend, verifica:

- [ ] El backend está corriendo en `http://localhost:8080`
- [ ] Tu frontend corre en un origen permitido (5173, 5137, etc.)
- [ ] Puedes hacer una petición GET a `/api/products` sin token
- [ ] El login retorna un token JWT válido
- [ ] Puedes hacer peticiones autenticadas con el token en el header
- [ ] Los errores de CORS no aparecen en la consola del navegador
- [ ] Mantienes el token de forma segura (Context API o localStorage)
- [ ] Manejas la expiración del token (redirigir a login en 401)

---

## � Configuración del Backend

### Requisitos
- Java 17 o superior
- Maven 3.6+
- Spring Boot 3.x

### Ejecutar el Backend

```bash
# Opción 1: Con Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Opción 2: Con Maven instalado
mvn spring-boot:run

# Opción 3: Ejecutar el JAR compilado
mvn clean package
java -jar target/e_commerce-0.0.1-SNAPSHOT.jar
```

### Verificar que el Backend está Corriendo

```bash
# Test simple con curl
curl http://localhost:8080/api/products

# Test desde el navegador
# Abre: http://localhost:8080/api/products
```

### Variables de Entorno (application.properties)

```properties
# Puerto del servidor
server.port=8080

# Configuración de la base de datos (H2 en memoria por defecto)
spring.datasource.url=jdbc:h2:mem:ecommerce
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# JWT Configuration
jwt.secret=tu_clave_secreta_muy_segura_de_al_menos_256_bits
jwt.expiration=86400000

# CORS ya está configurado en SecurityConfig.java
```

### Consola H2 (Base de Datos)

Para acceder a la consola H2 y ver los datos:

**URL:** `http://localhost:8080/h2-console`

**Credenciales:**
- JDBC URL: `jdbc:h2:mem:ecommerce`
- User Name: `sa`
- Password: (dejar vacío)

---

## 🔐 Seguridad y Mejores Prácticas

### Para el Frontend

1. **Nunca expongas el token en la URL**
   ```javascript
   // ❌ Incorrecto
   fetch(`/api/products?token=${token}`);
   
   // ✅ Correcto
   fetch('/api/products', {
     headers: { 'Authorization': `Bearer ${token}` }
   });
   ```

2. **Valida las respuestas**
   ```javascript
   const response = await fetch('http://localhost:8080/api/products');
   if (!response.ok) {
     throw new Error(`HTTP error! status: ${response.status}`);
   }
   const data = await response.json();
   ```

3. **Maneja el token expirado**
   ```javascript
   if (error.response?.status === 401) {
     // Token expirado o inválido
     localStorage.removeItem('token');
     navigate('/login');
   }
   ```

4. **No guardes datos sensibles en localStorage sin encriptar**
   - El token JWT es seguro de guardar
   - No guardes contraseñas
   - Considera usar sessionStorage para mayor seguridad (se borra al cerrar el navegador)

### Para Producción

⚠️ **Importantes cambios antes de producción:**

1. **Actualizar orígenes CORS** en `SecurityConfig.java`:
   ```java
   configuration.setAllowedOrigins(Arrays.asList(
       "https://tu-dominio-produccion.com"
   ));
   ```

2. **Cambiar JWT Secret** en `application.properties`:
   ```properties
   jwt.secret=${JWT_SECRET:tu_clave_muy_segura_y_larga}
   jwt.expiration=3600000  # 1 hora en producción
   ```

3. **Usar base de datos persistente** (PostgreSQL, MySQL):
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/ecommerce
   spring.datasource.username=${DB_USER}
   spring.datasource.password=${DB_PASSWORD}
   ```

4. **Habilitar HTTPS** en producción
5. **Configurar rate limiting** para prevenir abuso
6. **Implementar logging y monitoreo**

---

## 📚 Recursos Adicionales

### Documentación Relacionada
- [Spring Security](https://spring.io/projects/spring-security)
- [JWT.io](https://jwt.io/) - Para decodificar y verificar tokens
- [CORS MDN](https://developer.mozilla.org/es/docs/Web/HTTP/CORS)

### Herramientas Recomendadas
- **Postman** - Para probar endpoints
- **Thunder Client** (VS Code) - Cliente REST ligero
- **React DevTools** - Para debugging de React
- **Redux DevTools** - Si usas Redux para el estado

### Testing de la API

```javascript
// Ejemplo de test con Jest
describe('API Tests', () => {
  test('Login debería retornar token', async () => {
    const response = await fetch('http://localhost:8080/api/auth/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        email: 'test@example.com',
        password: 'password123'
      })
    });
    
    const data = await response.json();
    expect(response.status).toBe(200);
    expect(data.token).toBeDefined();
  });
});
```

---

## �📞 Soporte

Para dudas o problemas con la API, contactar al equipo de backend.

### Issues Comunes y Soluciones Rápidas

| Problema | Causa | Solución |
|----------|-------|----------|
| Error CORS | Origen no permitido | Verificar que el frontend corra en puerto permitido |
| 401 Unauthorized | Token inválido o expirado | Hacer login nuevamente |
| 404 Not Found | Endpoint incorrecto | Verificar la URL del endpoint |
| 400 Bad Request | Validación fallida | Revisar el formato del body |
| Network Error | Backend no está corriendo | Iniciar el backend en puerto 8080 |

**Última actualización:** 28 de octubre de 2025
