import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {
    private final static int PORT = 7777;
    public static void main (String[] args) {
        try {
            ServerSocket server = new ServerSocket(PORT, 10, InetAddress.getLocalHost());
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
