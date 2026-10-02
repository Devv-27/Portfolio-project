# Portfolio CMS - Backend (Spring Boot)
Owner: Debabrata Kamila

## Run on Mac
    brew install openjdk@17 maven
    export JAVA_HOME="$(brew --prefix openjdk@17)/libexec/openjdk.jdk/Contents/Home"
    mvn spring-boot:run
Server: http://localhost:8080  |  Default admin: admin / admin123 (set ADMIN_PASSWORD to change)

## Try it
    # public
    curl localhost:8080/api/about
    curl localhost:8080/api/skills
    # login -> token
    curl -X POST localhost:8080/api/auth/login -H 'Content-Type: application/json' -d '{"username":"admin","password":"admin123"}'
    # create a project (admin)
    curl -X POST localhost:8080/api/projects -H "Authorization: Bearer TOKEN" -H 'Content-Type: application/json' -d '{"title":"Portfolio CMS","description":"My custom CMS","technologies":"Spring Boot, React"}'
    # upload image (admin)
    curl -X POST localhost:8080/api/upload/image -H "Authorization: Bearer TOKEN" -F file=@photo.jpg
    # contact form (public)
    curl -X POST localhost:8080/api/contact -H 'Content-Type: application/json' -d '{"name":"Test","email":"t@t.com","message":"Hello"}'

## Endpoints
- POST /api/auth/login, /api/auth/refresh
- GET, PUT /api/about
- GET, POST, PUT, DELETE: /api/skills, /projects, /blogs, /experience, /testimonials, /services
- GET /api/messages (admin only), POST /api/contact (public), POST /api/upload/image (admin)
- GET is public, everything else needs `Authorization: Bearer <token>`

## PostgreSQL instead of H2
    export DB_URL=jdbc:postgresql://localhost:5432/portfolio DB_USER=postgres DB_PASSWORD=yourpass
Create the database first: `createdb portfolio`. Tables are created automatically.

## Code map (src/main/java/com/debabrata/portfolio)
Models.java = tables | Repos.java = DB access | Api.java = all endpoints
SecurityConfig.java + Jwt.java = login/JWT | DataSeeder.java = first-run data
