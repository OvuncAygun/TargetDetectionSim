import java.io.*;
import java.net.*;
import java.util.concurrent.*;

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

            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            Runnable move = new Runnable() {
                @Override
                public void run() {
                    try {
                        observer.move();
                    }
                    catch (IOException e) {
                        System.err.println(e.getMessage());
                        scheduler.shutdown();
                    }
                }
            };

            Runnable scan = new Runnable() {
                @Override
                public void run() {
                    try {
                        observer.scan();
                    }
                    catch (IOException e) {
                        System.err.println(e.getMessage());
                        scheduler.shutdown();
                    }
                }
            };

            scheduler.scheduleAtFixedRate(move, 0, 1000, TimeUnit.MILLISECONDS);
            scheduler.scheduleAtFixedRate(scan, 0, 100, TimeUnit.MILLISECONDS);

            boolean terminated = false;
            while (!terminated) {
                terminated = scheduler.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
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