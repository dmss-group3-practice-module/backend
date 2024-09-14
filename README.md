# Backend (Practice Module)


This Documentation is still WIP.

## Getting Started
1. Install Gradle plugin for your IDE.

## Commands
`./gradlew clean` : remove the build files generated from the build command \
`./gradlew build` : build the project \
`./gradlew bootrun` : run the project 


## File Structure
### Controller
Contains the Rest API that would serve inputs from the frontend

### Service
Contains the logic to handle the interaction between different entities in the system.

### Data Access
Contains the DAOs, handles the details required to connect to databases.

### Entity
Contains the Entity Object, each class should either....
- represent a database table
- the logical grouping of multiple tables
- entity required for communicating between different modules of the system,

### Util
Contains Utils functions that support the main system.
