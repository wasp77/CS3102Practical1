import java.io.*;
import java.net.Socket;

public class Client {
    private static final int PORT = 7777;
    private static final String HOSTNAME = "pc2-030-l.cs.st-andrews.ac.uk";
    public static void main(String[] args) {
        try {
            String path = "/var/tmp/" + args[0];
            Socket connection = new Socket(HOSTNAME, PORT);
            PrintWriter sendFile = new PrintWriter(connection.getOutputStream(), true);
            sendFile.println(args[0]);
            sendFile.flush();
            DataInputStream in = new DataInputStream(connection.getInputStream());
            boolean exists = in.readBoolean();
            if (exists) {
                OutputStream out = new FileOutputStream(path);
                long fileSize = in.readLong();
                byte[] buffer = new byte[1024];
                int bytesIn;
                while (fileSize > 0 && (bytesIn = in.read(buffer, 0, (int) Math.min(buffer.length, fileSize))) != -1) {
                    out.write(buffer, 0, bytesIn);
                    fileSize -= bytesIn;
                }
                System.out.println("File stored in /var/tmp");
                out.close();
            } else {
                System.out.println(args[0] + " not available");
            }

            in.close();
            sendFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
