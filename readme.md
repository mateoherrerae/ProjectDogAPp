# Dog Caregivers Application Project

Hi, I'm Mateo, a Software Engineering Student in third year, and welcome to the repository for my Dog Caregivers Application Project. I created this project as a comprehensive solution to bring together dog owners, walkers, and other pet care professionals—all powered by a robust microservices architecture.

---

## Overview

This application simplifies the management and care of dogs, ensuring that every user—from walkers to pet owners—has a seamless, efficient, and secure experience. The project is broken down into various microservices, each focused on a specific aspect of the overall functionality.

---

## Microservices Architecture

Below is an overview of each microservice, its purpose, key endpoints, and the primary data models involved.


| **Microservice**                  | **Purpose**                                                | **Key Endpoints**                                                                               | **Principal Models**          |
|-----------------------------------|------------------------------------------------------------|-------------------------------------------------------------------------------------------------|-------------------------------|
| **Completed/User Service**        | User management (registration, authentication, roles)      | `POST /api/users/register`<br>`POST /api/users/login`<br>`GET /api/users/{userId}`              | `User`, `Role`                |
| **Completed/Dog Service**         | Registration and management of dogs linked to their owners | `POST /api/dogs`<br>`GET /api/dogs/owner/{ownerId}`<br>`PUT /api/dogs/{dogId}`                  | `Dog`, `MedicalRecord`        |
| **Completed/Walker Service**      | Walker profiles, reviews, and availability                 | `POST /api/walkers`<br>`GET api/walkers/nearby?lat={LAT}&lng={LNG}`<br>`POST /api/walkers/rate` | `WalkerProfile`, `Review`     |
| **In Progress/Booking Service**   | Bookings for walks and feeding services                    | `POST /api/bookings`<br>`GET /api/bookings/user/{userId}`<br>`PUT /api/bookings`                | `Booking`, `ServiceRequest`   |
| **Pending/Chat Service**         | Real-time chat between users and walkers                   | WebSocket: `/ws/chat`<br>`GET /api/chat/history`                                                | `ChatMessage`, `Conversation` |
| **Pending/Notification Service** | Notifications (email, SMS, push)                           | `POST /api/notifications`<br>`GET /api/notifications/user/{userId}`                             | `Notification`, `Template`    |
| **Pending/Payment Service**      | Payment processing                                         | `POST /api/payments/process`<br>`GET /api/payments/history`                                     | `Payment`, `Invoice`          |
| **Pending/Lost Pet Service**      | Alerts for lost pets and geolocation                       | `POST /api/alerts`<br>`GET /api/alerts?location=...`                                            | `LostPetAlert`, `Sighting`    |

---

## Project Ideas and Features

Inspired by various innovative concepts, I have outlined several ideas and features to further enhance the project. These ideas not only cover the core functionality but also offer pathways for scaling the application over time.

### 1. Core Features (MVP)

#### Dog Walkers 
- **Registration & Verification**: Walkers register with identity verification through photos, documents, and references.
- **Rating & Review System**: Users can filter walkers based on scores, experience, and more.
- **Real-Time Geolocation**: Dog owners can track the walker's route in real time (integrating APIs like Google Maps).
- **Integrated Chat**: Using WebSocket (e.g., STOMP with Spring Boot) to ensure seamless communication.
- **Secure Payments**: Incorporates payment gateways like Stripe, PayPal, or MercadoPago for secure transactions.

#### Feeding During Vacations
- **Service Request**: Owners can specify dates, times, and dietary requirements.
- **Caregiver Verification**: Caregivers are properly vetted based on experience and certifications.
- **Notifications & Reports**: Caregivers can send photos or videos to confirm that feeding tasks were completed.

#### Nearby Veterinarians
- **Directory**: A basic listing of nearby veterinarians (address, phone, hours) powered by APIs such as Google Places.
- **User Reviews**: A system to gather user comments and ratings.
- **Map Integration**: Displaying the location of veterinary clinics directly on Google Maps.

---

### 2. Additional Ideas for Scaling

#### Social Network for Pets
- **Pet Profiles**: Each pet can have a profile with photos, medical history, and milestones (e.g., "First walk on the beach").
- **Thematic Groups**: Forums for like-minded pet owners (e.g., "Golden Retriever Owners in Madrid").

#### Lost Pet Alerts
- **Push Notifications**: Alerts for users within the vicinity when a pet is reported lost.
- **Social Media Integration**: Sharing alerts on social media to quickly spread the word.

#### Online Pet Store
- **E-Commerce**: Selling pet food, toys, and accessories.
- **Subscription Model**: Offering periodic deliveries for staple pet supplies.

#### Pet Daycare
- **Booking Reservations**: For certified daycare centers or in-home pet care services.
- **Live Camera Feeds**: Letting owners check in on their pets via live video streaming (using WebRTC).

#### Professional Trainers
- **Session Scheduling**: Arrange training sessions at home or in outdoor parks.
- **Online Courses**: Providing video tutorials and downloadable guides for pet training.

#### Pet Health Insurance
- **Insurance Comparison**: A feature that compares various insurance plans.
- **Partner Integrations**: Directly linking with associated veterinary services.

---

## 3. Recommended Technologies

### Backend
- **Spring Boot**: To develop microservices in conjunction with Spring Cloud.
- **Databases**: PostgreSQL for relational data and MongoDB for chat logs or system logs.
- **Authentication**: Utilizing JWT in combination with OAuth2.
- **Messaging Queues**: RabbitMQ or Kafka to handle real-time notifications.

### Frontend
- **Mobile Application**: Developed with Flutter or React Native.
- **Web Application**: Leveraging frameworks like React.js or Angular.

### External APIs
- **Google Maps/Places**: For geolocation functionalities.
- **Twilio or Firebase**: For enabling push notifications and real-time chat.
- **Payment Gateways**: Integrating Stripe, PayPal, or MercadoPago for secure payments.

---

## 4. Monetization Strategies

- **Service Commission**: Charging a percentage for each booking or transaction (from walks, feeding services, etc.).
- **Premium Subscriptions**: Offering exclusive features such as live camera feeds or discounts with partner veterinarians.
- **Advertising**: Running ads for pet stores or accessories.
- **Anonymous Data Sales**: Providing statistical insights into pet care (while adhering to GDPR and other privacy laws).

---

## 5. Technical Challenges and Solutions

- **Real-Time Geolocation**: Implemented using WebSocket (with Spring Boot + STOMP) or Firebase Realtime Database.
- **Real-Time Chat**: Leveraging libraries such as Socket.IO or SignalR for efficient communication.
- **Scalability**: Using Kubernetes or Docker Swarm to orchestrate the microservices efficiently.
- **Security**: Ensuring data validation (e.g., background checks for walkers) and encrypting all sensitive information such as user locations.

---

## 6. Tips for Getting Started

- **Prioritize the MVP**: Focus first on essential functionalities—dog walking and feeding—before expanding to additional features.
- **User Testing**: Engage pet owners early on to gather invaluable feedback to refine the platform.
- **Low-Code Tools**: Use low-code tools (e.g., FlutterFlow for the frontend) to speed up development if you're less experienced.
- **API Documentation**: Document the RESTful API thoroughly using tools like Swagger/OpenAPI to ensure smooth future integrations.

---



Thank you for taking the time to explore my project. I welcome any feedback or suggestions you might have!
