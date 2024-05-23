# PantryPal

<p align="center">
<img src="pantrypal/src/main/resources/static/images/transparent-logo.png" alt="PantryPal Logo" width="300"/>
</p>
PantryPal is a web application that helps you find recipes based on the ingredients you have. Simply upload an image of your groceries, and the app will analyze the ingredients and provide a list of possible recipes. Click on a recipe to see detailed information, including preparation instructions and ingredient lists.

## Table of Contents

- [Live Demo](#live-demo)
- [Features](#features)
- [Technologies](#technologies)
- [Setup](#setup)

## Live Demo
Check out the live application [here](https://pantrypal.runmydocker-app.com/)

## Features

- **Image Upload and Analysis**: Upload an image of your groceries to analyze the ingredients.
- **Recipe Search**: Find recipes based on the ingredients you have.
- **Recipe Details**: View detailed information about each recipe, including preparation instructions and ingredient lists.
- **Responsive Design**: The application is optimized for both desktop and mobile devices.

## Technologies

- **Frontend**: HTML, CSS (Bootstrap), JavaScript
- **Backend**: Java Spring Boot, FastAPI
- **Database**: PostgreSQL
- **External APIs**: Integration with third-party APIs for image analysis and recipe search

## Setup

### Prerequisites

- Java 11 or higher
- Maven
- Python 3.8 or higher
- Node.js and npm
- Docker and Docker Compose

### Clone the Repository

```bash
git clone https://github.com/yourusername/pantrypal.git
```

### Environment Variables
```
API_KEY=your_openai_api_key
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/postgres
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
IDENTIFIER_API_TARGET_URL=http://localhost:8000/analyze_image/?image_url=
```

### Running the Application
<strong>1. Start PostgreSQL Database </strong>

Ensure PostgreSQL is running and accessible with the credentials provided in the .env file.

<strong>2. Start FastAPI Server</strong>

```
cd indentifier
pip install -r requirements.txt
uvicorn main:app --reload
```

<strong>3. Start Spring Boot Application</strong>
```
cd pantrypal
mvn clean install
mvn spring-boot:run
```

### Access the Application
Open your browser and navigate to `http://localhost:8080`.
