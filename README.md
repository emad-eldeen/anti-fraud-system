# Anti-fraud System
TODO

## Technical Requirements
- Validating transactions as per the following workflow:
  <img alt="transaction validation workflow" height="500" src="/screenshots/transaction-validation-workflows.png?raw=true" width="500"/>
- Authentication:
  - HTTP basic authentication
  - Storing users credentials in a relational database
- REST APIs for:
  - Transactions:
    - Transaction validation: authenticated 
  - Users:
    - creating a user
    - deleting a user
    - list users

## Knowledge Used
- Spring Boot:
  - Web
  - Security
  - JPA
  - Validation
- Lombok
- H2 Database
- Data Transfer Objects (DTO)
- Java Streams