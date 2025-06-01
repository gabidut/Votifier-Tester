package xyz.sirblobman.votifier.tester;

import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.logging.Logger;
import javax.swing.*;

public class VoteForm {
    Logger logger = Logger.getLogger(VoteForm.class.getName());

    private JFrame frame;

    private JPanel panel1;
    private JTextField textFieldHostName;
    private JTextField textFieldPort;
    private JTextField textFieldServiceName;
    private JTextField textFieldUsername;
    private JTextField textFieldAddress;
    private JTextField textFieldTimestamp;
    private JTextArea textAreaPublicKey;
    private JButton submitButton;
    private JCheckBox saveAfterClose;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                new VoteForm();
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "An error occurred while initializing the form: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }


    public VoteForm() {
        JFrame frame = new JFrame("VoteForm");
        frame.setContentPane(this.panel1);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                if(VoteForm.this.saveAfterClose.isSelected()) {
                    String hostName = textFieldHostName.getText();
                    String portString = textFieldPort.getText();
                    String publicKey = textAreaPublicKey.getText();
                    String serviceName = textFieldServiceName.getText();
                    String username = textFieldUsername.getText();
                    String address = textFieldAddress.getText();
                    String timestampString = textFieldTimestamp.getText();

                    int port;
                    try {
                        port = Integer.parseInt(portString);
                    } catch (NumberFormatException e) {
                        port = 8192; // Default port
                    }

                    long timestamp;
                    try {
                        timestamp = Long.parseLong(timestampString);
                    } catch (NumberFormatException e) {
                        timestamp = System.currentTimeMillis(); // Default to current time
                    }

                    VoteFormData data = new VoteFormData(hostName, port, publicKey, serviceName, username, address, timestamp, true);
                    data.save();
                }
            }
        });
        resetForm();
        submitButton.addActionListener(this::onSubmit);
    }
    
    private void print(String message) {
        System.out.println(message);
    }
    
    private void displayError(String message) {
        JOptionPane.showMessageDialog(null, message + " (Check console for more details)", "Error", JOptionPane.ERROR_MESSAGE);
    }
    
    private void resetForm() {
        if(new java.io.File("votifier_tester.yml").exists()) {
            try {
                String yamlContent = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("votifier_tester.yml")));
                VoteFormData data = VoteFormData.fromYaml(yamlContent);

                this.textFieldHostName.setText(data.getHostName());
                this.textFieldPort.setText(Integer.toString(data.getPort()));
                this.textAreaPublicKey.setText(data.getPublicKey());
                this.textFieldServiceName.setText(data.getServiceName());
                this.textFieldUsername.setText(data.getUsername());
                this.textFieldAddress.setText(data.getAddress());
                this.textFieldTimestamp.setText(Long.toString(data.getTimestamp()));
                this.saveAfterClose.setSelected(data.isSaveAfterClose());
            } catch (Exception e) {
                displayError("Failed to load votifier_tester.yml: " + e.getMessage());
                logger.severe("Failed to load votifier_tester.yml: " + e);
                this.loadDefaultValues();
            }
        } else {
            this.loadDefaultValues();
        }
    }

    private void loadDefaultValues() {
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

        logger.info("Submitting vote with the following details:");
        logger.info("  Host Name: " + hostname);
        logger.info("  Port: " + port);
        logger.info("  Public Key: " + publicKey);
        logger.info("  Service Name: " + serviceName);
        logger.info("  Username: " + username);
        logger.info("  Address: " + address);
        logger.info("  Timestamp: " + timestamp);
        
        Vote vote = new Vote(serviceName, username, address, timestamp);
        Server server = new Server(hostname, port, publicKey);
        
        try {
            this.submitButton.setEnabled(false);
            server.sendVote(vote);
        } catch(IOException ex) {
            logger.severe("Failed to send vote: " + ex.getMessage());
            displayError(ex.getMessage());
        }

        if(!this.saveAfterClose.isSelected()) {
            resetForm();
        }
        this.submitButton.setEnabled(true);
    }
    
}
