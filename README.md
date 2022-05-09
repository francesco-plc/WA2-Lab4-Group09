# WA2-Lab4-Group09

## Instructions for launching the application
1. Configure the docker container
2. Open the project in folder WA2-Lab3-Group09 
3. Run this project, it will run in port 8080
4. in another window, open the project in folder WA2-Lab4-Group09
5. Run this project, it will run in port 8080
## Instructions to use the application
#### Registration of a user 
In order to create a user with surname 'USER1' execute this command:
```
curl -X POST -d '{"username":"USER1","email":"user1@user.com","password":"Passw0rd!"}' -v -i 'http://localhost:8080/user/register' -H "Content-Type: application/json"
```

In order to activate the user, go in the ACTIVATION table, copy/paste the provisionalId and activationCode in the following command:
```
curl -X POST -d '{"provisionalId":"{provisionalId}","activationCode":"{activationCode}"}' -v -i 'http://localhost:8080/user/validate' -H "Content-Type: application/json"
```

In order to login the user, execute the following command:
```
curl -X POST -d '{"username":"USER1","password":"Passw0rd!"}' -v -i 'http://localhost:8080/user/login' -H "Content-Type: application/json"
```

When the login is done, a JWT valid for the next hour is obtained in order to be able to later contact the TravelerService, specifying the JWT as a value of the Authorisation http header, like the following one:
```BearereyJhbGciOiJIUzM4NCJ9.eyJzdWIiOiJVU0VSMSIsImlhdCI6MTY1MTkzNjg2OSwiZXhwIjoxNjUxOTQwNDY5LCJyb2xlIjoiQ1VTVE9NRVIifQ.KTZKau_A0FxDUNxj9Sk9UKtiE6Ir1N8b7t_js3I0GFloVOghVpAx2uUS-bX6-V6u```