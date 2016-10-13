
## Deploy locally

Requirements:
* Linux machine
* JDK 8 (for example on Ubuntu 16.04 install with `sudo apt-get install openjdk-8-jdk`)

Deployment:
```
$ git clone https://github.com/pardiralli-dev/pardiralli.git
$ cd pardiralli
$ ./deploy-local.sh
```
This will connect to a dummy database in Heroku. To use a local database, setup the database locally and modify the database configuration/credentials in `deploy-local.sh` accordingly.
