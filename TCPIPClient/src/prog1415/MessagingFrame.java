package prog1415;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

//PROG1415 Developing Mobile Applications
//Developing Java Client Server Applications
//Assignment 2
//Due: October 27th 2024
//Emily Little

//Create Client UI to send and receive messages
public class MessagingFrame extends JPanel {
	
	JFrame frame;
	
	//Panels
	JPanel messagingPanel = new JPanel(new BorderLayout());
	JPanel left = new JPanel(new BorderLayout());
    JPanel center = new JPanel(new BorderLayout());
    JPanel displayMessages = new JPanel(new BorderLayout());
    JPanel sendMessagesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    
    //Button
    JButton sendMessage = new JButton("Send");
    
    //Labels
    JLabel online = new JLabel("Online");
    JLabel chat = new JLabel("Messages");
    
    //Text blocks
    static JTextArea onlineClients = new JTextArea();
    static JTextArea chatBox = new JTextArea();
    
    //Text field to send messages
    static JTextField message = new JTextField();
    
    static Client client;
    
    //Combo Box
  	static JComboBox<String> chatOptions = new JComboBox<String>();
	
	public MessagingFrame(JFrame frame, int portNumber, String name) {
		//Create UI and assign events
		this.frame = frame;

		//Right Panel
		left.add(online, BorderLayout.NORTH);
		
		onlineClients.setEditable(false);
		onlineClients.setLineWrap(true);
		
		JScrollPane scrollPaneClient = new JScrollPane(onlineClients);
		scrollPaneClient.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		left.add(scrollPaneClient, BorderLayout.CENTER);
		
		displayMessages.add(chat, BorderLayout.NORTH);
		chatBox.setEditable(false);
		chatBox.setLineWrap(true);
		
		JScrollPane scrollPaneChat= new JScrollPane(chatBox);
		scrollPaneChat.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		displayMessages.add(scrollPaneChat, BorderLayout.CENTER);
		
		message.setPreferredSize(new Dimension(275, 31));
		
		sendMessage.addActionListener(new SendMessage());
		message.addActionListener(new SendMessage());
		
		sendMessagesPanel.add(sendMessage);
		sendMessagesPanel.add(message);
		sendMessagesPanel.add(chatOptions);
		
		sendMessagesPanel.setPreferredSize(new Dimension(375, 80));
		
		center.add(displayMessages, BorderLayout.CENTER);
		center.add(sendMessagesPanel, BorderLayout.SOUTH);
	
		messagingPanel.add(center, BorderLayout.CENTER);
		messagingPanel.add(left, BorderLayout.WEST);
		
		frame.setContentPane(messagingPanel);
		frame.setVisible(true);
		
		chatOptions.addItem("Chat All");
		
		//Create a new client once user name and port number is confirmed
		client = new Client(portNumber, name);
	}
	
	
	
	class SendMessage implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			//Send message to server
			//If Chat All in combo box is selected, then send message to all clients
			if(chatOptions.getSelectedItem() == "Chat All" || chatOptions.getSelectedItem() == null)
				client.sendMessage(message.getText(), "General", "");
			//If any other option selected in combo box, send message to selected user as a private message
			else {
				client.sendMessage(message.getText(), "Private", chatOptions.getSelectedItem().toString());
			}
		}
		
	}
}
