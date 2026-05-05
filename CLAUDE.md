# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Stack

Spring Boot 2.7.18 on Java 17, server-rendered with Thymeleaf (+ `thymeleaf-extras-springsecurity5`), persisted in PostgreSQL via Spring Data JPA, secured with Spring Security 5.7. Lombok is used everywhere — assume `@Getter`/`@Setter`/`@RequiredArgsConstructor`; constructors and accessors won't appear in source.

The codebase is in Russian (comments, UI strings, validation messages); the domain is a Belarus/Russia-region classifieds marketplace (cities like Минск/Орша, +375 phone codes seeded in `data-product.sql`).

## Common commands (Windows / PowerShell)

```powershell
.\mvnw.cmd spring-boot:run                                  # run app on :8080
.\mvnw.cmd test                                             # run all tests
.\mvnw.cmd test "-Dtest=MarketPlaceApplicationTests"        # run a single test class
.\mvnw.cmd package                                          # build jar in target/
```

PostgreSQL must be reachable at `jdbc:postgresql://localhost:5432/postgres` with user `postgres` / password `postgres` (see `application.properties` — password is hardcoded). The seeded admin login is `admin@admin.com` / `admin` (created on first boot by `AdminUserSeeder`).

## Architecture

Classic layered Spring MVC. All business code lives in a single package: `org.marketplace.marketplace.project`, split into `controller` / `service` / `repository` / `model` / `config`. The bootstrap class `MarketPlaceApplication` is one package up — `@SpringBootApplication` there scans down into `project.*` automatically.

### Domains

- **Product** (`products`) — the listing. `region` and `category` are stored as plain `String` columns, not foreign keys. `condition` and `status` are `@ManyToOne` to `ProductCondition` and `ProductStatus`. `user` is `@ManyToOne` to `User` (the seller).
- **User** (`users`) — DB-backed accounts with `role` (`USER` / `ADMIN`) as a plain string column. Authentication uses `email` as the username (see `usernameParameter("email")` in `SecurityConfig` and `CustomUserDetailsService`).
- **Category** (`categories`) — managed via `/admin/categories` CRUD. **Important mismatch:** `Product.category` is still a free-text string. Creating/deleting a Category does not affect existing products. If you're touching either side, decide whether to bridge them (e.g., make `Product.category` a FK) or stay siloed.
- **ProductStatus** — `PENDING` / `APPROVED` / `REJECTED` constants live as `public static final String` on the entity (see `ProductStatus.java`). Rows are seeded by `ProductStatusSeeder` on boot.
- **ProductCondition** — referenced from forms via `StringToProductConditionConverter` so a plain `condition` form field resolves to the entity.

### Moderation flow

New listings start in `PENDING`. Admins use `/admin/moderation` (`ModerationController`) to approve → `APPROVED` or reject → `REJECTED`. The public index page should only show `APPROVED` listings — verify which `ProductService` query the controller calls before changing visibility logic, since `ProductRepository` exposes both filtered and unfiltered finders.

### Security — current state

`SecurityConfig` requires authentication for everything except:
- static assets (`/css`, `/js`, `/images`, `/img`)
- `/login`, `/register`
- public ad browsing (GET only): `/`, `/filter`, `/search-by-name`, `/product/{numericId}` (matched via `regexMatchers`), `/api/product/*/phone`
- `/admin/**` requires `ROLE_ADMIN`

CSRF is **disabled globally**. Form-login posts to `/login` with `email`/`password`, success → `/`. Access-denied page is `/`. The Thymeleaf header in `index.html` / `product-page.html` uses `sec:authorize="isAuthenticated()"` / `isAnonymous()` to swap profile/logout for a "Войти" link; the "Подать объявление" / "Мои объявления" buttons stay visible for everyone — anonymous clicks rely on Spring Security's auto-redirect to `/login`.

The `regexMatchers` for `/product/\d+` is deliberate: it permits the public detail page but keeps `/product/create` and `/product/edit/{id}` behind auth. If you add new public ad URLs, mirror that GET-only, regex-based pattern.

There's dead config in `application.properties`: `spring.security.user.*` (`user` / `123/*---`) is overridden by the `CustomUserDetailsService` bean and never used.

### View layer

Thymeleaf templates in `src/main/resources/templates/`. `index.html` is reused for `/`, `/filter`, and `/search-by-name` — `ProductController` returns `"index"` for all three and populates `products`, `regions`, `categories`, plus selected-filter attributes for sticky form state. The product detail page is `product-page.html`; admin pages live under `templates/admin/`.

Static assets are under `src/main/resources/static/`. **`WebConfig` remaps `/img/**` to two locations**: first `file:src/main/resources/static/img/` (so dev-mode photo uploads written there are served live), then the classpath. Don't expect classpath-only resolution.

### Schema management — gotcha

`spring.jpa.hibernate.ddl-auto=update` is on, so JPA entity changes auto-apply to the running DB. The SQL files under `src/main/resources/sql/schema/data/` (`schema.sql`, `data-product.sql`, `users.sql`) are **not** on Spring Boot's default init path (`classpath:schema.sql` / `classpath:data.sql`) and are not loaded automatically. They are reference / manual seed scripts. Runtime seeding is done in Java by `AdminUserSeeder` and `ProductStatusSeeder` (both `CommandLineRunner`s). If you need the SQL files auto-loaded, move them to the classpath root or set `spring.sql.init.schema-locations` / `data-locations`.
