# Portfolio CMS - Backend (Spring Boot)
Owner: Debabrata Kamila

## Run 
Server: http://localhost:8080  |  Default admin: admin / admin123 (set ADMIN_PASSWORD to change)



## Endpoints
- POST /api/auth/login, /api/auth/refresh
- GET, PUT /api/about
- GET, POST, PUT, DELETE: /api/skills, /projects, /blogs, /experience, /testimonials, /services




## Code map (src/main/java/com/debabrata/portfolio)
Models.java = tables | Repos.java = DB access | Api.java = all endpoints
SecurityConfig.java + Jwt.java = login/JWT | DataSeeder.java = first-run data
