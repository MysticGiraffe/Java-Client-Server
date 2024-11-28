package prog1415;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.List;

import javax.swing.*;

//PROG1415 Developing Mobile Applications
//Developing Java Client Server Applications
//Assignment 2
//Due: October 27th 2024
//Emily Little

public class Client{
	
	//Networking Objects
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	
	private boolean go = false;
	
	static String userName;
	
	//holds all connected clients from server
	List<String> connectedClients = new ArrayList<String>();
	
	//Put things in memory in constructor
	public Client(int portNumber, String name) {
		//connect to server
		try {
			userName = name;
			
			socket = new Socket("localhost", portNumber);
			//Construct output stream before input stream
			out = new ObjectOutputStream(socket.getOutputStream());
			in = new ObjectInputStream(socket.getInputStream());
		}
		catch (IOException e) {
			e.printStackTrace();
			//Display popup if cannot connect to server
			PortConnection.connectionRefuesed();
		}
		
		//Send the user name to server
		sendMessage(name, "", "");
		
		//Start thread loop 
		go = true;
		new readMessages().start();
	}
	
	class readMessages extends Thread {
		@Override
		public void run() {
			while (go) {
				try {
					Object obj = in.readObject();
					
					//Beginning of string determines what sent data is needed for
					if (obj instanceof String) {
						
						String data = obj.toString();
						
						//Add a client to the connected client list to display to on client
					
						if (data.startsWith("CONNECTEDCLIENT:")) {
							String starting = "CONNECTEDCLIENT:";
							String client = data.substring(starting.length());

							//Add user name to list
							connectedClients.add(client);
						}
						//All the client names are sent over, update client list on screen
						else if (data.startsWith("ALLCONNECTED:")){
							updateClients();
							//Clear the list to get ready for next update
							connectedClients.clear();
						}
						//Message for all clients
						else if (data.startsWith("MESSAGE:")){
							String starting = "MESSAGE:";
							MessagingFrame.chatBox.append(data.substring(starting.length()) + "\n");
						}
						//Private message from another client
						else if (data.startsWith("PRIVATE:")) {
							String starting = "PRIVATE:";
							MessagingFrame.chatBox.append(data.substring(starting.length()) + "\n");
						}
					}
				}
				catch (IOException | ClassNotFoundException e) {
					closeEverything();
				}
			}
		}
	}
	
	//Method to update client list on screen 
	public void updateClients() {
		//reset client list and combo box for private messages
		MessagingFrame.onlineClients.setText("");
		MessagingFrame.chatOptions.removeAllItems();
		MessagingFrame.chatOptions.addItem("Chat All");
		
		for (String s : connectedClients) {
			MessagingFrame.onlineClients.append(s + "\n");
			MessagingFrame.chatOptions.addItem(s);
		}
			
	}
	
	public void sendMessage(String message, String messageType, String sendTo) {
		
		if (message.length() > 0) {
			try {
				//Message for all clients
				if (messageType == "General") {
					out.writeObject("CHATALL:" + message);
				}
				//Private message, include client message is for
				else if (messageType == "Private") {
					out.writeObject("PRIVATE:" + sendTo + ":"  + message);
				}
				//Sending over user name
				else {
					out.writeObject(message);
				}
				out.flush();
				MessagingFrame.message.setText("");
				MessagingFrame.message.requestFocus();
			}
			catch (IOException e1) {
				closeEverything(); 
				e1.printStackTrace();
			}
		
		}
	}
	
	public void closeEverything() {
		//Close client connections
		try {
			//Stop threads
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
