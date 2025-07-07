import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        Socket socket = null;
        Enemy enemy;

        try {
            socket = new Socket("localhost", 1111);
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            enemy = new Enemy1(0, 0, outputStream);

            while (true) {
                enemy.move();
                Thread.sleep(1000);
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
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