# WA2-Lab4-Group09

## Instructions for launching the application
1. Configure the docker container
2. Open Database section on Intellij and create a connection with the postgres container.
3. Right-click on the database and add a new one (New -> Database -> Ok)
4. Open the project, in another window, in folder WA2-Lab3-Group09 
5. Modify the application properties with the options that you have just set
6. Run this project, it will run in port 8080
7. Open the project in folder WA2-Lab4-Group09
8. Run this project, it will run in port 8081
## Instructions to use the application
#### Registration of a user 
In order to create a user with username 'USER1' execute those commands:
```
curl -X POST -d '{"username":"USER1","email":"user1@user.com","password":"Passw0rd!"}' -v -i 'http://localhost:8080/user/register' -H "Content-Type: application/json"
```

In order to activate the user, go in the ACTIVATION table, copy/paste the provisionalId and activationCode in the following command:
```
curl -X POST -d '{"provisionalId":"{provisionalId}","activationCode":"{activationCode}"}' -v -i 'http://localhost:8080/user/validate' -H "Content-Type: application/json"
```
In order to create an ADMIN with username 'ADMIN' executes those commands:
```
curl -X POST -d '{"username":"ADMIN","email":"admin@user.com","password":"Passw0rd!"}' -v -i 'http://localhost:8080/user/register' -H "Content-Type: application/json"
```

In order to activate the user, go in the ACTIVATION table, copy/paste the provisionalId and activationCode in the following command:
```
curl -X POST -d '{"provisionalId":"{provisionalId}","activationCode":"{activationCode}"}' -v -i 'http://localhost:8080/user/validate' -H "Content-Type: application/json"
```
Now set in the DB the Role of this user equal to 1 in order to obtain the ADMIN privilege

In order to log in the user, execute the following command:
```
curl -X POST -d '{"username":"USER1","password":"Passw0rd!"}' -v -i 'http://localhost:8080/user/login' -H "Content-Type: application/json"
```

When the login is done, a JWT valid for the next hour is obtained in order to be able to later contact the TravelerService, specifying the JWT as a value of the Authorisation http header, like the following one:
```
BearereyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJVU0VSMSIsImlhdCI6MTY1MTkzNjg2OSwiZXhwIjoxNjUxOTQwNDY5LCJyb2xlIjoiQ1VTVE9NRVIifQ.KTZKau_A0FxDUNxj9Sk9UKtiE6Ir1N8b7t_js3I0GFloVOghVpAx2uUS-bX6-V6u
```

Now use this token to perform actions in 8081 microservice:

- GET "/my/profile":
```
curl --request GET -H "Authorization: {Bearer...}" -v -i  http://localhost:8081/my/profile
```
- PUT "/my/profile"
```
 curl --request PUT -H "Authorization: {Bearer...}" -H "Accept: application/json" -H "Content-Type:application/json" -d '{"name":"Mario","surname":"Rossi","address":"via dei test Torino","date_of_birth":"01/01/1990","telephone_number":"1231231231"}' -v -i  http://localhost:8081/my/profile
```
- GET "/my/tickets"
```
curl --request GET -H "Authorization: {Bearer...}" -v -i  http://localhost:8081/my/tickets
```
- POST "/my/tickets"
```
curl --request POST -H "Authorization: {Bearer...}" -H "Accept: application/json" -H "Content-Type:application/json" -d '{"cmd":"buy_tickets","quantity":"3","zones":"ABC"}' -v -i  http://localhost:8081/my/tickets
```

---

In order to log in with the admin, execute the following commands:
```
curl -X POST -d '{"username":"ADMIN","password":"Passw0rd!"}' -v -i 'http://localhost:8080/user/login' -H "Content-Type: application/json"
```

When the login is done, a JWT valid for the next hour is obtained in order to be able to later contact the TravelerService, specifying the JWT as a value of the Authorisation http header, like the following one:
```
BearereyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJVU0VSMSIsImlhdCI6MTY1MTkzNjg2OSwiZXhwIjoxNjUxOTQwNDY5LCJyb2xlIjoiQ1VTVE9NRVIifQ.KTZKau_A0FxDUNxj9Sk9UKtiE6Ir1N8b7t_js3I0GFloVOghVpAx2uUS-bX6-V6u
```
Now use this token to perform actions in 8081 microservice:

 - GET /admin/travelers 
```
curl GET -v -i 'http://localhost:8081/admin/travelers' -H "Authorization:{Bearer...}"
```
  The endpoint returns a JSON list of usernames for which there exists any information (either in terms of user profile or issued tickets). 

- GET /admin/traveler/{userID}/profile 
```
curl GET -v -i 'http://localhost:8081/admin/traveler/USER1/profile' -H "Authorization:{Bearer...}"
```
The endpoint will return the profile corresponding to userID. 

- GET /admin/traveler/{userID}/tickets ??? returns the tickets owned by userID.
```
curl GET -v -i 'http://localhost:8081/admin/traveler/USER1/tickets' -H "Authorization:{Bearer...}"
```
The endpoint will return the tickets owned by userID