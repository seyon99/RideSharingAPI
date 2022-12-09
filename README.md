# RideSharingAPI

## About
A microservices REST API backend for a ride sharing app. The API has three microservices, which are named `Location`, `User`, and `TripInfo`. It uses an API gateway to seperate external public APIs from internal microservice APIs, which utilizes multithreading to allow for concurrent requests.

## Technologies Used

- Java
- MongoDB
- PostgreSQL
- Neo4j
- Docker

Each microservice utilizes a different database technology, with MongoDB being used for the Trip Information microservice, PostgreSQL for the User microservice, and Neo4j being used for the Location microservice.

## Building/running

    git clone https://github.com/seyon99/RideSharingAPI/
    cd RideSharingAPI
    docker-compose up --build -d

View the Javadoc comments to learn about specific endpoints and their parameters. After building, make use of Postman, cURL, or whichever method you prefer to make API requests.
