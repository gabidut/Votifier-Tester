package xyz.sirblobman.votifier.tester;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Locale;
import javax.crypto.Cipher;

public final record Server(String hostName, int port, String publicKey) {
    public void sendVote(Vote vote) throws IOException {
        Socket socket = new Socket(hostName(), port());
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
    
        byte[] data = encryptVote(vote);
        if(data == null) {
            throw new IOException("Invalid Vote Data!");
        }
        
        String line;
        while((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
            if(line.toLowerCase(Locale.US).contains("votifier")) {
                OutputStream outputStream = socket.getOutputStream();
                outputStream.write(data);
                outputStream.flush();
                
                outputStream.close();
                break;
            }
        }
        
        inputStream.close();
        socket.close();
    }
    
    private byte[] encryptVote(Vote vote) {
        try {
            PublicKey publicKey = getPublicKey();
            Cipher encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
    
            String voteData = "VOTE" + '\n' +
                    vote.serviceName() + '\n' +
                    vote.username() + '\n' +
                    vote.address() + '\n' +
                    vote.timestamp();
            
            byte[] voteDataBytes = voteData.getBytes(StandardCharsets.UTF_8);
            return encryptCipher.doFinal(voteDataBytes);
        } catch(GeneralSecurityException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    private PublicKey getPublicKey() {
        try {
            String publicKeyString = publicKey();
            byte[] publicKeyBytes = publicKeyString.getBytes(StandardCharsets.UTF_8);
    
            Decoder decoder = Base64.getDecoder();
            byte[] publicKeyDecode = decoder.decode(publicKeyBytes);
    
            X509EncodedKeySpec publicKeyX509 = new X509EncodedKeySpec(publicKeyDecode);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(publicKeyX509);
        } catch(GeneralSecurityException ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
