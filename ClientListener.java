import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientListener implements Runnable{
	
	private Socket clientSocket;
	//Buffer that listens to server requests
	BufferedReader inFromServer;
	public  ClientListener(Socket clientSocket){
		this.clientSocket = clientSocket;
	}
	//Ardit Ymeri, Lola Davidovikj, Learta Januzi, Vlere Januzi
	public void run(){
		try {

				inFromServer = new BufferedReader(new InputStreamReader( clientSocket.getInputStream()) );
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		while(true) {
			try {
				if(inFromServer.ready()){
					//Upon receiving a message, print the message and check if it's an attempt at disconnecting
					//If exit, call the method for closing the socket and the other buffers
					String message = inFromServer.readLine();
					System.out.println(message);
					if(message.equals("exit")){
						inFromServer.close();
						MuTCPEchoClient.closeConnection();
					}else{
						System.out.println(inFromServer.readLine());
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
