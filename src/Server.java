import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

public class Server {
    private final static int PORT = 7777;
    private static int connections = 0;
    public static AtomicBoolean sendFile = new AtomicBoolean();

    public static void main (String[] args) {
        Scanner userIn = new Scanner(System.in);
        System.out.println("Enter the number of connections required for the file to send:");
        int connectionLimit;
        try {
            connectionLimit = userIn.nextInt();
        } catch (java.util.InputMismatchException e) {
            System.out.println("Incorrect entry");
            return;
        }
        sendFile.set(false);
        try {
            ServerSocket server = new ServerSocket(PORT, 50, InetAddress.getLocalHost());
            System.out.println("Server accepting connections on: " + InetAddress.getLocalHost());
            while (true) {
                Socket clientConnect = server.accept();
                Thread handler = new FileDistributor(clientConnect);
                handler.start();
                connections += 1;
                if (connections == connectionLimit) {
                    System.out.println("connection limit reached sending files..");
                    sendFile.set(true);
                    connections = 0;
                    break;
                }
            }

        } catch (IOException e) {
            System.out.println("problem encountered with the server socket");
        }

    }
}
