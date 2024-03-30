// IN2011 Computer Networks
// Coursework 2023/2024
//
// Submission by
// YOUR_NAME_GOES_HERE
// YOUR_STUDENT_ID_NUMBER_GOES_HERE
// YOUR_EMAIL_GOES_HERE


import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// DO NOT EDIT starts
interface TemporaryNodeInterface {
    public boolean start(String startingNodeName, String startingNodeAddress);
    public boolean store(String key, String value);
    public String get(String key);
}
// DO NOT EDIT ends
public class TemporaryNode implements TemporaryNodeInterface {

    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    private boolean startCommunication(String nodeName) throws IOException {
        sendMessage(String.format("START 1 %s\n", nodeName));
        String serverResponse = receiveMessage();
        // Check if the server sent a valid START response
        return serverResponse.startsWith("START");
    }

    private void endCommunication() throws IOException {
        sendMessage("END Communication ended by TemporaryNode\n");
    }

    private void sendMessage(String message) throws IOException {
        out.write(message);
        out.flush();
    }

    private String receiveMessage() throws IOException {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = in.readLine()) != null) {
            response.append(line).append("\n");
            if (line.isEmpty()) {
                break;
            }
        }
        return response.toString();
    }

    @Override
    public boolean start(String startingNodeName, String startingNodeAddress) {
        try {
            String[] address = startingNodeAddress.split(":");
            socket = new Socket(address[0], Integer.parseInt(address[1]));
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));

            return startCommunication(startingNodeName);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean store(String key, String value) {
        try {
            String hashID = getHashID(key);
            sendMessage(String.format("PUT? 1 1\n%s\n%s\n", key, value));
            String serverResponse = receiveMessage();
            // Check if the server sent a SUCCESS response
            return serverResponse.startsWith("SUCCESS");
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String get(String key) {
        try {
            sendMessage(String.format("GET? 1\n%s\n", key));
            String serverResponse = receiveMessage();
            // Check if the server sent a VALUE response
            Pattern pattern = Pattern.compile("^VALUE (\\d+)$", Pattern.MULTILINE);
            Matcher matcher = pattern.matcher(serverResponse);
            if (matcher.find()) {
                return serverResponse.substring(matcher.end()).trim();
            } else {
                return null; // No value found
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                endCommunication();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String getHashID(String data) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}