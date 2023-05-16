# Anti-fraud System
TODO

## Technical Requirements
- Validating transactions as per the following workflow:
  <img alt="transaction validation workflow" height="500" src="/screenshots/transaction-validation-workflows.png?raw=true" width="500"/>
- Authentication:
  - HTTP basic authentication
  - Storing users credentials in a relational database
- Authorization:
  - Roles: `ADMINISTRATOR`, `MERCHANT`, `SUPPORT`. only one role can be assigned to a user
  - Only one `ADMINISTRATOR` could exist
  - The first created user should be given `ADMINISTRATOR` role. Otherwise, `MERCHANT`
  - All users, except for `ADMINISTRATOR`, should be locked when created.
  - Role model:
```
|                                 | Anonymous | MERCHANT | ADMINISTRATOR | SUPPORT |
|---------------------------------|-----------|----------|---------------|---------|
| POST /api/auth/user             | -         | +        | +             | +       |
| DELETE /api/auth/user           | -         | -        | +             | -       |
| GET /api/auth/list              | -         | -        | +             | +       |
| POST /api/antifraud/transaction | -         | +        | -             | -       |
| PUT /api/auth/access            | -         | -        | +             | -       |
| PUT /api/auth/role              | -         | -        | +             | -       |
```
- REST APIs for:
  - Transactions:
    - Transaction validation 
  - Users:
    - creating a user
    - deleting a user
    - list users
    - change user's role
    - to lock/unlock user (`ADMINISTRATOR` cant be locked)

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