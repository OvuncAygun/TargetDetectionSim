import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        Socket socket = null;
        Enemy enemy;
        Board board = new Board(50, 50);

        try {
            socket = new Socket("localhost", 1111);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            enemy = new NormalEnemy(inputStream, outputStream, board);

            for(int i = 0; i < 100; i++){
                enemy.move();
            }

        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                }
                catch (IOException e) {
                    System.err.println(e.getMessage());
                }
            }
        }
    }
}