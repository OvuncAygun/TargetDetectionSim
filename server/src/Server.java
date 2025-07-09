import java.io.*;
import java.net.*;

public class Server implements Runnable{
    private final static int xSize = 50;
    private final static int ySize = 50;
    private final Board board = new Board(xSize, ySize);
    private final GUI gui;

    public Server(GUI gui) {
        this.gui = gui;
        gui.initialize(1000, 1000, xSize, ySize);
    }

    @Override
    public void run() {
        ServerSocket server = null;
        try {
            server = new ServerSocket(1111);
            while (true) {
                Socket client = server.accept();

                ServerThread clientHandler = new ServerThread(client, board, gui);

                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}
