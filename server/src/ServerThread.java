import java.io.*;
import java.net.*;

public class ServerThread implements Runnable {
    private final Socket clientSocket;
    private final Board board;
    private final GUI gui;
    private Entity entity = null;

    public ServerThread(Socket clientSocket, Board board, GUI gui) {
        this.clientSocket = clientSocket;
        this.board = board;
        this.gui = gui;
    }

    @Override
    public void run() {
        try {
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            outputStream.writeInt(board.xSize);
            outputStream.writeInt(board.ySize);
            String type = inputStream.readUTF();
            entity = switch (type) {
                case "enemy" -> new Enemy(inputStream, outputStream, board, gui);
                case "observer" -> new Observer(inputStream, outputStream, board, gui);
                default -> throw new IllegalArgumentException("Undefined type received for entity creation");
            };

            while (gui.running) {
                String operation = inputStream.readUTF();
                switch (operation) {
                    case "MOVE":
                        entity.move();
                        break;
                    case "DISCOVER":
                        entity.discover();
                        break;
                    case "SCAN":
                        entity.scan();
                        break;
                    case "MARK":
                        entity.mark();
                        break;
                    default:
                        throw new IllegalArgumentException("Undefined command received");
                }
            }
        }
        catch (IOException | IllegalArgumentException e) {
            if(e instanceof EOFException) {
                System.out.println("Client Disconnected.");
            }
            else {
                System.err.println(e.getMessage());
            }

        }
        finally {
            if(entity != null) {
                entity.remove();
            }
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
