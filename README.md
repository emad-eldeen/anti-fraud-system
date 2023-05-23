# Anti-fraud System
A system to validate credit cards' transactions. It supports authentication and authorization. Data is saved persistently in a database

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
|                                                  | Anonymous | MERCHANT | ADMINISTRATOR | SUPPORT |
|------------------------------------------------  |-----------|----------|---------------|---------|
| POST /api/auth/user                              | -         | +        | +             | +       |
| DELETE /api/auth/user                            | -         | -        | +             | -       |
| GET /api/auth/list                               | -         | -        | +             | +       |
| PUT /api/auth/access                             | -         | -        | +             | -       |
| PUT /api/auth/role                               | -         | -        | +             | -       |
| POST /api/anti-fraud/transaction                 | -         | +        | -             | -       |
| POST, DELETE, GET api/anti-fraud/suspicious-ips  | -         | -        | -             | +       |
| POST, DELETE, GET api/anti-fraud/stolen-cards    | -         | -        | -             | +       |
```
- REST APIs for:
  - Anti fraud:
    - Transaction validation
    - add/delete a stolen card
    - list stolen cards
    - add/delete suspicious IP
    - list suspicious IPs
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