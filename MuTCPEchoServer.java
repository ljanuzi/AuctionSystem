import java.io.*;
import java.net.*;
import java.util.*;

public class MuTCPEchoServer {
	public static Map<Integer, Auction> auctionList = new HashMap<Integer, Auction>();
	/* This is the port on which the server is running */
	private int serverPort;
	public static Hashtable<String, PrintStream> outputChannels = new Hashtable<String, PrintStream>();

	/* Constructor Method */
	public MuTCPEchoServer(int port) {
		serverPort = port;
	} /* End Contructor Method */

	//Lola
	/* Listen Method */
	public void listen() {
		/* Socket for connections made */
		Socket connectionSocket = null;
		/* Server's listening socket */
		ServerSocket welcomeSocket;

		// Create a socket to listen on.
		try {
			welcomeSocket = new ServerSocket(serverPort);
		} catch (IOException e) {
			System.out.println("Could not use server port " + serverPort);
			return;
		}

		// Listen forever for new connections. When a new connection
		// is made a new socket is created to handle it.
		while (true) {
			System.out.println("<-- Server listening on socket " + serverPort + " -->");
			/* Try and accept the connection */
			try {
				connectionSocket = welcomeSocket.accept();
			} catch (IOException e) {
				System.out.println("Error accepting connection.");
				continue;
			}

			/* A connection was made successfully */
			System.out.println("<-- Made connection on server socket -->");
			/* Create a thread to handle it. */
			handleClient(connectionSocket);
		}
	} /* End listen method */

	//Lola
	//Create a thread to handle a client. Puts the output channel of that thread in the outputChannels Hashtable.
	public void handleClient(Socket clientConnectionSocket) {
		System.out.println("<-- Starting thread to handle connection -->");
		try {
			PrintStream outToClient = new PrintStream(clientConnectionSocket.getOutputStream(), true);
			outputChannels.put(clientConnectionSocket.getInetAddress().getHostAddress(), outToClient);
			new Thread(new ConnectionHandler(clientConnectionSocket, outToClient)).start();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	} /* End handleClient method */

	//Params: message - the message being sent to the clients
	//		bidder - the auctioneer placing the bid
	//		auction - the auction where the bid is taking place
	// Sends a message to all registered users when a bid has taken place
	public static synchronized void sendMessage(String message, String bidder, Auction auction) {
		LinkedList<Auctioneer> registeredUsers = auction.getRegisteredUsers();
		for (Auctioneer user : registeredUsers) {
			if (user.getIpAddress() != bidder)
				outputChannels.get(user.getIpAddress()).println(message);
		}
		outputChannels.get(auction.getSeller().getIpAddress()).println(message);
	}
	
	//Vlera
	/**Params:
	* winningBid - the highest bid
	* registeredUsers - the List of users that are registered in the Auction
	* auctionid - the id of the auction
	*/
	public static synchronized void endAuction(Bid winningBid, LinkedList<Auctioneer> registeredUsers, int auctionid) {
		Auction auction = auctionList.get(auctionid);
		if (winningBid != null) {
			for (Auctioneer user : registeredUsers) {
				outputChannels.get(user.getIpAddress())
						.println("Auction " + auction.getItem().getAuctionid() + " has ended. The winner is: "
								+ winningBid.getClient().getIpAddress() + ", with a bid of " + auction.getHighestBid().getPrice()
								+ " euros.");
			}
			outputChannels.get(winningBid.getClient().getIpAddress()).println("Congratulations on winning the auction "
					+ auction.getItem().getAuctionid() + " with a bid of " + auction.getHighestBid().getPrice() + " euros.");

			outputChannels.get(auction.getSeller().getIpAddress())
					.println("Congratulations, the item " + auction.getItem().toString()
							+ " has been sold with a bid of " + auction.getHighestBid().getPrice() + "euros. :)");
		} else {
			for (Auctioneer user : registeredUsers) {
				outputChannels.get(user.getIpAddress())
						.println("Auction " + auction.getItem().getAuctionid() + " has ended.");
			}
			outputChannels.get(auction.getSeller().getIpAddress())
					.println("Timer ran out, no bids were made. Maybe next time :(");
		}
		auctionList.remove(auction.getItem().getAuctionid());
	}

	//Ardit & Learta
	//Params: registeredUsers - Users belonging to the auction that is ending
	//		auctionId - The id of the auction that is ending
	// 		message - The message being sent to the registered users
	public static synchronized void sendCountdown(LinkedList<Auctioneer> registeredUsers, int auctionid, String message){
		Auction auction = auctionList.get(auctionid);
		String countDownMessage = "";
		if(auction.getHighestBid() == null)
			countDownMessage = "Last bid for item " + auction.getItem().getItemName() + " is " + auction.getItem().getStartingPrice() + " going " + message;
		else
			countDownMessage = "Last bid for item " + auction.getItem().getItemName() + " is " + auction.getHighestBid().getPrice() + " going " + message;
			
		for (Auctioneer user : registeredUsers) {
			outputChannels.get(user.getIpAddress())
					.println(countDownMessage);
					//.println("Auction "+ auctionid + " ending in " + message + " seconds!");
		}

		outputChannels.get(auctionList.get(auctionid).getSeller().getIpAddress())
					.println(countDownMessage);

	}
	//A. Ymeri
	public static void main(String argv[]) throws Exception {
		/* The port the server is listening on */
		int port;

		/* Parse the port which is passed to program as an arguement */
		port = Integer.parseInt(argv[0]);
		/* Create a new instance of the echo server */
		MuTCPEchoServer myServer = new MuTCPEchoServer(port);
		/* Listen for connections. It can not return until the server is shut down. */
		myServer.listen();
		/* Display message of server shutting down */
		System.out.println("<-- Server exiting -->");
	} /* End main method */

} /* End class MuTCPEchoServer */