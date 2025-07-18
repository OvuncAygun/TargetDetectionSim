import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server implements Runnable{
    private final static int xSize = 25;
    private final static int ySize = 25;
    private final Board board = new Board(xSize, ySize);
    private final GUI gui;
    private ServerSocket server = null;
    private final ExecutorService executorService;

    public Server(GUI gui) {
        this.gui = gui;
        gui.initialize(1000, 1000, xSize, ySize);
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r);
            }
        };
        executorService = Executors.newThreadPerTaskExecutor(threadFactory);
    }

    @Override
    public void run() {
        try {
            server = new ServerSocket(1111);
            while (gui.running) {
                Socket client = server.accept();

                ServerThread clientHandler = new ServerThread(client, board, gui);

                executorService.submit(clientHandler);
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

    public void stop() throws IOException {
        server.close();
        executorService.shutdownNow();
    }
}
