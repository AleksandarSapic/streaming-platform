# 🎬 Video Streaming Platform  

A **full-stack video streaming platform** that allows users to watch movies and TV shows, manage favorites, filter and query content, and securely interact with the application through JWT-based authentication and authorization.  

This project is built with a **Spring Boot backend** and an **Angular frontend**, and is designed to be scalable, extensible, and production-ready.  

---

## 🚀 Features  

- 📺 **Watch Movies & TV Shows** – Stream available content directly within the platform.  
- ⭐ **Favorites (My List)** – Add/remove movies and shows to your personalized list.  
- 🔎 **Filtering & Querying** – Search by title, genre, type (movie/show), etc.  
- 📑 **Pagination** – Efficiently browse large collections of content.  
- 🔐 **Authentication & Authorization (JWT)** – Secure login & role-based access.  
- 🎥 **Content Management** – Create, update, and manage movies/shows in the system.  

---

## 📂 Tech Stack  

### Backend (API)  
- **Spring Boot**  
- **Spring Security + JWT**  
- **Hibernate / JPA**  
- **H2 (Testing) / PostgreSQL (Production-ready)**  

### Frontend (UI)  
- **Angular**  
- **Bulma CSS**  
- **Bootstrap (for extra UI elements)**  

### DevOps  
- **Docker & Docker Compose**  
- **GitHub Actions CI/CD**  

---

## ⚙️ Installation & Setup  

### 1. Clone Repository  
```bash
git clone https://github.com/AleksandarSapic/streaming-platform
cd streaming-platform
```

### 2. Backend Setup (Spring Boot)
```bash
cd backend
./mvnw clean package
./mvnw spring-boot:run
```
Backend will start at: http://localhost:8080

### 3. Frontend Setup (Angular)
```bash
cd frontend
npm install
npm start
```
Frontend will run at: http://localhost:4200

## 🔑 Authentication
- **JWT Tokens** are issued upon login.
- **Roles:**
  - USER – can watch, search, and manage favorites.
  - ADMIN – can create, edit, and delete content.

## 📖 Use Cases
1. **User**
    - Register / login
    - Browse movies & TV shows
    - Filter by genre, title, or type
    - Add/remove favorites
    - Watch selected content
2. **Admin**
    - Manage content (create, update, delete)

## 🛠️ Future Improvements
- Recommendation system (based on watch history)
- Multi-language subtitle support
- Payment & subscription integration
- Real-time streaming statistics
