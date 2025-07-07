import javax.lang.model.type.UnknownTypeException;
import java.io.*;
import java.net.*;

public class ServerThread implements Runnable {
    private final Socket clientSocket;
    private final Board board;

    public ServerThread(Socket clientSocket, Board board) {
        this.clientSocket = clientSocket;
        this.board = board;
    }

    @Override
    public void run() {
        Entity entity;
        try {
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            String type = inputStream.readUTF();
            entity = switch (type) {
                case "normalEnemy" -> new Enemy(inputStream, outputStream, board);
                default -> throw new UnknownTypeException(null, "Unknown type while creating object");
            };

            while (true) {
                String operation = inputStream.readUTF();
                switch (operation) {
                    case "MOVE":
                        entity.move();
                        break;
                    case "DISCOVER":
                        int size = inputStream.readInt();
                        entity.discover(size);
                    default:
                        break;
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
