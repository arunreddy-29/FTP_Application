
# File Transfer Application

This application enables file transfers between a client and a server over sockets in Java. The server waits for connections from clients, accepts file upload/download requests, and processes them accordingly. The client can connect to the server, request to download or upload files, and terminate the connection.

## Server

The server listens on port 8000 for client connections. It can handle requests to send a file to the client (download) or receive a file from the client (upload). When a file request is received, the server looks for the file in the "server" directory. Similarly, received files are saved in the same directory with "new" prefixed to the original filename.

### Running the Server

To start the server, compile and run the `Server.java` file. Ensure the server directory exists in the same location as your `Server.java` file.

```bash
javac Server.java
java Server
```

## Client

The client connects to the server using the server's IP address (localhost in this case) and port number (8000). Once connected, the client can send commands to upload or download files. Files to be uploaded should be placed in the "client" directory. Downloaded files are saved in the same directory with "new" prefixed to the original filename.

### Running the Client

Compile and run the `Client.java` file. Ensure the client directory exists in the same location as your `Client.java` file.

```bash
javac Client.java
java Client
```

## Supported Commands

- `get {filename}`: Downloads the specified file from the server.
- `upload {filename}`: Uploads the specified file to the server.
- `exit`: Closes the connection and exits the application.
