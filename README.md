# Lokalise Service

Backend service for Lokalise Pro Max, a replica of [Lokalise](https://lokalise.com) with image annotation feature. <br />
My project at GoTo / Gojek Hackathon 2024, won 2nd place out of 95 teams / 371 participants across Indonesia, India, Singapore, and China.

## Requirements

- Java 11
- Postgres 13
- AWS S3 bucket (this project uses AWS S3 presigned url that provides only temporary access to the image files. Use public S3 bucket for permanent image files access)

## Setting up project

1. Create database
   ```
   createdb -h localhost -U postgres -O my_username lokalise_db
   ```

2. Add required configs in application.yml
   ```
   DB_NAME: lokalise_db
   DB_HOST: localhost
   DB_PORT: 5432
   DB_USERNAME: my_username
   DB_PASSWORD: foo
   ```

3. Run migration
   ```
   ./gradlew flywayMigrateDb
   ```

4. Run project
   ```
   ./gradlew run
   ```

5. Short demo
<p align="center">
  <img src="https://github.com/mukhlishga/lokalise-backend/blob/main/Lokalise%20Pro%20Max.gif" alt="lokalise-pro-max-demo" />
</p>
