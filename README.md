<<<<<<< HEAD
# StaySaathi (Java Full-Stack)

It includes rich search/filter/sort, verified badges, listing detail pages, inquiry booking, reviews, shortlist compare, favorites, and an admin inquiry dashboard.

## Tech Stack

- Java 17
- Spring Boot 3 (Web + Thymeleaf + JPA + Validation)
- H2 (default local DB)
- PostgreSQL (production profile)
- Vanilla JS + custom CSS frontend (responsive)
- Docker + Docker Compose

## GitHub Pages + API Setup

GitHub Pages hosts only static files. Your API must run on a separate Java host (Render/Railway/EC2).

1. Deploy backend API from this repo (Spring Boot).
2. Set backend URL in [config.js](./config.js):

```js
window.STAYSAATHI_CONFIG = {
  API_BASE: "https://your-backend-url.onrender.com",
  STATIC_ROUTING: true
};
```

3. Set backend CORS origins with environment variable:

```text
APP_CORS_ALLOWED_ORIGINS=https://nirkumera14.github.io,http://localhost:8080
```

## Core Features

- Lucknow-focused PG inventory with seeded realistic sample data
- India-wide city search catalog with state-aware autocomplete suggestions
- Advanced filters:
  - Gender
  - Room type
  - Rent range
  - Food included
  - Verified only
  - Amenities
- Sorting:
  - Relevance
  - Price low to high
  - Price high to low
  - Rating
- Listing cards with:
  - Verified / partner verified / brand new tags
  - Ratings + review count
  - Bed availability
  - Amenity highlights
- Detail page:
  - Gallery
  - Room option table
  - Amenities
  - Nearby transit/landmark
  - Contact + map deep link
  - Optional Google Maps photos + Google Maps reviews via Places API
- Inquiry booking form (persisted)
- Review submission + rating refresh
- Favorites (local storage)
- Compare up to 3 listings
- India listings fallback:
  - If a city has no matching listing, API can auto-fallback to nearest listing city within 40 km
- Admin dashboard:
  - Metrics
  - Inquiry list
  - Inquiry status update (NEW / CONTACTED / CLOSED)

## Run Locally

1. Build and run:

```bash
mvn spring-boot:run
```

2. Open:

- App: http://localhost:8080
- Admin: http://localhost:8080/admin
- H2 Console: http://localhost:8080/h2-console

Default admin key:

```text
pg-admin-2026
```

Header used by admin APIs: `X-Admin-Key`

## Production Profile (PostgreSQL)

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

Environment variables used in `prod`:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `APP_ADMIN_KEY`
- `GOOGLE_MAPS_ENABLED` (`true` / `false`)
- `GOOGLE_MAPS_API_KEY`
- `PORT`

## Google Maps Photos & Reviews

This app supports official Google Places API integration for listing detail pages.

- Endpoint usage:
  - Find Place (Legacy)
  - Place Details (Legacy) (reviews/photos/rating/url)
  - Place Photos (Legacy)
- Configure:
  - `GOOGLE_MAPS_ENABLED=true`
  - `GOOGLE_MAPS_API_KEY=<your_key>`
- Enable billing and Places APIs in Google Cloud for the key.
- Important: this uses API-based integration, not website scraping.

## Docker Deployment

Build and run with PostgreSQL:

```bash
docker compose up --build
```

App will be available on `http://localhost:8080`.

## API Snapshot

- `GET /api/listings`
- `GET /api/listings/india`
- `GET /api/meta/options`
- `GET /api/cities?query=<prefix>&limit=8`
- `GET /api/cities/india?query=<prefix>&limit=8`
- `GET /api/cities/summary?limit=8`
- `GET /api/listings/{slug}`
- `GET /api/listings/{slug}/reviews`
- `POST /api/listings/{slug}/reviews`
- `POST /api/inquiries`
- `GET /api/admin/metrics` (requires `X-Admin-Key`)
- `GET /api/admin/inquiries` (requires `X-Admin-Key`)
- `PATCH /api/admin/inquiries/{id}/status?status=NEW|CONTACTED|CLOSED` (requires `X-Admin-Key`)

## Notes

- The app seeds data only when no listings exist.
- India city catalog lives in `src/main/resources/india-statutory-towns.csv`.
- This is a full working baseline intended for quick deployment and further customization.
=======
# StaySaathi
>>>>>>> db75722fd21e5c24e6373d1ff2ab94474036d776
