# 🏋️ Fitness Microservices

A cloud-native fitness management system built using **Spring Boot Microservices**. This project demonstrates how to design and implement a scalable backend using modern microservice architecture with service discovery, centralized configuration, and API Gateway.

## 🚀 Features

- Microservices Architecture
- API Gateway using Spring Cloud Gateway
- Service Discovery with Netflix Eureka
- Centralized Configuration using Spring Cloud Config
- RESTful APIs
- Independent service deployment
- Maven-based multi-module project
- Scalable and loosely coupled architecture

---

## 🏗️ Architecture

```
                        +-----------------------+
                        |       Client          |
                        +----------+------------+
                                   |
                                   |
                        +----------v------------+
                        |     API Gateway       |
                        |      Port: 8080       |
                        +----------+------------+
                                   |
          -------------------------------------------------
          |                     |                        |
          |                     |                        |
+---------v---------+  +--------v--------+   +----------v---------+
|   User Service    |  | Activity Service|   |    AI Service      |
|                   |  |                 |   |                    |
+-------------------+  +-----------------+   +--------------------+

                 All services register with

                  +-----------------------+
                  |    Eureka Server      |
                  |      Port: 8761       |
                  +-----------------------+

                 Configuration served by

                  +-----------------------+
                  |   Config Server       |
                  +-----------------------+
```

---

## 📂 Project Structure

```
fitness-microservices
│
├── gateway/               # API Gateway
├── eureka/                # Eureka Discovery Server
├── configserver/          # Spring Cloud Config Server
├── userservice/           # User Management Service
├── activityservice/       # Activity Tracking Service
├── aiservice/             # AI Service
└── pom.xml                # Parent Maven Project
```

---

## 🛠️ Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Cloud Gateway
- Spring Cloud Config
- Netflix Eureka
- Spring Web
- Maven

### Tools

- IntelliJ IDEA
- Git & GitHub
- Postman

---

## 📌 Services

| Service | Default Port |
|----------|-------------:|
| Gateway | 8080 |
| Eureka Server | 8761 |
| Config Server | 8888 *(Update if different)* |
| User Service | Configured via application.yml |
| Activity Service | Configured via application.yml |
| AI Service | Configured via application.yml |

---

## ⚙️ Getting Started

### Clone Repository

```bash
git clone https://github.com/sanketbose4/fitness-microservices.git

cd fitness-microservices
```

### Start Services

Start the services in the following order:

1. Config Server
2. Eureka Server
3. User Service
4. Activity Service
5. AI Service
6. API Gateway

---

## 🌐 Eureka Dashboard

```
http://localhost:8761
```

---

## 🔀 API Gateway

```
http://localhost:8080
```

All requests should be routed through the Gateway.

---

## 📡 API Endpoints

### User Service

```
GET /users
POST /users
PUT /users/{id}
DELETE /users/{id}
```

### Activity Service

```
GET /activities
POST /activities
```

### AI Service

```
GET /ai
POST /ai
```

> Update the endpoints above according to your implementation.

---

## 📈 Future Improvements

- Docker Support
- Docker Compose
- Kubernetes Deployment
- JWT Authentication
- Spring Security
- OpenAPI / Swagger Documentation
- Distributed Tracing
- Centralized Logging
- CI/CD using GitHub Actions
- Monitoring using Prometheus & Grafana

---

## 🤝 Contributing

Contributions are welcome.

1. Fork the repository
2. Create a feature branch

```
git checkout -b feature/feature-name
```

3. Commit changes

```
git commit -m "Add feature"
```

4. Push

```
git push origin feature/feature-name
```

5. Open a Pull Request

---

## 👨‍💻 Author

**Sanket Bose**

- GitHub: https://github.com/sanketbose4
- LinkedIn: https://www.linkedin.com/in/sanket-bose/

---

## ⭐ Support

If you found this project useful, consider giving it a ⭐ on GitHub.
