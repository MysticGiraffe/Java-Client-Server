//PROG1415
//Vanscoy


package nc.vanscoy;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;

import java.awt.event.*;

//PROG1415 Developing Mobile Applications
//Developing Java Client Server Applications
//Assignment 2
//Due: October 27th 2024
//Emily Little

//a java server program
public class TCPServer extends JFrame implements Runnable, WindowListener {

	private static final long serialVersionUID = 1L;
	
	//Server
	private ServerSocket server;
	
	private boolean go = false;
	
	static JTextArea output = new JTextArea();
	static List<Client> clients = new ArrayList<Client>();
	static List<String> messages = new ArrayList<String>();
	
	//List to hold client names
	static List<String> connectedClients = new ArrayList<String>();
	
	//Private conversations lists
	static List<String> privateMessages = new ArrayList<String>();
	static List<String> privateSendTo = new ArrayList<String>();
	static List<String> privateSentFrom = new ArrayList<String>();
	
	//Hold port Number
	int portNumber;
	
	public	TCPServer() {
		//build interface
		output.setEditable(false);
		this.getContentPane().add(new JScrollPane(output));
		this.setTitle("Server");
		this.setBounds(100,100,300,500);
		this.setVisible(true);
		this.addWindowListener(this);
		
		//Confirm the port number before creating connection 
		PortConnection port = new PortConnection(this);
				
		//Check if user wants new port number
		int choice = port.confirmPortNumber();
				
		if (choice == 1) {
			//Get the new port number and write it to file
			port.writeNewPortNumber();
		}
				
		//Assign the port number
		portNumber = port.port;
		
		//start server main thread
		Thread acceptThread = new Thread(this);
		acceptThread.setPriority(Thread.MAX_PRIORITY);
		acceptThread.start();
	}

	@Override
	public void run() {
		try {
			//construct the server
			server = new ServerSocket(portNumber);
			
			output.append("Ready using port " + portNumber + "...\n");
			
			//start additional server threads
			go = true;
			new BroadCast().start();
		} catch (Exception e) {
			output.append("Server launch failed...\n");
			return;
		}
		
		//loop to receive client connections
		int count = 1;
		while(go) {
			try {
				output.append("Waiting...\n");
				Socket socket = server.accept();
				//start a new process for each client
				new Client(socket);
				
			} catch (IOException e) {
				output.append("Attempt to connect " + count + " failed...\n");
				if(count++ <= 3) 
					continue;
				else
					break;
			}
		}
		output.append("Server is no longer accepting clients...\n");
	}

	//a server thread to broadcast text messages to all connected clients
	class BroadCast extends Thread {
		@Override
		public void  run() {
			output.append("Broadcasting enabled...\n");
			while(go) {
				//allow other threads to run
				Thread.yield();
				//check for clients and messages
				if(messages.size() > 0 && clients.size() > 0) {
					String msg = messages.get(0);
					for(int x=0;x<clients.size();x++)
						try {
							clients.get(x).out.writeObject(msg);
						} catch (IOException e) {
							output.append("Error writing to client...\n");
							clients.get(x).closeConnections();
							clients.remove(x);
						}
					messages.remove(0);
				}
			}
		}
	}
	
	//Server thread to update connected client list to user
	static class UpdateClients extends Thread {
		public void run() {
			//loop through all clients, and loop through all connected client names
			//Send each name with a special header to tell client it is receiving a connected client name
			//Send ALLCONNECTED: message to tell client that all connected clients are received 
			try {
				for(int x=0;x<clients.size();x++) {
					for(int y=0;y<connectedClients.size();y++) {
						clients.get(x).out.writeObject("CONNECTEDCLIENT:" + connectedClients.get(y));
					}
					//Tell client that all connected names sent, can update their client list
					clients.get(x).out.writeObject("ALLCONNECTED:");
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//Server thread to send private message
	static class PrivateMessage extends Thread {
		public void run() {
			try {
				//loop through connected clients
				for(int x=0;x<clients.size();x++) {
					//check if client user name is equal to sender or recipient, if yes then send them the message
					if (clients.get(x).userName.equals(privateSendTo.get(0)) || clients.get(x).userName.equals(privateSentFrom.get(0))) {
						clients.get(x).out.writeObject("PRIVATE:Private message from " + privateSentFrom.get(0) + " to " + privateSendTo.get(0) + ": " + privateMessages.get(0));
					}
				}
				//Remove the message, sender, and recipient from their respective lists
				privateMessages.remove(0);
				privateSendTo.remove(0);
				privateSentFrom.remove(0);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		this.go = false;
		for(int x=0;x<clients.size();x++)
			clients.get(x).closeConnections();
		try {
			server.close();
		} catch (IOException e) {}
		System.exit(0);
		
	}
	
	@Override
	public void windowActivated(WindowEvent arg0) {}

	@Override
	public void windowClosed(WindowEvent arg0) {}

	@Override
	public void windowDeactivated(WindowEvent arg0) {}

	@Override
	public void windowDeiconified(WindowEvent arg0) {}

	@Override
	public void windowIconified(WindowEvent arg0) {}

	@Override
	public void windowOpened(WindowEvent arg0) {}

	public static void main(String[] args) {
		new TCPServer();
	}
}
