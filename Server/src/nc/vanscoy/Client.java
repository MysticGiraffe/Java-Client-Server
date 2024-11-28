package nc.vanscoy;

import java.io.*;
import java.net.Socket;

//PROG1415 Developing Mobile Applications
//Developing Java Client Server Applications
//Assignment 2
//Due: October 27th 2024
//Emily Little

public class Client implements Runnable {

	//streams to read and write with client instances
	ObjectInputStream in = null;
	ObjectOutputStream out = null;
	boolean go = false;
	
	//Socket
	Socket socket;
	
	//Holds Client user name
	String userName;

	public Client(Socket socket) {
		try {
			this.socket = socket;
			
			//create IO streams
			this.out = new ObjectOutputStream(socket.getOutputStream());
			this.in = new ObjectInputStream(socket.getInputStream());
			
			//Set client user name. It's the first thing sent over
			userName = (String)in.readObject();
			
			//add client to the server's client list
			TCPServer.clients.add(this);
			TCPServer.output.append("Client " + TCPServer.clients.size() + " " + userName +" connected...\n");
			
			//Add user name to list
			TCPServer.connectedClients.add(userName);
			
			//Run thread to update connected clients
			new TCPServer.UpdateClients().start();
			
			//start a thread to read incoming client messages
			this.go = true;
			Thread thread = new Thread(this);
			thread.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch(ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	@Override
	public void run() {
		while(go) {
			try {
				//wait until a message comes from the client
				Object obj = in.readObject();
				//add valid message to the queue
				if(obj instanceof String) {
					String incomingMessage = obj.toString();
					if (incomingMessage.startsWith("CHATALL:")){
						//Message that is sent to all clients
						//Header to specify that chat is available to all clients
						String start = "CHATALL:";
						//Add client user name to the message
						TCPServer.messages.add("MESSAGE:" + userName + ": " + incomingMessage.substring(start.length()));
					}
					else if (incomingMessage.startsWith("PRIVATE:")) {
						//client sent a private message. Make sure that only sender and recipient get the message
						//Header to specify that message is private between two clients
						String start = "PRIVATE:";
						String removeStart = incomingMessage.substring(start.length());
						int messageIndex = removeStart.indexOf(':');
						//Get send to and message from in coming message
						String sendTo = removeStart.substring(0, messageIndex);
						String message = removeStart.substring(messageIndex + 1);
						
						//Add message, sender, and recipient 
						TCPServer.privateMessages.add(message);
						TCPServer.privateSendTo.add(sendTo);
						TCPServer.privateSentFrom.add(userName);
						
						//start a new private message thread
						new TCPServer.PrivateMessage().start();
					}
				}
				
			} catch (Exception e) {
				
				//Remove user name from user name list
				for(int x=0;x<TCPServer.clients.size();x++) {
					if (TCPServer.clients.get(x).userName == this.userName) {
						TCPServer.connectedClients.remove(TCPServer.clients.get(x).userName);
						break;
					}	
				}
				
				//remove from the client list if streams are broken
				TCPServer.clients.remove(this);
				
				//Run thread to update connected clients
				new TCPServer.UpdateClients().start();
				TCPServer.output.append("Client " + this.userName + " disconnected...\n");
				TCPServer.output.append("Waiting...\n");
				closeConnections();
			} 
		}
	}
	
	public void closeConnections() {
		//Close object input/output stream, and close the socket since client is no longer connected
		try {
			//Stop the thread loop
			go = false;
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
