import javax.lang.model.type.UnknownTypeException;
import java.io.*;
import java.net.*;

public class ServerThread implements Runnable {
    private final Socket clientSocket;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        DataInputStream inputStream;
        Entity entity;
        try {
            inputStream = new DataInputStream(clientSocket.getInputStream());
            String type = inputStream.readUTF();
            entity = switch (type) {
                case "enemy1" -> new Enemy(inputStream);
                default -> throw new UnknownTypeException(null, "Unknown type while creating object");
            };

            while (true) {
                String operation = inputStream.readUTF();
                switch (operation) {
                    case "MOVE":
                        entity.move();

                }
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                }
                catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
