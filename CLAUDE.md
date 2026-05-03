# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

Spring Boot 2.7.18 on Java 17, server-rendered with Thymeleaf, persisted in PostgreSQL via Spring Data JPA, secured with Spring Security. Lombok is used heavily — assume `@Getter`/`@Setter`/`@RequiredArgsConstructor` everywhere; constructors and accessors won't appear in source.

The codebase contains Russian-language comments and UI strings; the domain is a Belarus/Russia-region classifieds marketplace (cities like Минск/Орша, +375 phone codes seeded in `data-product.sql`).

## Common commands (Windows / PowerShell)

```powershell
.\mvnw.cmd spring-boot:run                                  # run app on :8080
.\mvnw.cmd test                                             # run all tests
.\mvnw.cmd test "-Dtest=MarketPlaceApplicationTests"        # run a single test class
.\mvnw.cmd package                                          # build jar in target/
```

PostgreSQL must be reachable at `jdbc:postgresql://localhost:5432/postgres` with user `postgres` / password `postgres` (see `application.properties`). The currently committed config has the password hardcoded — flag if rotating.

## Architecture

Classic layered Spring MVC. All business code lives in a single package: `org.marketplace.marketplace.project`, split into `controller` / `service` / `repository` / `model` / `config`. The bootstrap class `MarketPlaceApplication` is one package up — `@SpringBootApplication` there scans down into `project.*` automatically.

Two domains:
- **Product** (`products` table) — the marketplace listing entity. `region`, `category`, `condition` are stored as plain `String` columns, not foreign keys. `ProductRepository` exposes both Spring Data derived queries (`findByRegion`, etc.) and JPQL `@Query` methods (`searchProducts`, `filterProducts`, `searchByTitle`) used for the filter form on the index page.
- **Category** (`categories` table) — managed via `/admin/categories` CRUD. **Important mismatch:** Categories are a separate entity, but `Product.category` is still a free-text string. Creating/deleting a Category does not affect existing products. If you're touching either side, decide whether the change should bridge them (e.g., make Product.category a FK) or stay siloped.

### Schema management — gotcha

`spring.jpa.hibernate.ddl-auto=update` is on, so JPA entity changes auto-apply to the running DB. The SQL files under `src/main/resources/sql/schema/data/` (`schema.sql`, `data-product.sql`) are **not** on Spring Boot's default init path (`classpath:schema.sql` / `classpath:data.sql`) and are not currently loaded automatically. They serve as reference / manual seed scripts. If you need them auto-loaded, either move them to the classpath root or add `spring.sql.init.schema-locations` / `data-locations`.

### Security — current state

`SecurityConfig` permits `/admin/**`, `/edit/**`, `/create-category`, `/update/**`, `/delete-category/**`, `/`, and `/login` for everyone, requires auth for the rest, and **disables CSRF globally**. So in practice, the admin category pages and most product mutations are publicly reachable. There's also a mismatch: the permit list mentions `/create-category` and `/delete-category/**`, but `CategoryController` actually lives at `/admin/categories/create` and `/admin/categories/delete/{id}` — those paths are only accessible because `/admin/**` is wide open. When tightening security, fix both the matchers and the permit list together.

A single in-memory user `user` / `123/*---` is defined twice — once in `SecurityConfig.userDetailsService()` and once via `spring.security.user.*` in `application.properties`. The `UserDetailsService` bean wins; the properties entry is dead config.

### View layer

Thymeleaf templates live in `src/main/resources/templates/`. The product list page (`index.html`) is reused for filtered, name-searched, and unfiltered views — `ProductController` returns `"index"` for `/`, `/filter`, and `/search-by-name`, populating `products`, `regions`, `categories`, plus selected-filter attributes for sticky form state. Static assets (`/css`, `/js`, `/img`) are under `src/main/resources/static/`.
