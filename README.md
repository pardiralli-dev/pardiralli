
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

**Note.** When deploying locally, the base url is localhost:8080/ and the current subpages are localhost:8080/search, localhost:8080/statistics, localhost:8080/settings and localhost:8080/insert.
The link to our web application is http://pardiralli.herokuapp.com/.

## Access to admin pages

To gain access to the admin pages (every page except the index page) use the username **part** and password **donald**.
