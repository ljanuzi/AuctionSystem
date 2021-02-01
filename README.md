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
