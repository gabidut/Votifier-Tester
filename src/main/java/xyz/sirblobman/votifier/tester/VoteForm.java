package xyz.sirblobman.votifier.tester;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class VoteForm {
    public static void main(String[] args) {
        JFrame frame = new JFrame("VoteForm");
        frame.setContentPane(new VoteForm().panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
    
    private JPanel panel1;
    private JTextField textFieldHostName;
    private JTextField textFieldPort;
    private JTextField textFieldServiceName;
    private JTextField textFieldUsername;
    private JTextField textFieldAddress;
    private JTextField textFieldTimestamp;
    private JTextArea textAreaPublicKey;
    private JButton submitButton;
    
    public VoteForm() {
        resetForm();
        submitButton.addActionListener(this::onSubmit);
    }
    
    private void print(String message) {
        System.out.println(message);
    }
    
    private void displayError(String message) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void resetForm() {
        this.textFieldHostName.setText("");
        this.textFieldPort.setText("8192");
        this.textAreaPublicKey.setText("");
    
        this.textFieldServiceName.setText("SirBlobman_Votifier_Testing");
        this.textFieldUsername.setText("Steve");
        this.textFieldAddress.setText("127.0.0.1");
        this.textFieldTimestamp.setText(Long.toString(System.currentTimeMillis()));
    }
    
    private void onSubmit(ActionEvent e) {
        print("Submit Button Action: " + e.getActionCommand());
    
        String hostname = textFieldHostName.getText();
        if(hostname == null || hostname.isBlank()) {
            displayError("Host name must not be empty!");
            return;
        }
    
        String portString = textFieldPort.getText();
        int port;
    
        try {
            port = Integer.parseInt(portString);
            if(port < 1 || port > 65565) {
                throw new NumberFormatException("Not in range!");
            }
        } catch(NumberFormatException ex) {
            displayError("Port must be a valid number between 1 and 65565.");
            return;
        }
        
        String publicKey = textAreaPublicKey.getText();
        if(publicKey == null || publicKey.isBlank()) {
            displayError("Public Key must not be empty!");
            return;
        }
        
        String serviceName = textFieldServiceName.getText();
        if(serviceName == null || serviceName.isBlank()) {
            displayError("Service Name must not be empty!");
            return;
        }
        
        String username = textFieldUsername.getText();
        if(username == null || username.isBlank()) {
            displayError("Username must not be empty!");
            return;
        }
        
        String address = textFieldAddress.getText();
        if(address == null || address.isBlank()) {
            displayError("Address must not be empty!");
            return;
        }
        
        String timestampString = textFieldTimestamp.getText();
        long timestamp;
        
        try {
            timestamp = Long.parseLong(timestampString);
        } catch(NumberFormatException ex) {
            displayError("Timestamp must be a valid millisecond time stamp.");
            return;
        }
    
        print("Server Information:");
        print("  Host Name: " + hostname);
        print("  Port: " + port);
        print("  Public Key: " + publicKey);
    
        print("Vote Information:");
        print("  Service Name: " + serviceName);
        print("  Username: " + username);
        print("  Address: " + address);
        print("  Timestamp: " + timestamp);
    
        print("");
        
        Vote vote = new Vote(serviceName, username, address, timestamp);
        Server server = new Server(hostname, port, publicKey);
        
        try {
            this.submitButton.setEnabled(false);
            server.sendVote(vote);
        } catch(IOException ex) {
            ex.printStackTrace();
            displayError(ex.getMessage());
        }
        
        resetForm();
        this.submitButton.setEnabled(true);
    }
    
}
