### PARDIRALLI CHARITY PLATFORM

Developed by Tuule Sõber, Kaspar Papli, Priit Paluoja, Mari Liis Velner
For Eesti Vähihaigete Lastega Vanemate Liit


## Local Deployment

Can be deployed locally using the deploy-local.sh script.
Requirements for doing so:
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
The link to our web application is http://urgas.ee/pardiralli/.

## Functions

# Search page

This page allows the administrator to search for ducks.
* The administrator can search for ducks, given the duck ID and the period of time when the duck was bought.
* The administrator can search for ducks, given the owner's first and last name and phone number and the buyer's email and the period of time when the ducks was bought.
* The page displays the following data about the search results: duck ID, buyer's email, owner's first and last names and phone number and the specific race when the duck was bought.
_____________________

# Statistics page

This page displays statistics about the duck purchases.
* There is a graph that displays two types of data: the amount of sold ducks and the amount of money gained by said purchases. Both of these are displayed in relation to time.
* The administrator can choose the period of time about which the data is displayed on the graph.
* The administrator can export the two aforementioned types of data as a CSV file, again choosing the time period.
_____________________

# Races page

This page allows the administrator to manage races. A race is one 'Pardiralli' event that takes place once a year. Races cannot take place during
    overlapping periods of time.
* The administrator can create a new race, given its name, description, beginning and end dates.
* The page displays all of the races - its name and beginning and end dates. Upon clicking on the name of the race, further info
    about the race is displayed - a graph displaying the amount of money donated and ducks bought during that race and its description.
* The administrator can close races. They also close automatically when the finish date is past.
_____________________

# Insertion page

This page allows the administrator to insert ducks manually.
* The administrator can insert a duck, given the owner's first nam, last name and phone number, the buyer's email, the number of ducks bought and the price per duck.
_____________________

# Main page

Whereas the previously described pages are only accessible for logged in administrators, this page can be accessed by everyone.
On the main page, people can purchase ducks.
* The user must insert their email, and the name and phone number of the duck owner.
   The user must agree to the terms of the purchase and choose the amount of ducks they want to purchase and the amount of money each duck costs.
* After inserting the information and it being verified, the user is shown the data they just inserted and are allowed to choose
   a bank link through which they wish to pay.
* The user can then pay. The info about their purchase is sent to their email.
_____________________

The administrator can also log in in order to do their thing.



University of Tartu, 2017