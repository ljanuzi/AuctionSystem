# AuctionSystem
Network Programming Using the Sockets Interface
The goal of this project was to simulate an auction system by using the sockets for communication. 
The objective of this project was to generate a system in which the user can create an auction by placing an item for sale, 
bid for an item in a particular auction in which this participant is registered, withdraw from an auction, 
as well as closing the auction in the case if it was created by the client making the request.
However, all of the listed functionalities represent the elementary features of the produced system. 
A prerequisite for the development of the project was to decide on the transport layer communication mechanism which is essential for the system operation. 
Another significant design decision would be the representation of the application protocol, 
more specifically the actual manner in which data is exchanged and interpreted in the developed auction system. 
This data is represented by the packet class, which is designed for the purpose of serialization and parsing of data for
the communication between the client and the server. 

Manual
-------------------------------
Download the files on a machine with Java SE installed!

For the server, on your terminal execute and run the MuTCPEchoServer.java class:
(execution command: javac MuTCPEchoServer.java | running command: java MuTCPEchoServer < port >)
  
For the client, on your terminal execute and run the MuTCPEchoClient.java class: 
(execution command: javac MuTCPEchoClient.java | running command: java MuTCPEchoClient < ip address or machineName> <port the server is runnning on >)

Both client and server can run on the same device or on different devices in the same Local Area Network.

For devices that are not connected in the same Local Area Network: 
The machine running the MuTCPEchoServer class has to enable port sharing on their routers.
The proceeding process does not change from the process mentioned above
