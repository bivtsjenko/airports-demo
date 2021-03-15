# Introduction 
As an assignment for Schiphol the following tasks where executed. 

- An overview of the top 10 airports used as source airport. Output file can be found at /src/main/resources/input/top10airports.csv
- Load a source file as a streamed dataframe. Show within each window of 100 minutes top 10 airports used as source airport.
- Deploy jobs as a azure web app for monitoring.

# Getting Started
clone me pls

# Build and Test
`mvn clean install -U -X`

# Deployment as azure app service

- login on azure container registry

  `az acr login -n schipholdemo`

- containerize application

   `mvn compile jib:build`
    
- at every container build update webapp

  `az webapp update -g schiphol-demo-rg -n schipholdemo`
 

# Webapp 

Spark
`https://schiphol-demo.azurewebsites.net/`
  
 
 # Resulting top10 airports    
 
 
 <div class="foo">
 
 Airport | Routes
 ------ | -----
 ATL     | 915 
 ORD     | 558 
 PEK     | 535  
 LHR     | 527  
 CDG     | 524  
 FRA     | 497  
 LAX     | 492  </div>
 DFW     | 469  
 JFK     | 456  
 AMS     | 453 
 
 #Final remarks
 It needs more testing. My main focus was to get it running end-to-end. 
 The application reads sourcefile, spark processes it and shows jobs as a azure webapp.
 
 