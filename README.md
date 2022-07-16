# Spring Boot "ZinkWorks ATM"
This is a sample Java / Maven / Spring Boot (version 2.7.1) application

# How to Run
This application is packaged as a jar. You run it using the ```java -jar``` command.
* Make sure you are using JDK 17
* You can build the project and run the tests by running ```mvn clean package```
* Once successfully built, you can run the service:
```
java -jar target/atm-0.0.1-SNAPSHOT.jar
```
## Endpoints can be tested using Swagger
````
http://localhost:8080/swagger
````
### GET BALANCE

````
Request:-

curl -X 'GET' \
  'http://localhost:8080/v1/accounts/123456789' \
  -H 'accept: */*' \
  -H 'pin: 1234'
  
Response:-

{
  "accountNumber": 123456789,
  "openingBalance": 800,
  "overDraft": 200
}
````
### Dispense Cash
````
Request:-

curl -X 'POST' \
  'http://localhost:8080/v1/accounts/123456789' \
  -H 'accept: */*' \
  -H 'pin: 1234' \
  -H 'atmId: 1' \
  -H 'amount: 85' \
  -d ''
 
Response:-

{
  "Account": {
    "accountNumber": 123456789,
    "openingBalance": 715,
    "overDraft": 200
  },
  "noOfFifty": 1,
  "noOfFive": 1,
  "noOfTen": 1,
  "noOfTwenty": 1
} 
````

## About the Service (ZinkWorks ATM)

We all know how ATM Machine work… don’t we? You are tasked with developing software for one that:

Should initialize with the following accounts:

| Account    | PIN    | Opening Balance   | Overdraft   | 
|------------|--------|-------------------|-------------|
| 123456789  | 1234   | 800               | 200         |
| 987654321  | 4321   | 1230              | 150         | 

- Should initialize with €1500 made up of 10 x €50s, 30 x €20s, 30 x €10s and 20 x €5s 
- Should not dispense funds if the pin is incorrect, 
- Cannot dispense more money than it holds, 
- Cannot dispense more funds than customer have access to 
- Should not expose the customer balance if the pin is incorrect, 
- Should only dispense the exact amounts requested, 
- Should dispense the minimum number of notes per withdrawal, 

The application should receive the data, process the operations and then output the results, it is responsible for validating customer account details and performing basic operations as described by API requirements:
- User (assume any rest client – curl, postman, browser) should be able to request a balance check along with maximum withdrawal amount (if any), 
- User should be able to request a withdrawal. If successful - details of the notes that would be dispensed along with remaining balance, 
- If anything goes wrong, user should receive meaningful message, and there should be no changes in user’s account, 

Assume application will be distributed as Docker image. Provide Dockerfile, but don’t waste too much time for building and testing docker image, focus on functionality.

Assume importance levels:
1. Code working as described in requirements,
2. Application is building with simple javac, mvn install or gradle build command (or any basic build command working on behalf of programming language you choose),
3. Unit tests are included. Coverage level depends on time you have left to complete the assignment, but we would like to see business logic (service layer) coverage at 60%,
4. Other things you would like to implement for this project (ex. Database, application test coverage over 90%, API for gathering different statistics, UI or whatever else you think would make your application extraordinary),

### Notes:

- Unit Test are included. Code Coverage is 90% (Jacoco Plugin is used to generate reports)
- InMemory database H2 is used for initialization of accounts and atms tables. (data.sql in resources)
- Proper Error Messages are given with HTTP Codes for all scenarios.
- DockerFile is present as per requirements
- User's Balance is considered his opening balance and overdraft combined. Money is deducted from opening balance and overdraft depending upon the amount requested.