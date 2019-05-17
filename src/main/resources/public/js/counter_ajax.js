var counter = 0;//set this to what ever you want the start # to be
// var ducks = data.duckCount;
	countUP();//call the function once	
	function countUP () {
		counter++;//increment the counter by 1
		setTimeout ( "countUP()", 1000 );//runs itsself after 1000 miliseconds
		//console.log(counter);uncomment to can see it in action, only with firebug
	}