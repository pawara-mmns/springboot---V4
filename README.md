# Spring Boot 4.0 - HTTP Interface Demo

## 📋 Project Overview

This project demonstrates the **HTTP Interface** feature introduced in **Spring Framework 6.0** and refined in **Spring Boot 4.0**. It showcases how to create declarative HTTP clients using simple Java interfaces, eliminating the need for boilerplate code when consuming REST APIs.

## 🎯 What This Project Does

The application consumes the [JSONPlaceholder](https://jsonplaceholder.typicode.com) REST API to fetch user data. It exposes local REST endpoints that proxy requests to the external API, demonstrating the modern, declarative approach to HTTP client development.

---

## 🆕 HTTP Interface: Spring Boot 4.0 vs Previous Versions

### **Spring Boot 4.0 Approach (Current Project)**

Spring Boot 4.0 introduces **declarative HTTP clients** using the `@HttpExchange` annotation family. This approach allows you to define HTTP clients as simple interfaces without any implementation code.

#### Key Features:
- ✅ **Zero boilerplate code** - No manual HTTP request construction
- ✅ **Type-safe** - Compile-time checking of requests
- ✅ **Clean and readable** - Interface-based design
- ✅ **Reactive support** - Built on WebClient (WebFlux)
- ✅ **Easy maintenance** - Changes only in interface declarations

#### Example from this project:
```java
@HttpExchange("/users")
public interface UserClient {
    @GetExchange
    List<User> getUsers();

    @GetExchange("/{id}")
    User getUserById(@PathVariable("id") int id);
}
```

**That's it!** No implementation needed. Spring generates the implementation at runtime.

---

### **Before Spring Boot 4.0 (Legacy Approaches)**

#### **1. RestTemplate (Spring Boot 2.x - Deprecated)**

```java
@Service
public class UserService {
    private final RestTemplate restTemplate;
    
    public UserService(RestTemplateBuilder builder) {
        this.restTemplate = builder
            .rootUri("https://jsonplaceholder.typicode.com")
            .build();
    }
    
    public List<User> getAllUsers() {
        return Arrays.asList(
            restTemplate.getForObject("/users", User[].class)
        );
    }
    
    public User getUserById(int id) {
        return restTemplate.getForObject("/users/" + id, User.class);
    }
}
```

**Drawbacks:**
- ❌ Synchronous/blocking operations
- ❌ Deprecated in Spring Boot 3.x
- ❌ Manual URL construction and error handling
- ❌ Tightly coupled to service implementation

---

#### **2. WebClient (Spring Boot 2.x/3.x)**

```java
@Service
public class UserService {
    private final WebClient webClient;
    
    public UserService(WebClient.Builder builder) {
        this.webClient = builder
            .baseUrl("https://jsonplaceholder.typicode.com")
            .build();
    }
    
    public Mono<List<User>> getAllUsers() {
        return webClient.get()
            .uri("/users")
            .retrieve()
            .bodyToFlux(User.class)
            .collectList();
    }
    
    public Mono<User> getUserById(int id) {
        return webClient.get()
            .uri("/users/{id}", id)
            .retrieve()
            .bodyToMono(User.class);
    }
}
```

**Drawbacks:**
- ❌ Verbose and repetitive code
- ❌ Requires understanding of reactive programming
- ❌ Manual request building for each method
- ❌ More code to maintain and test

---

#### **3. Feign Client (Spring Cloud)**

```java
@FeignClient(name = "user-client", url = "https://jsonplaceholder.typicode.com")
public interface UserClient {
    @GetMapping("/users")
    List<User> getUsers();
    
    @GetMapping("/users/{id}")
    User getUserById(@PathVariable("id") int id);
}
```

**Drawbacks:**
- ❌ Requires Spring Cloud dependency (heavyweight)
- ❌ External library maintenance
- ❌ Limited reactive support
- ❌ Additional configuration complexity

---

### **Comparison Table**

| Feature | RestTemplate | WebClient | Feign | **HTTP Interface (Boot 4.0)** |
|---------|-------------|-----------|-------|-------------------------------|
| **Boilerplate Code** | High | Medium | Low | **Minimal** |
| **Reactive Support** | ❌ No | ✅ Yes | ⚠️ Limited | ✅ **Yes** |
| **Type Safety** | ⚠️ Manual | ⚠️ Manual | ✅ Yes | ✅ **Yes** |
| **External Dependencies** | None | None | Spring Cloud | **None** |
| **Spring Native** | ✅ Yes | ✅ Yes | ❌ No | ✅ **Yes** |
| **Declarative** | ❌ No | ❌ No | ✅ Yes | ✅ **Yes** |
| **Status** | Deprecated | Active | Active | **Latest/Recommended** |

---

## 🏗️ Project Architecture

```
edu.icet
├── HttpInterfaceDemoApplication.java    # Main application entry point
├── client/
│   └── UserClient.java                  # HTTP Interface declaration
├── config/
│   └── HttpClientConfig.java            # HTTP client bean configuration
├── controller/
│   └── UserController.java              # REST API endpoints
├── model/
│   └── User.java                        # User data model
└── service/
    └── UserService.java                 # Business logic layer
```

---

## 📦 Code Explanation

### **1. Main Application Class**
**File:** `HttpInterfaceDemoApplication.java`

```java
@SpringBootApplication
public class HttpInterfaceDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(HttpInterfaceDemoApplication.class);
    }
}
```

- Standard Spring Boot application entry point
- `@SpringBootApplication` enables auto-configuration and component scanning

---

### **2. HTTP Interface (The Core Feature)**
**File:** `UserClient.java`

```java
@HttpExchange("/users")
public interface UserClient {
    @GetExchange
    List<User> getUsers();

    @GetExchange("/{id}")
    User getUserById(@PathVariable("id") int id);
}
```

**Explanation:**
- `@HttpExchange("/users")`: Base path for all methods in this interface
- `@GetExchange`: Maps to HTTP GET request
- `@PathVariable`: Binds URI template variables
- **No implementation needed** - Spring generates proxy at runtime

**Available Annotations:**
- `@HttpExchange` - Base annotation for any HTTP method
- `@GetExchange` - HTTP GET
- `@PostExchange` - HTTP POST
- `@PutExchange` - HTTP PUT
- `@DeleteExchange` - HTTP DELETE
- `@PatchExchange` - HTTP PATCH

---

### **3. HTTP Client Configuration**
**File:** `HttpClientConfig.java`

```java
@Configuration
public class HttpClientConfig {
    @Bean
    public UserClient userClient() {
        WebClient client = WebClient.builder()
                .baseUrl("https://jsonplaceholder.typicode.com")
                .build();

        HttpServiceProxyFactory factory = HttpServiceProxyFactory.builder()
                .exchangeAdapter(WebClientAdapter.create(client))
                .build();

        return factory.createClient(UserClient.class);
    }
}
```

**Explanation:**
- `@Configuration`: Marks this as a configuration class
- `@Bean`: Registers the UserClient as a Spring bean
- **WebClient**: Underlying HTTP client (reactive, non-blocking)
- **HttpServiceProxyFactory**: Creates dynamic proxies for HTTP interfaces
- **WebClientAdapter**: Bridges WebClient with the HTTP Interface API
- **factory.createClient()**: Generates implementation of UserClient interface

This is where the magic happens! The factory creates a proxy that intercepts method calls and translates them into HTTP requests.

---

### **4. Service Layer**
**File:** `UserService.java`

```java
@Service
public class UserService {
    private final UserClient userClient;

    public UserService(UserClient userClient) {
        this.userClient = userClient;
    }

    public List<User> getAllUsers() {
        return userClient.getUsers();
    }
    
    public User getUserById(int id) {
        return userClient.getUserById(id);
    }
}
```

**Explanation:**
- `@Service`: Marks this as a service component
- **Constructor injection**: Injects the UserClient bean
- Delegates HTTP calls to the client interface
- Can add business logic, caching, error handling here

---

### **5. REST Controller**
**File:** `UserController.java`

```java
@RestController
@RequestMapping("/api/test")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/users")
    public List<User> getUsers() {
        return userService.getAllUsers();
    }
    
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable("id") int id) {
        return userService.getUserById(id);
    }
}
```

**Explanation:**
- `@RestController`: Combines `@Controller` and `@ResponseBody`
- `@RequestMapping("/api/test")`: Base path for all endpoints
- Exposes two endpoints:
  - `GET /api/test/users` - Get all users
  - `GET /api/test/users/{id}` - Get user by ID

---

### **6. Data Model**
**File:** `User.java`

```java
@Data
public class User {
    private int id;
    private String name;
    private String userName;
    private String email;
}
```

**Explanation:**
- `@Data`: Lombok annotation that generates:
  - Getters for all fields
  - Setters for all non-final fields
  - `toString()` method
  - `equals()` and `hashCode()` methods
  - Constructor
- Represents the user data structure from the API

---

## 🔧 Dependencies Explained

### **1. Spring Boot Starter Web (v4.0.0-RC2)**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>4.0.0-RC2</version>
</dependency>
```

**Purpose:**
- Provides Spring MVC for building REST APIs
- Includes embedded Tomcat server
- JSON serialization/deserialization (Jackson)
- Required for `@RestController` and `@RequestMapping`

---

### **2. Spring Boot Starter WebFlux (v4.0.0-RC2)**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
    <version>4.0.0-RC2</version>
</dependency>
```

**Purpose:**
- Provides **WebClient** (reactive HTTP client)
- Required for HTTP Interface feature
- Enables reactive programming support
- Includes Reactor core library

**Why This Dependency is Essential:**
The HTTP Interface feature in Spring Boot 4.0 is built on top of **WebClient**, which is part of the WebFlux module. Even though this application uses traditional blocking endpoints, WebFlux is needed because:

1. **WebClient is the foundation**: `HttpServiceProxyFactory` uses `WebClientAdapter` internally
2. **Modern HTTP client**: WebClient is the recommended HTTP client (RestTemplate is deprecated)
3. **Reactive support**: Allows future migration to reactive endpoints
4. **Non-blocking I/O**: More efficient resource usage for HTTP calls

---

### **3. Lombok (v1.18.42)**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <version>1.18.42</version>
</dependency>
```

**Purpose:**
- Reduces boilerplate code with annotations
- `@Data` generates getters, setters, toString, equals, hashCode
- Keeps model classes clean and maintainable

---

### **4. Maven Compiler Plugin - Annotation Processing**
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-compiler-plugin</artifactId>
    <version>3.11.0</version>
    <configuration>
        <parameters>true</parameters>
    </configuration>
</plugin>
```

**Why `<parameters>true</parameters>` is Critical:**

This configuration enables **parameter name retention** at compile time, which is essential for the HTTP Interface feature.

**Technical Explanation:**

By default, Java bytecode does **not** preserve method parameter names. They become generic names like `arg0`, `arg1`, etc.

When this is enabled, Spring can:
1. ✅ Read actual parameter names from `@PathVariable("id")`
2. ✅ Match them to URI templates like `/{id}`
3. ✅ Properly bind path variables without explicit naming

**Without this setting:**
```java
// This would NOT work
User getUserById(@PathVariable int id);  // Parameter name lost!
```

**With this setting:**
```java
// This WORKS because parameter name is preserved
User getUserById(@PathVariable("id") int id);
```

**Alternative:** Without `<parameters>true</parameters>`, you would need to explicitly name every parameter:
```java
@GetExchange("/{id}")
User getUserById(@PathVariable("id") int id);  // "id" must be explicit
```

With the setting enabled, Spring can infer parameter names automatically, making the code cleaner.

---

## 🚀 Running the Application

### **Prerequisites**
- Java 17 or higher
- Maven 3.6+

### **Steps**

1. **Clone/Download the project**

2. **Build the project**
```bash
mvn clean install
```

3. **Run the application**
```bash
mvn spring-boot:run
```

4. **Test the endpoints**

Get all users:
```bash
curl http://localhost:8080/api/test/users
```

Get user by ID:
```bash
curl http://localhost:8080/api/test/users/1
```

---

## 📡 API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/test/users` | Fetch all users from JSONPlaceholder |
| GET | `/api/test/users/{id}` | Fetch a specific user by ID |

---

## 🎓 Key Takeaways

### **Why Use HTTP Interface in Spring Boot 4.0?**

1. **Cleaner Code**: No boilerplate HTTP request building
2. **Better Maintainability**: Changes only in interface declarations
3. **Type Safety**: Compile-time checking of HTTP requests
4. **Native Spring Feature**: No external dependencies like Feign
5. **Future-Proof**: Built on modern reactive stack (WebFlux)
6. **Testability**: Easy to mock interfaces in unit tests

### **When to Use It?**

✅ **Use HTTP Interface when:**
- Building microservices that communicate via REST
- Consuming third-party REST APIs
- You want clean, declarative client code
- You need type-safe HTTP clients

❌ **Consider alternatives when:**
- Working with legacy Spring Boot 2.x projects
- Need advanced features not yet supported
- GraphQL or gRPC communication (different protocols)

---

## 🔍 Additional Resources

- [Spring Framework HTTP Interface Documentation](https://docs.spring.io/spring-framework/reference/integration/rest-clients.html#rest-http-interface)
- [Spring Boot 4.0 Release Notes](https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-4.0-Release-Notes)
- [WebClient Documentation](https://docs.spring.io/spring-framework/reference/web/webflux-webclient.html)

---

## 📄 License

This is a demonstration project for educational purposes.

---

## 👨‍💻 Author

**Institute of Computer Engineering and Technology (ICET)**

---

**Happy Coding! 🎉**

