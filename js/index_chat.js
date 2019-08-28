const express = require('express'),
http = require('http'),
app = express(),
server = http.createServer(app),
io = require('socket.io').listen(server);
app.get('/', (req, res) => {

res.send('Chat Server is running on port 3001')
});
io.on('connection', (socket) => {
console.log('user connected');
socket.on('join', function(userNickname) {
        console.log(userNickname +" : has joined the chat " );
		socket.broadcast.emit('userjoinedthechat',userNickname +" has joined the chat ");
    });

socket.on('messagedetection', (senderNickname,messageContent, usertype) => {    
       //log the message in console 
       console.log(senderNickname+" : "+messageContent);  
      //create a message object    
      let  message = {"message":messageContent, 
                      "senderNickname":senderNickname,
					  "usertype": usertype};      
	// send the message to all users including the sender  using io.emit        
      io.emit('message', message);  
	//  console.log(message);
      });
	  
	  socket.on('usertyping', (senderNickname) => {      	  
		socket.broadcast.emit('typing', senderNickname);
      });
	  
     socket.on('disconnect', function(userNickname) {
        console.log(userNickname +' has left');
        socket.broadcast.emit( "userdisconnect" ,'user has left');     
    });
})
 
server.listen(3001,()=>{
console.log('Node app is running on port 3001');
});