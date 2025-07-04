import java.io.*;
import java.net.*;

public class Main {
    public static void main(String[] args) {
        Socket socket = null;

        try {
            socket = new Socket("localhost", 1111);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            int i = 1;
            while (i <= 10) {
                out.println(i);
                System.out.println(i + " to " + socket);
                i++;
//a
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