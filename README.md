
## Deploy locally

Requirements:
* Linux machine
* JDK 8 (on Ubuntu 16.04 install with `sudo apt install openjdk-8-jdk`)

Deployment:
```
$ git clone https://github.com/pardiralli-dev/pardiralli.git
$ cd pardiralli
$ ./deploy-local.sh
```
This will connect to a dummy database in Heroku. To use a local database, setup the database locally and modify the database configuration/credentials in `deploy-local.sh` accordingly.

**Note.** As of now, only the search page and partially the statistics page are implemented and therefore after deploying locally, pages http://localhost:8080/search and http://localhost:8080/statistics can be viewed without errors. 
The link to our web application is http://pardiralli.herokuapp.com/ but for the previously stated reasons, only pages http://pardiralli.herokuapp.com/search and http://pardiralli.herokuapp.com/statistics are currently viewable without errors.
