# 📝 Todo App — Backend

A RESTful backend API for the Todo App, built with **Java / Spring Boot**, containerized with **Docker**, and powered by **Supabase (PostgreSQL)** as the cloud database.

> 🖥️ **Frontend Repository:** [todo-app-frontend](https://github.com/NullUser3/To-do-frontend)

---

## 🧰 Tech Stack

| Layer       | Technology              |
|-------------|-------------------------|
| Language    | Java 21                 |
| Framework   | Spring Boot             |
| Database    | Supabase (PostgreSQL)   |
| ORM         | Spring Data JPA         |
| Container   | Docker & Docker Compose |

---


## ⚙️ Environment Variables

Rename `.env.properties.example` to `.env.properties` and fill in your values:

```bash
cp .env.properties.example .env.properties
```

| Variable                  | Description                              |
|---------------------------|------------------------------------------|
| `DB_URL=`                 | Your Supabase project URL                |
| `DB_USERNAME`             | Database username                        |
| `DB_PASSWORD`             | Database password                        |
| `JWT_SECRET`              | Secret key used for JWT signing          |

> ⚠️ Never commit your `.env.properties` file. It is already listed in `.gitignore`.

---

## 🐳 Running with Docker Compose

Make sure you have [Docker](https://www.docker.com/) installed, then:

```bash
# 1. Clone the repository
git clone https://github.com/NullUser3/To-do-backend.git
cd todo-app-backend

# 2. Set up environment variables
cp .env.properties.example .env.properties
# Edit .env.properties with your Supabase credentials

# 3. Build and start the containers
docker-compose up --build

# 4. To run in detached mode
docker-compose up --build -d

# 5. To stop the containers
docker-compose down
```

The API will be available at: `http://localhost:8080`

---

## 🐋 Dockerfile Overview

The `Dockerfile` uses a multi-stage build:

1. **Build stage** — compiles the Spring Boot app into a `.jar` using Maven/Gradle.
2. **Runtime stage** — runs the `.jar` on a lightweight JRE image.

---


## ☁️ Supabase Setup

1. Create a free project at [supabase.com](https://supabase.com).
2. Navigate to **Settings → Database** to get your connection string.
3. Navigate to **Settings → API** to get your `URL` and `anon key`.
4. Paste these values into your `.env.properties`.

---

## 🚀 Running Locally (Without Docker)

```bash
# Requires Java 21+ and Maven installed

mvn clean install
mvn spring-boot:run
```

Make sure your `.env.properties` variables are exported or configured in `application.properties` before running.

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/my-feature`
3. Commit your changes: `git commit -m 'Add my feature'`
4. Push to the branch: `git push origin feature/my-feature`
5. Open a Pull Request

---

## 📄 License

This project is licensed under the [MIT License](LICENSE).
