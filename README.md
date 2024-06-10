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
Check out the live application [here](https://pantrypal.runmydocker-app.com/home)

## Features

- **Image Upload and Analysis**: Upload an image of your groceries to analyze the ingredients.
- **Recipe Search**: Find recipes based on your ingredients.
- **Recipe Details**: View detailed information about each recipe, including preparation instructions and ingredient lists.
- **User History**: Save your favorite recipes for later.
- **Responsive Design**: The application is optimized for desktop and mobile devices.

## Technologies

- **Frontend**: HTML, CSS (Bootstrap), JavaScript
- **Backend**: Java Spring Boot, FastAPI
- **Database**: PostgreSQL
- **External APIs**:
    - **Image analysis**: OpenAI Vision
    - **Recipe search and information**: Rapid API
- **Storage**: AWS S3

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
Fill in missing variables:

`/Identifier/requirements.txt`
```
API_KEY=your_openai_api_key
```

`/pantrypal/src/main/resources/application.properties`
```
spring.application.name=pantrypal
identifier.api.target.url=http://localhost:8000/analyze_image/
recipes.api.target.url=https://spoonacular-recipe-food-nutrition-v1.p.rapidapi.com/recipes/
recipes.api.key=
recipes.api.host=spoonacular-recipe-food-nutrition-v1.p.rapidapi.com
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=org.postgresql.Driver

#JPA properties
spring.jpa.show-sql = true
spring.jpa.hibernate.ddl-auto = update
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect

amazon.aws.accesskey=
amazon.aws.secretkey=
bucket.url=

spring.thymeleaf.prefix=classpath:/static/
spring.thymeleaf.suffix=.html
spring.thymeleaf.mode=HTML5

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
