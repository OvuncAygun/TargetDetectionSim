import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread implements Runnable {
    private Socket clientSocket;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            while (true) {
                System.out.println(clientSocket);
                System.out.println(in.readLine());
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        }
        finally {
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
