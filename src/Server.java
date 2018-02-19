import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private final static int PORT = 7777;
    public static void main (String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT);
            while (true) {
                Socket clientConnect = server.accept();
                Thread handler = new FileDistributor(clientConnect);
                handler.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
