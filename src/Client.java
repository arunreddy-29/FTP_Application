import java.net.*;
import java.io.*;
import java.util.Scanner;

public class Client {
    Socket requestSocket;           //socket connect to the server
    ObjectOutputStream out;         //stream write to the socket
    ObjectInputStream in;          //stream read from the socket
    String message;                //message send to the server

    public Client() {}

    void run() {
        try {

            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter the port number to connect to: ");
            int port = scanner.nextInt();
            //create a socket to connect to the server
            requestSocket = new Socket("localhost", port);
            System.out.println("Connected to localhost in port " + port);
            //initialize inputStream and outputStream
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(requestSocket.getInputStream());

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            boolean keepRunning = true;
            while (keepRunning) {
                System.out.print("Input get {file}, upload {file} or exit command: ");
                message = bufferedReader.readLine();
                String[] parts = message.split(" ", 2);
                String command = parts[0];

                switch (command.toLowerCase()) {
                    case "get" -> {
                        if (parts.length < 2) {
                            System.out.println("Please specify a file to get.");
                        } else {
                            String getFileName = parts[1];
                            System.out.println("Getting file: " + getFileName);
                            transmitMessage(message, out);
                            downloadFile(getFileName, in);
                            System.out.println("new" + getFileName + " downloaded.");
                        }
                    }
                    case "upload" -> {
                        if (parts.length < 2) {
                            System.out.println("Please specify a file to upload.");
                        } else {
                            String uploadFileName = parts[1];
                            System.out.println("Uploading file: " + uploadFileName);
                            transmitMessage(message, out);
                            uploadFile(uploadFileName, out);
                            System.out.println(uploadFileName + " uploaded.");
                        }
                    }
                    case "exit" -> {
                        System.out.println("Exiting...");
                        transmitMessage(message, out);
                        keepRunning = false;

                    }

                    // Handle the exit logic here, like closing resources or connections
                    default -> System.out.println("Invalid command. Please use 'get', 'upload', or 'exit'.");
                }
            }
        } catch (ConnectException e) {
            System.err.println("Connection refused. You need to initiate a server first.");
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            //Close connections
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    private void uploadFile(String fileName, ObjectOutputStream outputStream) throws IOException {
        String directoryPath = "client";
        File fileToUpload = new File(directoryPath, fileName);
        if (!fileToUpload.exists()) {
            outputStream.writeObject("File not found");
            outputStream.flush();
            return;
        }

        try (FileInputStream fileInputStream = new FileInputStream(fileToUpload)) {
            byte[] fileContentBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(fileContentBuffer)) != -1) {
                outputStream.writeInt(bytesRead);
                outputStream.write(fileContentBuffer, 0, bytesRead);
                outputStream.flush();
            }
            outputStream.writeInt(-1);
            outputStream.flush();
        }
    }

    private void downloadFile(String fileName, ObjectInputStream inputStream) throws IOException {
        String formattedFileName = formatFileName(fileName);
        File destinationFile = new File("client", formattedFileName);

        try (FileOutputStream fileOutputStream = new FileOutputStream(destinationFile)) {
            byte[] fileContentBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.readInt()) != -1) {
                if (bytesRead > 0) {
                    inputStream.readFully(fileContentBuffer, 0, bytesRead);
                    fileOutputStream.write(fileContentBuffer, 0, bytesRead);
                }
            }
            fileOutputStream.flush();
        }
    }

    private String formatFileName(String originalName) {
        return "new" + Character.toUpperCase(originalName.charAt(0)) + originalName.substring(1);
    }

    public void transmitMessage(String message, ObjectOutputStream outputStream) {
        try {
            outputStream.writeObject(message);
            outputStream.flush();
            System.out.println("Message transmitted: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //main method
    public static void main(String[] args) {
        new Client().run();
    }

}
