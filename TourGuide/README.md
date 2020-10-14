# TourGuide - Main application
Welcome to TourGuide !

- TourGuide is part of the TripMaster company applications portfolio
- TourGuide is a web application which goal is help users to travel
- The application is working but with some issues

The goal of this project is to : 
- enhance the performance of the application
- modify existing functionalities to meet users requests
- add a new functionality requested by product owners
- split and distribute the application 

For more information about the application, its functioning, the goals of this enhancement project and the results obtained, you can refer to the technical documentation attached in the repository.


### TourGuide Distributed version

This version is a splitted version of the monolithic TourGuide application available in the following repository (most up to date branch = *develop*):
<https://github.com/ob78/TourGuide/tree/develop/TourGuide>

>Please note that Java has been upgraded from version 8 to version 11 (and Gradle from version 4.5.1 to 6.1.1) in order to use the most up to date version of the Java HttpClient used on the client side for communication between Microservices.

Being a distributed application, this version is composed of 4 parts (*4 Microservices*) :
- TourGuide Application : the main application using the below services 
- GPS Service : in charge of services related to localisation
- Preferences Service : in charge of services related to trip deals proposed to users and depending on their travel preferences 
- Rewards Service : in charge of services related to rewards computation

Technologies used for each Microservice are the following :
- Java is used as programming language
- SpringBoot is used for the web application which is based on the MVC pattern
- Server used is SpringBoot Tomcat embedded
- Gradle is used to run the tests, build and package the application
- JUnit is used as tests engine
- Mockito is used as mocking framework for tests

Microservices communicate using REST APIs (Server side : Tomcat / Client side : Java HttpClient).

### TourGuide Application

This repository contains the main TourGuide application.

You will find the other services in the following repositories :
- GPS Service : <https://github.com/ob78/TourGuide_Distributed_GpsApplication/tree/develop/TourGuide>
- Preferences Service : <https://github.com/ob78/TourGuide_Distributed_PreferencesApplication/tree/develop/TourGuide>
- Rewards Service : <https://github.com/ob78/TourGuide_Distributed_RewardsApplication/tree/develop/TourGuide>

### Getting Started

The following instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

You need to install the following software :

- Java 11
- Gradle 6.6.1
- Docker
>You don't need to install SpringBoot by yourself because dependencies will be added by Gradle (Version of SpringBoot used is 2.1.6)

### Installing

You will find below a step by step explanation that tell you how to get a development environment running :

1.Install Java :
<https://www.oracle.com/java/technologies/javase-jdk11-downloads.html>

2.Install Gradle :
<https://gradle.org/install/>

3.Install Docker :
<https://docs.docker.com/get-docker/>

### Application running

Then you can import and run the application from your favorite IDE.

>Please note that the application has been developed with the IntelliJ IDE.

### Configuration

In each Microservice there is a Spring configuration properties file called : *application.properties*.

In this file, you can manage :
- in all Microservices : the logging level 
- in the TourGuide Application : the sever listening port, as well as the name and listening port of the 3 other Services
- in the GPS, Rewards and Preferences Services : the sever listening port

### Application initialization

When the TourGuide Application is launched, by default it is initialized with a certain number of users stored in memory (in a HashMap).
You can modify the number of initial users using the *internalUserNumber* in the *InternalTestHelper* class.

>You can disable this initialization by setting the *testMode* boolean in the *TourGuideService* class at false. 


### Endpoints

The following EndPoints are exposed by the TourGuide main application :

- GET  <http://localhost:8080/getLocation> : provide the location (being composed of latitude and longitude) of a user
>Request Parameter : *userName* = name of the user 

- GET  <http://localhost:8080/getNearbyAttractions> : provide the 5 closest attractions to the user location
>Request Parameter : *userName* = name of the user 

- GET  <http://localhost:8080/getRewards> : provide the rewards earned by the user
>Request Parameter : *userName* = name of the user 

- GET  <http://localhost:8080/getAllCurrentLocations> : provide the current locations for all users

- GET  <http://localhost:8080/getTripDeals> : provide the trip deals proposed by the travel agency network and depending on the rewards and preferences of the user
>Request Parameter : *userName* = name of the user 

- GET  <http://localhost:8080/getPreferences> : to get travel preferences of the user
>Request Parameter : *userName* = name of the user 

- POST <http://localhost:8080/rating/postPreferences> : to post travel preferences of the user
>Request Parameter : *userName* = name of the user 
> / Request Body : *userPreferences* = preferences of the user

### Docker container deployment

A Dockerfile is present in this repository in order to deploy the application in a Docker container.
>In order to build a Docker Image using this Dockerfile, please use the following command line (in the *Dockerfile* directory) :
`docker build -t tourguide .`

You can deploy each of the GPS, Rewards and Preferences Services in a Docker container as well (please see instructions in their dedicated repositories).

When you have 4 Docker Images for the 4 parts of the application, you can run all these 4 parts of the whole application using the *docker-compose.yml* file present in this repository.
>To do this, please use the following commend line (in the *docker-compose.yml* directory) :
`docker-compose up -d`
 
This will launch the 4 Microservices and create a dedicated Docker bridge network to enable their communication. 
 
### Tests

Tests are included. You can run them using JUnit runner or using Gradle.

>For the performances tests, you can modify the number of users.
