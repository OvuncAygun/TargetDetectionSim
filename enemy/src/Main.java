import java.io.*;
import java.net.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        Socket socket = null;

        try {
            socket = new Socket("localhost", 1111);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            int i = 0;
            while (true) {
                out.println(i);
                i++;
                System.out.println(socket);
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