# Finance Tracker Backend

A Java backend that handles user authentication, transaction management, categorization, and analytics.

## Contents

- [Features](#features) 
- [Tech Stack](#tech-stack)  
- [Getting Started](#getting-started)

## Features

- **User authentication**: register, login, logout
- **Security**: JWT authentication with cookie support
- **Profile management**: update email, password, name, goal amount
- **Analytics endpoints**: monthly income vs expenses, category breakdowns
- **Transaction management**: add, update, delete, list transactions for authenticated users

## Tech Stack

- **Framework**: Spring Boot
- **Database**: MySQL

## Getting Started

### Prerequisites:
- **Java 17+**
- **MySQL**

Clone the repo:
```bash
git clone https://github.com/vesc0/finance-tracker-backend.git
cd finance-tracker-backend
```

Build the project:
```bash
mvn clean install
```

Start the server:
```bash
mvn spring-boot:run
```
