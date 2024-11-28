package prog1415;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

//PROG1415 Developing Mobile Applications
//Developing Java Client Server Applications
//Assignment 2
//Due: October 27th 2024
//Emily Little

public class LoginFrame extends JFrame implements WindowListener{

	//Panels
	JPanel center = new JPanel(new GridLayout(2,1));
	JPanel centerRowOne = new JPanel(new FlowLayout(FlowLayout.CENTER));
	JPanel centerRowTwo = new JPanel(new FlowLayout(FlowLayout.CENTER));
	
	//Text Fields
	JTextField userName = new JTextField();
	
	//Labels
	JLabel userNameLabel = new JLabel("Enter Username:");
	JLabel portNumberDisplay = new JLabel();
	
	//Button
	JButton login = new JButton("Login");

	//Port number
	int portNumber;
	
	//User name
	static String name;
	
	public LoginFrame() {
		//Create UI and connect events
		this.addWindowListener(this);
		
		userName.setPreferredSize(new Dimension(200, 31));
		
		userName.addActionListener(new Login());
		
		centerRowOne.add(userNameLabel);
		centerRowOne.add(userName);
		
		centerRowOne.setBorder(BorderFactory.createEmptyBorder(100, 0, 20,0));
		
		centerRowTwo.add(portNumberDisplay);
		
		center.add(centerRowOne);
		center.add(centerRowTwo);
		
		login.addActionListener(new Login());
		
		this.getContentPane().add(center, BorderLayout.CENTER);
		this.getContentPane().add(login, BorderLayout.SOUTH);
		
		this.setBounds(100,100,500,400);
		this.setTitle("Messaging Client");
		this.setResizable(false);
		this.setVisible(true);
		
		//Confirm the port number before creating connection 
		PortConnection port = new PortConnection(this);
		
		//Check if user wants new port number
		int choice = port.confirmPortNumber();
		
		if (choice == 1) {
			//Get the new port number and write it to file
			port.writeNewPortNumber();
		}
		
		//Get the port number
		portNumber = port.port;
		
		//Show port number on screen 
		portNumberDisplay.setText("Selected port number: " + String.valueOf(portNumber));
	}
	
	class Login implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			//Make sure that a name is entered
			if (userName.getText().isBlank()) {
				JOptionPane.showMessageDialog(LoginFrame.this, "Please enter a username.", "Invalid Username", JOptionPane.ERROR_MESSAGE);
				return;
			}
				
			
			name = userName.getText();
			//Go to MessageFrame
			new MessagingFrame(LoginFrame.this, portNumber, name);
			
		}
	}

	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent e) {
		//Make sure that if client exists, close connections before exiting application
		if (MessagingFrame.client != null)
			MessagingFrame.client.closeEverything();
		System.exit(0);
	}

	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}

}

