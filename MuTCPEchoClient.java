import java.io.*;
import java.net.*;
import java.util.*;

public class MuTCPEchoClient {
	/* Our socket end */
	static Socket clientSocket;
	/* For writing to socket */
	static PrintStream outToServer;
	/* For reading from user */
	static BufferedReader inFromUser;

	/* Map of the request */
	Packet packet;
	HashMap<String, String> message;
	//Boolean that turns true when the user wants to disconnect and isn't the highest bidder in any auction
	static boolean exit = false;

	//Ardit & Learta
	public void connect(String host, int port) {

		System.out.println("-- Client connecting to host/port " + host + "/" + port + " --");

		/* Connect to the server at the specified host/port */
		try {
			clientSocket = new Socket(host, port);
			/* Create a buffer to hold the user's input */
			inFromUser = new BufferedReader(new InputStreamReader(System.in));
			/* Create a writing buffer to the socket */
			outToServer = new PrintStream(clientSocket.getOutputStream(), true);
			/* Create a reading buffer to the socket */

		} catch (UnknownHostException e) {
			System.out.println("Can not locate host/port " + host + "/" + port);
			return;
		} catch (IOException e) {
			System.out.println("Could not establish connection to: " + host + "/" + port);
			return;
		}

		System.out.println("<-- Connection established  -->");
		new Thread(new ClientListener(clientSocket)).start();
		try {
			/* Continue forever until user types 'exit' */
			/* request = {1-6}, type of function */
			//

			message = new HashMap<String, String>();
			//Display the menu while the client is connected in the auction
			 while (!exit) {
				int request = 0;
				String menuString = "" + "+-------------------------------------------------+\n"
						+ "+                      MENU                       +\n"
						+ "+-------------------------------------------------+\n"
						+ "| Type 1 if you want to create an auction         |\n"
						+ "+-------------------------------------------------+\n"
						+ "| Type 2 if you want to see the list of auctions. |\n"
						+ "+-------------------------------------------------+\n"
						+ "| Type 3 if you want to register in an auction.   |\n"
						+ "+-------------------------------------------------+\n"
						+ "| Type 4 if you want to bid to an auction.        |\n"
						+ "+-------------------------------------------------+\n"
						+ "| Type 5 if you want to see the highest bid.      |\n"
						+ "+-------------------------------------------------+\n"
						+ "| Type 6 if you want to withdraw from an auction. |\n"
						+ "+-------------------------------------------------+\n"
						+ "| Type 'exit' to disconnect from the auction      |\n"
						+ "+-------------------------------------------------+";
				System.out.println(menuString);
				String userInput = inFromUser.readLine();

				if (userInput.equals("exit")) {
					// Close all of our connections.
					outToServer.println("exit");
					continue;
				} else {
					while (true) {
						try {
							/* Read client's message through the User Interface input buffer */
							request = Integer.parseInt(userInput);
							if (request >= 1 && request <= 6) {
								packet = new Packet(request);
								break;
							} else {
								System.out.println("Please write a number between 1-6");
								userInput = inFromUser.readLine();
							}
						} catch (NumberFormatException e) {
							System.out.println("That is not a number. Please, provide a number between 1-6");
							userInput = inFromUser.readLine();
						}

					}
				}

				switch (request) {
					case 1:
						createAuction();
						break;
					case 3:
						register();
						break;
					case 4:
						placeBid();
						break;
					case 5:
						getHighestBid();
						break;
					case 6:
						withdrawFromAuction();
						break;
				}

				/* Send the message to server */
				outToServer.println(packet.getMessage());

			}
		} catch (IOException e) {
			System.out.println(e);
		}
	}

