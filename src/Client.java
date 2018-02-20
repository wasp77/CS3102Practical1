import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
    private static final int PORT = 7777;
    private static String hostname;
    private static String filename;

    private static void getSettings() {
        System.out.println("Enter server hostname or IP:");
        Scanner userIn = new Scanner(System.in);
        hostname = userIn.nextLine();
        System.out.println("Enter file name:");
        filename = userIn.nextLine();
        userIn.close();
    }

    private static void getFile() {
        String path = "/var/tmp/" + filename;
        Socket connection = null;
        try {
            connection = new Socket(hostname, PORT);
        } catch (IOException e) {
            System.out.println("Could not connect to server");
        }
        if (connection != null) {
            try {
                PrintWriter sendFile = new PrintWriter(connection.getOutputStream(), true);
                sendFile.println(filename);
                sendFile.flush();
                DataInputStream in = new DataInputStream(connection.getInputStream());
                boolean exists = in.readBoolean();
                if (exists) {
                    long startTime = System.nanoTime();
                    OutputStream out = new FileOutputStream(path);
                    long fileSize = in.readLong();
                    byte[] buffer = new byte[1024];
                    int bytesIn;
                    while (fileSize > 0 && (bytesIn = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                        out.write(buffer, 0, bytesIn);
                        fileSize -= bytesIn;
                    }
                    long endTime = System.nanoTime();
                    long duration = (endTime - startTime) / 1000000;
                    System.out.println("file downloaded in " + duration + " milliseconds");
                    System.out.println("File stored in /var/tmp");
                    out.close();
                } else {
                    System.out.println("file named: " + filename + " not available");
                }

                in.close();
                sendFile.close();
            } catch (IOException e) {
                System.out.println("Error encountered with the input and output streams");
            }
        }
    }

    public static void main(String[] args) {
        getSettings();
        getFile();
    }
}
