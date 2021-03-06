# Introduction 
As an assessment for Schiphol the following tasks where executed. 

- An overview of the top 10 airports used as source airport. Output file can be found locally at `/src/main/resources/output/top10airports.csv`.
- Load source file as a streamed dataframe. Show within each window of 100 minutes top 10 airports used as source airport.
- Containerize the application with JIB and push to Azure Container Registry.
- Deploy jobs as a azure web app for monitoring.

# Getting Started
clone me pls

# Build and Test
`mvn clean install -U -X`

# Deployment As Azure App Service

- login on azure container registry

  `az acr login -n schipholdemo`

- containerize application

   `mvn compile jib:build`
    
- at every container build update webapp

  `az webapp update -g schiphol-demo-rg -n schipholdemo`
 

# Webapp 

Spark jobs can be found at:

[schiphol-demo.azurewebsites.net](http://schiphol-demo.azurewebsites.net/stages/)

- SparkUI can be slow. The basic SKU and specs were used. Thus, have a bit patience with it.
- Refresh couple of times in case 'stages' page is empty.

 
 # Resulting Top10 Airports    
 
 
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
 
 # Final Remarks
 
 It needs more testing. My main focus was to get it running end-to-end. 
 The application reads sourcefile, spark processes it and shows jobs as a azure webapp.
 
 