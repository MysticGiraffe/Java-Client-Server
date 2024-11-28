package nc.vanscoy;

import java.io.*;
import java.util.*;

import javax.swing.*;

//PROG1415 Developing Mobile Applications
//Developing Java Client Server Applications
//Assignment 2
//Due: October 27th 2024
//Emily Little

public class PortConnection {

	//Port number
	int port;
	
	JFrame frame;
	
	public PortConnection(JFrame frame) {
		this.frame = frame;
		
		//Get file with port number
		File portNumber = new File("src/Assets/portnumber.txt");
				
		try {
			//scanner to read file
			Scanner readPort = new Scanner(portNumber);
					
			//Read port from file
			while(readPort.hasNextLine()) {
				try {
					port = Integer.parseInt(readPort.nextLine());
				}
				catch(NumberFormatException ex) {
					//If for whatever reason the line read cannot be converted into an Integer, default to 8000
					port = 8000;
				}
			}
					
			//Close the scanner
			readPort.close();
			System.out.println(port);
					
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int confirmPortNumber() {
		//Check with user to see if they want to use the port read from the file
		return JOptionPane.showConfirmDialog(frame, "Use port " + port + "?", "Accept Port", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	}
	
	
	public void writeNewPortNumber() {
		//User does not want to use port written from file, get user input to use a new port, and write new port to file
		String s = (String)JOptionPane.showInputDialog(frame, "Enter a new valid port number: ", port);
		
		//Make user enter something 
		while (s == null || s.isBlank()) {
			s = (String)JOptionPane.showInputDialog(frame, "Enter a new valid port number: ", port);
		}
		
		//Make sure that entered port is valid. That entered value is a number and is not greater than 65 535
		try {
			port = Integer.parseInt(s);
			
			if (port > 65535 || port < 0) {
				//Port numbers above 65535 and below 0 do not exist, don't allow entry
				writeNewPortNumber();
			}
		}
		catch (NumberFormatException ex){
			//Did not enter a port number, call method again to bring up box again
			writeNewPortNumber();
		}
		
		//Port entered is valid, write to file
		FileWriter portFile;
		try {
			portFile = new FileWriter("src/Assets/portnumber.txt");
			portFile.write(String.valueOf(port));
			portFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
