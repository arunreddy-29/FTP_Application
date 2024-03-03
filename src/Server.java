import java.net.*;
import java.io.*;

public class Server {
    private final int serverPort = 8000; // Use final for constants
    private ServerSocket serverSocket; // Renamed for clarity
    private Socket clientSocket = null; // Renamed for clarity and consistency
    private String receivedMessage; // Renamed for clarity
    private ObjectOutputStream outputStream; // Renamed for clarity
    private ObjectInputStream inputStream; // Renamed for clarity

    public Server() {
        // Consider initializing components here if necessary
    }

    public void run() {
        try {
            // Create a server socket
            serverSocket = new ServerSocket(serverPort, 10);
            // Wait for connection
            System.out.println("Waiting for client connections...");
            // Accept a connection from the client
            clientSocket = serverSocket.accept();
            System.out.println("Connection received from " + clientSocket.getInetAddress().getHostName());
            // Initialize Input and Output streams
            outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(clientSocket.getInputStream());

            while (true) {
                receivedMessage = (String) inputStream.readObject();
                System.out.println("Received message: " + receivedMessage);
                if ("exit".equalsIgnoreCase(receivedMessage)) {
                    System.out.println("Exit command received. Shutting down server...");
                    break;
                }

                processClientMessage(receivedMessage);
            }
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            closeConnection();
        }
    }

    private void processClientMessage(String message) throws IOException {
        if (message.startsWith("get ")) {
            String fileName = message.substring(4);
            sendFile(fileName, outputStream);
            System.out.println("File sent");
        } else if (message.startsWith("upload ")) {
            String fileName = message.substring(7);
            receiveFile(fileName, inputStream);
            System.out.println("File received");
        }
    }

    private void sendFile(String fileName, ObjectOutputStream out) throws IOException {
        String directoryPath = "server";
        File file = new File(directoryPath, fileName);
        if (!file.exists()) {
            out.writeObject("File not found");
            out.flush();
            return;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytes;
            while ((bytes = fis.read(buffer)) != -1) {
                out.writeInt(bytes);
                out.write(buffer, 0, bytes);
                out.flush();
            }
            out.writeInt(-1);
            out.flush();
        }
    }

    private void receiveFile(String fileName, ObjectInputStream in) throws IOException {
        String newFileName = "new" + Character.toUpperCase(fileName.charAt(0)) + fileName.substring(1);
        System.out.println("Receiving file as: " + newFileName);
        File newFile = new File("server", newFileName);

        try (FileOutputStream fos = new FileOutputStream(newFile)) {
            byte[] buffer = new byte[1024];
            int numBytes;
            while ((numBytes = in.readInt()) != -1) {
                if (numBytes > 0) {
                    in.readFully(buffer, 0, numBytes);
                    fos.write(buffer, 0, numBytes);
                }
            }
            fos.flush();
        }
    }

    private void closeConnection() {
        try {
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
            if (serverSocket != null) serverSocket.close();
        } catch (IOException ioException) {
            System.err.println("Error closing resources: " + ioException.getMessage());
        }
    }

    public static void main(String[] args) {
        new Server().run();
    }
}
