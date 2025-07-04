import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        Board board = new Board(100, 100);

        ServerSocket server = null;
        try {
            server = new ServerSocket(1111);
            while (true) {
                Socket client = server.accept();

                ServerThread clientHandler = new ServerThread(client);

                new Thread(clientHandler).start();
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        finally {
            if (server != null) {
                try {
                    server.close();
                }
                catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }

    }
}