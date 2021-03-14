# Introduction 
As an assignment for Schiphol the following tasks where excecuted. 

- An overview of the top 10 airports used as source airport. Result can be found at /src/main/resources/input/routes.dat
- Loads a .bat file into a streamed DataFrame. Shows within each window of 1 second top 10 airports used as source airport.

# Getting Started
clone me pls

# Build and Run
`mvn clean install -U -X`

# Deployment as azure app service

- login on azure container registry

  `az acr login -n schipholdemo`

- containerize application

   `mvn compile jib:build`
    
- deploy as a web app

  `az webapp update -g schiphol-demo-rg -n schipholdemo`
  