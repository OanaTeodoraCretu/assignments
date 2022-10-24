# Scheduler Application

Contents:

* [Summary](#summary)
* [Running the application locally using docker-compose](#running)
* [Architecture](#architecture)
    * [Database](#database)
    * [Persistence](#persistence)
    * [Service](#service)
    * [API](#API)
    
* [Issues and TODOs](#issues)

## Summary
Scheduler Application is a spring boot java app, that expose endpoints through an API, for defining schedules using 
date time ranges, series and exceptions, updating an existing schedule (also for a specific date) and to get the 
information about an offender, if he/she should be in a specific zone depending on the current schedule associated with 
the zone.

## Running the application locally using docker-compose
This was tested with Docker 20.10.20 and docker-compose version 1.29.2

```sh
docker-compose up --build
```

The command above will build two Docker images and start them:
- the first one is the `schedules` image, which will be exposed on [localhost:8080](http://localhost:8080/)
- the second one is a MySQL 5.6 image, which will be exposed to `schedules` as `db:3306`

Initially it may take a few seconds for the containers to start, as both images have to be built and, also, the database schema has to be created.

You may notice that the `schedules` container waits for the `db` container to be healthy, before starting.

Now you can query the API:

```sh
curl --location --request POST 'http://localhost:8080/api/schedule' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "winter",
    "start_date": "2023-10-01T21:06:02Z",
    "end_date": "2023-12-31T21:06:02Z",
    "active": true,
    "seriesList": [{
        "start_hour": "10:00",
        "end_hour": "11:00",
        "freq_type_id": 3,
        "freq_interval_id": 1000,
        "exception_id": null,
        "repeat_interval_value": 2,
        "exception": {
            "for_date": "2023-10-21T00:06:02Z",
            "new_start_time": "10:00",
            "new_end_time": "11:15",
            "active": true
        }
    }
    ]
}'
```

You can stop both containers by pressing Ctrl+C. The database is persisted as a docker volume:

```sh
docker volume ls
```

## Architecture
This web app was entirely built using java 17, spring boot, spring web, spring jdbc. Maven was used as a build automation
tool. Postman was used to send different requests to the web app in order to test the application. Tomcat is the default
servlet container used by spring boot to deploy the web app.

### Database
I used mysql as the relational database management system with MySQL Workbench as the client. 
The name of the database is "schedulesdb". It has 9 tables. Here is how I thought about the database schema:
    
    * schedules table has a schedule_id (unique id, PK, autoincrement), start_date, end_date, name, active (in case there will be some future deletes)
    Note: since there was a mention of the timezone (UTC) in the specifications, I decided to use TIMESTAMP instead of 
        DATETIME for all the dates.
   
    * frequencytypes table describes how frequently is the interval associated to the schedule.
        1 = One time only
        2 = daily - the event happens daily
        3 = weekly - the event happens weekly
        4 = monthly - the event happens monthly
        5 = yearly - the event happens yearly
   
    * frequencyintervals table holds information about which is the day when the event occurs. The mapping between the 
        DAY_OF_WEEK and the id is the following:
        Sunday - 1 (binary is also 1)
        Monday - 2 (binary is 10)
        Tuesday - 4 (binary is 100)
        Wednesday - 8 (binary is 1000)
        Thursday - 16 (binary is 10000)
        Friday - 32 (binary is 100000)
        Saturday - 64 (binary is 1000000)

    * exceptions table stores all the exceptions for a certain series:
        - exception_id (unique, PK, AI)
        - for_date (timestamp) - not null
        - new_start_time (time) - it can be empty, and in this case the series are excepted only for the specified date
        - new_end_time (time) - it can be empty, and in this case the series are excepted only for the specified date
        - active (boolean) - can active/inactive an exception

    * series table has the following structure:
        - series_id (int, unique id, PK, autoincrement)
        - start_hour (time) - the start time for the interval
        - end_hour (time) - the end time for the interval
        - schedule_id - reference to the schedule table (FK) - relation is one-to-many (one schedule can have many series)
        - freq_type_id (int) - reference to the frequencytypes table. Series frequency can be:
                one (time only), daily, weekly, monthly, yearly.
        - freq_interval_id (int) - reference to the frequencyintervals table, that provides ids for all days of week. 
                It can store multiple days if the binnary value of their ids are summed up. For example if the interval 
        happens weekly, every Monday and Tuesday then the freq_type_id = 3 and freq_interval_id = 110. In this way we 
        can store intervals that happen everyday (freq_interval_id = 1111111), only weekends, week days and so on. 
        - repeat_interval_value (int) - how often the interval should repeat on the specified days. for example:
        every 2 weeks, every 3 months. The number 2 and 3 can be stored in this field.
        - exception_id - reference to the exceptions table where all the exceptions are stored. 

    * zones table stores info about a zone (zone_id and name)
    * scheduleszones table holds mapping between a schedule and a zone (one zone can have multiple schedules and one schedule 
        can be defined for multiple zone)
    * offenders table stores info about the offender ( id, first_name, last_name)
    * offenderszones table stores relation between an offender and a zone. (one zone can have multiple offenders and 
        one offender can be assigned to multiple zones).

TODOs: add indexes for improving the performance, add unique constraints and foreign keys. 

## Persistence
On top of the database there is a persistence layer that interacts directly with the database. I used spring jdbc for this 
job because I thought it's a simple web app with just a few tables and a few relationships between them. I didn't see the
justification for using an ORM like Hibernate for this particular job. I do think it would have made my job much easier if
I had chosen it from the beginning and it would have saved me a lot of time. The code still needs a lot of work on 
validating inputs, implementing the constraints, add transactional queries for creation of the schedule as I did on update.


##Service
The DAOs (data access objects) described previously are used by a layer of services. Their role is to use the necessary 
DAOs to return what's being needed in the controller.

##API
The ScheduleController maps all the requests to the address http://${HOST_NAME:PORT}/api/schedule....
I used spring-web for the implementation.
Here are all the requested endpoints:
  * Create new schedule (just date range, using just series or using exceptions for series)
    POST  http://${HOST:PORT}}/api/schedule
    Example for Body (using raw JSON):
    {
    "name": "winter",
    "start_date": "2023-10-01T21:06:02Z",
    "end_date": "2023-12-31T21:06:02Z",
    "active": true,
    "seriesList": [{
      "start_hour": "10:00",
      "end_hour": "11:00",
      "freq_type_id": 3,
      "freq_interval_id": 1000,
      "exception_id": null,
      "repeat_interval_value": 2,
      "exception": {
        "for_date": "2023-10-21T00:06:02Z",
        "new_start_time": "10:00",
        "new_end_time": "11:15",
        "active": true
        }
      }]
    }
    
  * Update an existing schedule (assuming the UI sends a schedule id)
    
    PUT http://${HOST:PORT}/api/schedule/29
    The schedule_id is a path variable and the request body is a map that has 
    key = field_name being updated, value = new value for the field_name
    
    I chose this solution because the object Schedule can become pretty big and if we update just one
    field, there should be no need in sending the whole object from UI. 
    TODO: some DTOs (data transfer objects) would have been nice here.
    
    Example for the request body:
    
    {
    "name":"spring",
    "start_date": "2024-03-20T23:06:02Z",
    "end_date": "2024-06-24T00:06:02Z",
    "is_active": true,
    "series_list": [{
      "series_id":19,
      "start_hour": "10:00",
      "end_hour": "11:00",
      "freq_type_id": 2,
      "freq_interval_id": 10,
      "repeat_interval_value": 1,
      "exception": {
        "exception_id": 4,
        "for_date": "2024-03-20",
        "new_start_time": "10:00",
        "new_end_time": "10:15",
        "is_active": false
      }},
      {
      "series_id":20,
      "start_hour": "11:00",
      "end_hour": "12:00",
      "freq_type_id": 3,
      "freq_interval_id": 10,
      "repeat_interval_value": 1
      }]
    }

  * Update an existing schedule but just for a specific date.
  PUT http://${HOST}:8080/api/schedule?specificDate=2024-03-20T00:00:00Z
    
  Example for Request body:
    {
      "freq_type_id": 3, -> weekly
      "freq_interval_id": 10, -> Monday
      "repeat_interval_value": 1, -> each week
      "exception": {
        "for_date": "2023-03-20T00:06:02Z",
        "new_start_time": "10:00",
        "new_end_time": "12:00",
        "active": true
      }
    }

  * Checks if an offender must be in the zone for a given date (it looks also in the 
    exceptions that are defined for every series of every schedule that fits the requirement).
    
    GET http://${HOST}:8080/api/schedule/checkPresence?date=2024-03-21T00:00:00Z&zoneId=1&offenderId=1
    
    All the APIs return true is the operation was successful, false otherwise. 
    TODOs: All the API responses can improved in handling more information on what was processed or what error occurred.

## Issues and TODOs
  Time was not my friend lately and unfortunately I had to give up on a few very important areas. 
 - date formatting
 - missing data validation
 - missing logging
 - missing unit tests
 - documenting the code
 - adding more comments to the code


    