
var TIME_LIMIT = 10; // wait 10 secs for users to answer
var SLEEP_TIME = 5; //wait 5 sec before next round starts

function User(id,name,question,correct_answer,player_answer,score)
{
	this.id=id; //keep connection id(socket id)
	this.name=name;
	this.question = question;
	this.correct_answer = correct_answer;
	this.player_answer = player_answer;
	this.score = score;
}

function Client(id,name)
{
	this.id=id;
	this.name=name;
}

var ArrayList = require('arraylist')

var app = require('express')();
var http = require('http').Server(app);
var io = require('socket.io')(http);


//create list of playing users
var listUsers = new ArrayList;
var listClients = new ArrayList;


//create server
app.get('/', function(req,res){
	res.sendFile('index.html',{root: __dirname}) //when user use GET method, reutrn a default index
	
});


function sleep(sec)
{
	return new Promise(resolve => setTimeout(resolve,sec*1000)); //sleep
}

var two_playing = false;
var last_question;
async function countDown(){ 
	if(two_playing == true){
		//broadcast count down timer for all clinet

		var timeTotal = TIME_LIMIT;
		do{
			//send timer to all client
			io.sockets.emit('broadcast',timeTotal);
			timeTotal--;
			await sleep(1); //sleep 1 sec
		}while(timeTotal>0 && two_answers != 2);

		//after time limit is finished
		processResult(); //send reward to winner

		//here i should check if there's more questions to display or not
		if(last_question == 0){
			//reset data for next roun
	
			timeTotal = TIME_LIMIT;
			money =0;
			io.sockets.emit('wait_before_restart', SLEEP_TIME); //send msg wait server calculate result before next round
			io.sockets.emit('money_send',0); //send total of money to all users next round default is 0
			await sleep(SLEEP_TIME); //wait

			io.sockets.emit('restart',"new_question"); //send msg next round for all client

			countDown();
		}else if(last_question == 1){
			
			io.sockets.emit('broadcast',"0");
			await sleep(SLEEP_TIME); 
			io.sockets.emit('restart',"stop");
			//show to each user what score the other one obtained
			
			}
	}else{
		io.sockets.emit('broadcast',"0");
	}
}


	
	var name = 0;
function processResult(){
	timeTotal=0; 
	var winnerName = 0;
	two_answers=0;

	console.log('Server is processing data');

	//remove duplicate data
	
	listUsers.unique();
	
	//count in the list of users how many winners
	var count = listUsers.find(function(user){
		return user.correct_answer == user.player_answer;	
		
	}).length;
	
//find the winner to show later
	 var findUser = listUsers.find(function(user){
		if(user.correct_answer == user.player_answer){
			winnerName = user.name;		
		}
	});
	
	//send to each user what the other one has answered
		 for(var i = 0; i < listUsers.length;i++){
		//console.log('answers ' + listUsers.get(i).player_answer);
		io.sockets.emit('opponent_answer',{name: listUsers.get(i).name, answer: listUsers.get(i).player_answer});
		
	 }
	 
	 //daca nu au rasp amandoi, tre sa le scad un punct fiecaruia
if(listUsers.length == 0){
	io.sockets.emit('lose','no answer'); 
}else if(listUsers.length ==1){
	//find the user that answered
		listUsers.find(function(user){
			name =user.name;
		if(user.correct_answer == user.player_answer){
			io.to(user.id).emit('reward','congrats'); 
		}
		else{
			io.to(user.id).emit('lose','maybe next time');
			console.log('Winner test: ' + winnerName);
			//show to the loser who the winner is
			io.to(user.id).emit('winner',winnerName); 
		}					
	});	
	//the other one that didnt answer
	listClients.find(function(client){
		if(client.name != name){
			io.to(client.id).emit('lose','no answer');
		}
	});
	
}else{
//both players answered
	
	//now just find winner and loser to send reward
	listUsers.find(function(user){
		if(user.correct_answer == user.player_answer){
			io.to(user.id).emit('reward','congrats'); 
		}
		else{
			io.to(user.id).emit('lose','maybe next time');
			console.log('Winner test: ' + winnerName);
			//show to the loser who the winner is
			io.to(user.id).emit('winner',winnerName); 
		}
						
	});	
}
	io.sockets.emit('show_correct',correctA); 
	
	console.log('We have ' + count+' winners');
	
	if(last_question == 1){
			for(var i = 0; i < listUsers.length;i++){
				io.sockets.emit('opponent_score',{name: listUsers.get(i).name, score: listUsers.get(i).score});	
			}
	}

	//clear list players
	listUsers.clear();	
	
}
function assignColor(){
		listClients.find(function(client){	
	
			//console.log('test: ' + listClients.get(0).name);		
			//pt primul user da i culoarea mov
			if(client.id == listClients.get(0).id){
			io.to(client.id).emit('color','mov'); 
		}
		else{
			io.to(client.id).emit('color','porto');
	
			}
		});
}
var correctA = 0;
var players_num =0;
var two_answers=0;
//process connection socket
io.on('connection', function(socket){
	console.log('A new user ' + socket.id+ ' is connected');
	players_num++;
	io.sockets.emit('player_num',players_num);
	socket.on('client_connects',function(client){
		var client = new Client(socket.id,client.name);
		listClients.add(client);
		console.log(listClients);
		
		assignColor();

	});
	
	 if (!socket.sentMydata) {
		 io.sockets.emit('url','api.json');
        socket.sentMydata = true;
    }
	
	socket.on('client_send_answer',function(objectClient){
		
		console.log(objectClient); //print object client in json format
		var user = new User(socket.id,objectClient.name, objectClient.question,objectClient.correct_answer, objectClient.player_answer, objectClient.score);
		two_answers++;
		console.log(two_answers);
		correctA = user.correct_answer;
		//save user to list user online
		listUsers.add(user);
		console.log('Total user answers: ' + listUsers.length);
			//console.log(listUsers);
			//iau numele primului player	
	});
	
if(players_num ==2){
	two_playing = true;
		countDown();
}


	socket.on('is_last_question',function(check){
		if(check == "true"){ //daca e ultima intrebare, nu mai restartam
			last_question =1;
		}else{
			last_question =0;
		}
	});
	
	//whenever someone disconnects
socket.on('disconnect',function(){
	 players_num--;
	   console.log('user has left ');
	   two_playing = false;
	   
	   listClients.find(function(client){
			io.to(client.id).emit('opponent_disconnect',0); 

						
	});	


			listClients.find(function(client){	
//	console.log('user: '+client.id+' is leaving'); 
	

		for(var i=0; i< listClients.length; i++){
			console.log('test: ' + listClients.get(i).name);		
		//	if(client.name == listClients.get(i).name){
			 listClients.remove(i);
		//	}		
		}
		
	}); //listClients

	 console.log('list of clients '+listClients.length);
}); //disconnect

});//connection

//start server
http.listen(3000,function(){
	console.log('SERVER GAME STARTED ON PORT: 3000');
});