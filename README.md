# 👥 Employee Pair Finder
### Find employees who worked together on common projects for the longest period of time.

## 🌐 Live Demo
### Deployed Application: https://employees-pzl0.onrender.com

The application is hosted on Render.com (free tier) and stays awake 24/7 thanks to a cron job that pings the /health endpoint every 14 minutes.

## About The Project
Employee Pair Finder is a Spring Boot web application that analyzes employee-project assignment data to identify pairs of employees who have worked together on common projects for the longest period.

The application reads data from a CSV file, calculates overlapping work periods between employees on the same project, and displays the results in a clean, sortable table format.

## ✨ Features
- CSV File Upload - Drag and drop or select CSV files 
- Multiple Date Formats - Supports various date formats (see Date Formats)
- NULL Date Handling - Treats NULL as current date 
- Longest Working Pair - Highlights the pair that worked together the most 
- Project Details - Shows all common projects with days worked 
- Sortable Tables - Click column headers to sort results 
- Responsive Design - Works on desktop, tablet, and mobile 
- REST API - Health check and ping endpoints 
- Error Handling - Friendly error pages with suggestions 
- Docker Support - Containerized for easy deployment 
- 24/7 Availability - Keep-alive mechanism prevents sleeping

## 🛠 Technology Stack
Technology| Version |          Purpose          
|:--------:|:-------:|:-------------------------:|
|   Java   |   17    | Core programming language |
|Spring Boot|  3.2.0  |       Web framework       |
|Thymeleaf|  3.1.3  |      Template engine      |
|Gradle|   7.6   |        Build tool         |
|Bootstrap|  5.3.2  |     Frontend styling      |
|OpenCSV|   5.9   |        CSV parsing        |
|Docker| latest  |     Containerization      |
|Render.com|    -    | Cloud hosting |

## 🚀 Getting Started
### Prerequisites
Make sure you have the following installed:

- Java 17 or higher 
- Gradle 7.6 or higher (or use the Gradle wrapper)
- Git (for cloning)
- Docker (optional, for containerized run)

### Local Installation

#### 1. Clone the repository
```sh
git clone https://github.com/andy489/Andrey-Stoev-employees.git
cd Andrey-Stoev-employees
```

#### 2. Build the project
```sh
# Using Gradle wrapper
./gradlew clean build

# Or using local Gradle
gradle clean build
```

#### 3. Run the application
```sh
# Using Gradle
./gradlew bootRun

# Or run the JAR directly
java -jar build/libs/employee-pair-finder-*.jar
```

#### 4. Access the application
Open your browser and navigate to:

```sh
http://localhost:8080
```

## Sample Data

```csv
143,12,2013-11-01,2014-01-05
218,10,2012-05-16,NULL
143,10,2009-01-01,2011-04-27
143,10,2013-11-01,2014-01-05
218,12,2013-10-01,2014-02-01
144,12,2014-01-01,2014-03-01
144,10,2013-01-01,2013-06-01
218,12,2014-02-01,2014-04-01
145,15,2020-01-01,2020-12-31
146,15,2020-06-01,2021-06-01
145,16,2021-01-01,2021-12-31
147,16,2021-03-01,2021-09-01
```

## Project Structure

```
employees/
├── src/
│   ├── main/
│   │   ├── java/com/sirma/employees/
│   │   │   ├── EmployeesApplication.java
│   │   │   ├── controller/
│   │   │   │   ├── EmployeePairController.java
│   │   │   │   └── HealthController.java
│   │   │   ├── service/
│   │   │   │   └── EmployeePairService.java
│   │   │   ├── model/
│   │   │   │   ├── EmployeeAssignment.java
│   │   │   │   ├── EmployeePair.java
│   │   │   │   └── PairProject.java
│   │   │   ├── util/
│   │   │   │   └── DateUtils.java
│   │   │   └── exception/
│   │   │       └── GlobalExceptionHandler.java
│   │   └── resources/
│   │       ├── application.yaml
│   │       ├── static/
│   │       │   ├── styles/
│   │       │   │   ├── style.css
│   │       │   │   └── err.css
│   │       │   ├── scripts/
│   │       │   │   ├── main.css
│   │       │   │   └── err.js
│   │       │   └── images/
│   │       │       └── favicon.png
│   │       └── templates/
│   │           ├── index.html
│   │           ├── result.html
│   │           └── error.html
│   └── test/
│       └── java/com/example/employeepair/
├── Dockerfile
├── build.gradle
├── settings.gradle
├── gradlew
├── gradlew.bat
├── gradle/
│   └── wrapper/
│       ├── gradle-wrapper.jar
│       └── gradle-wrapper.properties
└── README.md
```