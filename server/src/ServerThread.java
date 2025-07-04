import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ServerThread implements Runnable {
    private final Socket clientSocket;

    public ServerThread(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        BufferedReader in;
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String message = "";
            while (message != null) {
                message = in.readLine();
                System.out.println(message + " from " + clientSocket);
                Thread.sleep(1000);
            }
        }
        catch (IOException e) {
            System.err.println(e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
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