	//Ardit
	//User creates a new Auction
	private void createAuction() {
		//Array of properties that lets us print different strings depending on what field we want
		String[] properties = { "Item Name", "Item Description", "Close Auction type", "Starting Price", "Time" };
		String userInput = "";

		//Get input for all item properties
		for (int i = 0; i < properties.length; i++) {
			System.out.println(
					"Please write out the " + properties[i] + ". Type cancel if you want to cancel the auction.");
			if(i==2){
				System.out.println("Please write, 0 for a fixed timer. Write 1, for allowing the timer to extend.");
			}
			try {
				userInput = inFromUser.readLine();
			} catch (IOException e) {
				System.out.println(e);
			}	
			if (userInput.equals("cancel")) {
				//If the user wants to go back to the menu, discard the current message
				message.clear();
				break;
			}
			//if item properties are inserted, lower variable i for 1
			switch (i) {
				case 0:
					if (!packet.insert("name", userInput))
						i--;
					break;
				case 1:
					if (!packet.insert("dscp", userInput))
						i--;
					break;
				case 2:
					if (verify(userInput, i)) {
						if (!packet.insert("closeauction", userInput)) {
							i--;
						}
					} else {
						i--;
					}
					break;
				case 3:
					if (verify(userInput, i)) {
						if (!packet.insert("price", userInput)) {
							i--;
						}
					} else {
						i--;
					}
					break;
				case 4:
					if (verify(userInput, i)) {
						if (!packet.insert("time", userInput)) {
							i--;
						}
					} else {
						i--;
					}
					break;
			}
		}

	}

	//Learta
	private void register() {
		System.out.println(
				"Type the ID of the auction you want to register in. Write cancel if you want to return to the previous menu.");
		getID();
	}

	//Ardit
	private void getHighestBid() {
		System.out.println(
				"Type the ID of the auction you want to get the highest bid of. Write cancel if you want to return to the previous menu.");
		getID();
	}

	//Learta
	//Method that loops until the user provides an integer for the id or writes cancel. Upon getting an ID, adds it to the message
	private void getID() {
		String input = "";
		while (true) {
			try {
				input = inFromUser.readLine();
			} catch (IOException e) {
				System.out.println(e);
			}
			if (input.equals("cancel")) {
				message.clear();
				break;
			} else if (verify(input, 3)) {
				packet.insert("id", input);
				break;
			}
		}
	}

	//Ardit
	//Method that takes 2 inputs. The first is the id, managed by getID(), the other is the bid. Both must be positive integers/
	private void placeBid() {
		System.out.println(
				"Type the id of the auction you want to bid in. Write cancel if you want to return to the previous menu.");
		String input = "";

		bidPrompt: while (true) {
			getID();
			int price;
			System.out.println("Type the amount you want to bid: ");

			while (true) {
				try {
					input = inFromUser.readLine();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				if (input.equals("cancel")) {
					message.clear();
					break bidPrompt;
				} else if (verify(input, 3)) {
					price = Integer.parseInt(input);
					packet.insert("price", String.valueOf(price));
					break bidPrompt;
				}
			}
		}

	}
	
	//Learta
	//Close the active buffers and socket 
	public static void closeConnection() {
		try {
			inFromUser.close();
			outToServer.close();
			clientSocket.close();
			exit = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//Learta
	private void withdrawFromAuction() {
		System.out.println("Type the ID of the auction you want to withdraw from: ");

		getID();
	}

	//Learta
	/**
	 * Precondition: Type is a number that corresponds to the following mapping:
	 * {0-1} = Must be String, {2} = Must be 0 or 1. {3,4} = Must be an integer.
	 * <br>
	 * <br>
	 * Postcondition: The method returns true if the input is valid and false if
	 * it's not
	 */
	private boolean verify(String message, int type) {
		if (type >= 2) {
			try {
				int number = Integer.parseInt(message);
				if (number < 0) {
					System.out.println("Incorrect type of input. Please input a positive number.");
					return false;
				}
				if (type == 2) {
					if (number == 0 || number == 1) {
						return true;
					} else {
						System.out.println("Incorrect type of input. Please type 0 or 1.");
						return false;
					}
				}
			} catch (NumberFormatException e) {
				if (type == 2) {
					System.out.println("Incorrect type of input. Please type 0 or 1.");
				} else {
					System.out.println("Incorrect type of input. Please type an integer number.");
				}
				return false;
			}
		}
		return true;
	}

	public static void main(String[] argv) {
		/* Holds the server's name */
		String server;
		/* Holds the server's port number */
		int port;

		/* The first argument is the server's name */
		server = argv[0];
		/* The second argument the port that the server accepts connections */
		port = Integer.parseInt(argv[1]);

		/* Create a new instance of the client */
		MuTCPEchoClient myclient = new MuTCPEchoClient();

		/* Make a connection. It should not return until the client exits */
		myclient.connect(server, port);

		System.out.println("<-- You have exited. Thanks for being part of the Auction! -->");
	} /* End main method */

} // MuTCPEchoClient
