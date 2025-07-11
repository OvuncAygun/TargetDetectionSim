import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        Socket socket = null;
        Observer observer;

        try {
            socket = new Socket("localhost", 1111);
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
            int xSize = inputStream.readInt();
            int ySize = inputStream.readInt();
            Board board = new Board(xSize, ySize);
            observer = new NormalObserver(inputStream, outputStream, board);

            int i = 0;
            while (true) {
                observer.move();
                observer.scan();
                i++;
                Thread.sleep(100);
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