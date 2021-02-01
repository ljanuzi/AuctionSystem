import java.io.*;
import java.net.*;
import java.util.*;

public class ConnectionHandler implements Runnable {
	// Socket for our endpoint
	protected Socket echoSocket;
	

	// Holds messages we get from client
	private String clientRequest = "";
	// Input object
	private BufferedReader inFromClient;
	// Output object
	private PrintStream outToClient;
	// Client's name
	private String peerName;

	public ConnectionHandler(Socket aSocketToHandle, PrintStream outToClient) {
		echoSocket = aSocketToHandle;
		this.outToClient = outToClient;
	}

	/** * New thread for handling client interaction will start here. */
	//Ardit Ymeri, Lola Davidovikj, Learta Januzi, Vlere Januzi
	public void run() {

		// Attach a println/readLine interface to the socketso we can read and write
		// strings to the socket.
		try {
			/* Get the IP address from the client */
			peerName = echoSocket.getInetAddress().getHostAddress();
			/* Create a writing stream to the socket */
			outToClient = new PrintStream(echoSocket.getOutputStream(), true);
			/* Create a reading stream to the socket */
			inFromClient = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));

		} catch (IOException e) {
			System.out.println("Error creating buffered handles.");
			return;
		}

		//Create auctioneer object with the ip of the connected client
		Auctioneer user = new Auctioneer(peerName);

		System.out.println("Handling connection to client at " + peerName + " --");
		outToClient.println("Welcome to the auction system!");
		while (true) {
			Map<String, String> userPacket;
			try {
				/* Read client's message through the socket's input buffer */
				clientRequest = inFromClient.readLine();

				if(clientRequest.equals("exit")){
					Iterator<Map.Entry<Integer, Auction>> hmIterator = MuTCPEchoServer.auctionList.entrySet().iterator(); 
					String reply = "You cannot exit from a server as you are the highest bidder in auction(s):";
					Stack<Integer> auctionStack = new Stack<Integer>();

					//Iterate through the elements of the hashmap and find all the auctions that 
					//the client is the highest bidder on
					while (hmIterator.hasNext()) { 
						
						Map.Entry<Integer,Auction> mapElement = hmIterator.next(); 

						if(mapElement.getValue().getHighestBid()!=null){
							if(mapElement.getValue().getHighestBid().getClient().getIpAddress().equals(peerName)){
								auctionStack.push(mapElement.getKey());
							}
						}
					} 
					if(auctionStack.empty()){
						outToClient.println("exit");
						/* Close input stream */
						inFromClient.close();
						/* Close output stream */
						outToClient.close();
						/* Close TCP connection with client on specific port */
						echoSocket.close();
						break;
					}
					else{
						while(!auctionStack.isEmpty()){
							reply+=auctionStack.pop()+"\n";
						}
						outToClient.println(reply);
						continue;
					}
					
				}

				userPacket = Packet.parse(clientRequest);

			} catch (IOException e) {
				System.out.println(echoSocket.getInetAddress() + " - " + peerName + " broke the connection.");
				break;
			}

			/* Output to screen the message received by the client */
			System.out.println(userPacket.toString());

			String request = userPacket.get("request");

			//If request is one, use the userPacket map to construct an Item object, then put the item on an auction and the auction on an auctionlist
			if (request.equals("1")) {
				System.out.println("Client " + echoSocket.getInetAddress() + " wants to make auction.");

				Item auctionItem = new Item(userPacket.get("name"), userPacket.get("dscp"),
						userPacket.get("closeauction"), userPacket.get("price"), userPacket.get("time"), generateID());
				Auction auction = new Auction(auctionItem, user);
				MuTCPEchoServer.auctionList.put(auctionItem.getAuctionid(), auction);
				outToClient.println("Auction created with id: " + auctionItem.getAuctionid());
			} else if (request.equals("2")) {
				//If request is 2, return the list of all active auctions by iterating through the auctionlist map
				Iterator<Map.Entry<Integer, Auction>> hmIterator = MuTCPEchoServer.auctionList.entrySet().iterator(); 
				String listOfAuctions = "";
					while (hmIterator.hasNext()) { 
						
						Map.Entry<Integer,Auction> mapElement = hmIterator.next(); 
						listOfAuctions+="\n"+mapElement.getValue().toString();
					} 
				outToClient.println("List of auctions: " + listOfAuctions);
			} else if (request.equals("3")) {
				//If request is 3, try to register a user to the client.
				Auction auction = MuTCPEchoServer.auctionList.get(Integer.parseInt(userPacket.get("id")));
				if (auction == null) {
					outToClient.println("That auction does not exist or is no longer available.");
					
				}
				//Don't register users to their own client
				else if(auction.getSeller().getIpAddress().equals(user.getIpAddress())){
					outToClient.println("You cannot register in your own auction!");
				}
				//Don't register clients to an auction more than once
				 else if(auction.isRegistered(user)) {
					outToClient.println("You are already registered in this auction!");
				}
				//If none of the above, register is successful
				else{
					outToClient.println("Thank you for registering to auction: " + auction.toString());
					auction.register(user);
				}
			} else if (request.equals("4")) {
				//If request is 4, then try to place a bid
				Auction auction;
				try{
					auction = MuTCPEchoServer.auctionList.get(Integer.parseInt(userPacket.get("id")));
				}catch(Exception e){
					System.out.println(e);
					continue;
				}
				if (auction == null) {
					//If the id is not in the auction list, return the appropriate error message
					outToClient.println("That auction does not exist or is no longer available.");
				}else if(!auction.isRegistered(user))
				{
					//Auctioneers who haven't registered to an auction can't bid in it
					outToClient.println("You are not registered to this auction!");
				} else if(auction.getSeller() == user) {
					//Creators of their auctions cannot bid in their own auctions
					outToClient.println("You can not bid in the auction you have created!");
				}else{

					Bid bid;

					int highestBid;
					if (auction.hasBidders()) {
						highestBid = auction.getHighestBid().getPrice();
					} else {
						highestBid = auction.getStartingPrice();
					}

					//Check if bid is higher than the highest bid.
					if (Integer.parseInt(userPacket.get("price")) > highestBid) {
						bid = new Bid(Integer.parseInt(userPacket.get("price")), user);
						auction.addBid(bid);
						outToClient.println("Bid of " + bid.getPrice() + " euros sent successfully.");
						MuTCPEchoServer.sendMessage("Item: " + auction.getItem() + " of auction" + auction.getItem().getAuctionid() + " with a bid of " + bid.getPrice() + " euros sent by " + user.getIpAddress(), user.getIpAddress(), auction);
					} else {
						outToClient.println("Please enter a bid that is higher than " + highestBid + " euros");
					}
				}

			} else if (request.equals("5")) {
				//Get the highest bid of an auction
				Auction auction = MuTCPEchoServer.auctionList.get(Integer.parseInt(userPacket.get("id")));
				if (auction == null) {
					outToClient.println("That auction does not exist or is no longer available");
				} else {
					if (auction.hasBidders()) {
						//If it has bidders, return information about the bidder
						outToClient.println("The highest bid is: " + auction.getHighestBid().getPrice()+"euro \n Bidder: " + auction.getHighestBid().getClient().getIpAddress() + " At time: " + auction.getHighestBid().getTimeBidIsPlaced());
					} else {
						//If it has no bidders, simply return the starting price
						outToClient.println("This auction has no bidders, the starting price is: " + auction.getStartingPrice() + "euro");
					}
				}
			} else if (request.equals("6")) {
				//Try to withdraw from an auction, provided you are not the highest bidder in it
				Auction auction = MuTCPEchoServer.auctionList.get(Integer.parseInt(userPacket.get("id")));
				if (auction == null) {
					outToClient.println("That auction does not exist or is no longer available");
				} else {

					if (auction.hasBidders()) {
						//Can't withdraw from an auction if you are the highest bidder
						if (auction.getHighestBid().getClient().equals(user)) {
							outToClient.println("You cannot withdraw from an auction where you are the highest bidder!");
						} else {
							if (auction.removeAllBids(user)) {
								//If the number of removed bids is higher than 0, tell the user they have withdrawn
								outToClient.println("You have successfully withdrawn from the auction!");
							} else {
								//If 0, tell the user that nothing has changed
								outToClient.println("You are not a bidder in this auction.");
							}
						}
					} else {
						outToClient.println("You are not a bidder in this auction.");
					}

				}
			}

			
		}

	} /* End run method */

	//Generates a unique ID. Maximum number of IDs is 100000, although this can be adjusted without 
	//interference in other methods/
	//L. Januzi
	public int generateID() {
		int id = (int) (Math.random() * 100000);
		while (true) {
			if (MuTCPEchoServer.auctionList.get(id) == null) {
				return id;
			} else {
				id = (int) (Math.random() * 100000);
			}
		}
	}

	
} // end class
	