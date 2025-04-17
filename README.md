🛒 E-Commerce Challenge - Backend Application backend that implements a shopping cart for an e-commerce. Developed in Java using Spring Boot, with in-memory storage and automatic removal of inactive shopping carts.

📌 Table of contents Summary

About the project

Starting

Pre-requisites

Controllers and Endpoints

Functionality description

Persistency

Documentation

Author

📋 Summary This project is part of a technical test for the position of Software Engineer. It consists of the development of a backend system capable of managing shopping carts and products, with specific rules such as automatic cart expiration after 10 minutes of inactivity. The whole system must run without a persistent database, using in-memory storage.

📦 About the project The goal is to build a simple, efficient and easy to test REST service. Users can create carts, add products, get their information or delete them. In addition, a background task is implemented that automatically deletes carts that have been unused for more than 10 minutes.

🚀 Starting 🛠 Pre-rerequisites Make sure you have the following installed:

Java JDK 17+

Apache Maven

Spring Boot

IDE (IntelliJ, Eclipse, VSCode, etc.)

Postman (optional, for test)

Docker (optional)

▶️ Execution

Clone the repository
git clone

Access the project directory
cd ecommerce-cart

Run the project with Maven
./mvnw spring-boot:run You can also open the project in your favorite IDE and run the EcommerceCartApplication.java class.

🌐 Controllers and Endpoints CartController

Method Endpoint Description POST /api/carts Create a new cart GET /api/carts/{id} Gets the information of a cart by ID POST /api/carts/{id}/products Add a product to cart DELETE /api/carts/{id} Delete a cart by ID

📄 Description of functionalities ✅ Automatic creation of a UUID for each cart

✅ Product aggregation with validations (ID, description, price)

✅ Input validation using jakarta.validation

✅ Automatic deletion of carts after 10 minutes of non-use (background scheduler)

✅ Global exception handling with customized messages

✅ 100% in-memory storage, without database

🧠 Persistency The project does not use a database. All information is stored in memory using a ConcurrentHashMap. It is volatile, so it is lost when the application is restarted. This is intentional and aligned with the technical test requirements.

📚 Documentation Endpoints are documented and can be easily tested with Postman.

📌 You can also add Swagger later for automatic visual documentation. I can help you integrate it if you want.

👨‍💻 Author Esteban Software Engineer | Backend Developer Correo: estebanfigue29@gmail.com
