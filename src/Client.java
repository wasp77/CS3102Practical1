import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    private static final int PORT = 7777;
    private static String hostname;
    private static String filename;
    private static String path;
    private static int testNum = 1;
    private static BufferedWriter logFile;
    private static int testAvg = 0;

    private static void deleteFile() {
        File file = new File(path);
        if (file.delete()) {
            System.out.println(path + "deleted");
        } else {
            System.out.println("couldn't delete");
        }
    }
    private static void getSettings() {
        System.out.println("Enter server hostname or IP:");
        Scanner userIn = new Scanner(System.in);
        hostname = userIn.nextLine();
        System.out.println("Enter file name:");
        filename = userIn.nextLine();
        System.out.println("Enter number of tests (Default is 1):");
        testNum = userIn.nextInt();
        userIn.close();
    }

    private static void getFile() {
        path = "/var/tmp/" + filename;
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
                    logFile.append(String.valueOf(duration)).append(" ms\n");
                    testAvg += duration;
                    //System.out.println("file downloaded in " + duration + " milliseconds");
                    System.out.println("File stored at " + path);
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
        try {
            logFile = new BufferedWriter(new FileWriter(InetAddress.getLocalHost().getHostName() + "-" + filename + ".txt"));
        } catch (UnknownHostException e) {
            System.out.println("couldn't get the host name");
        } catch (IOException e) {
            System.out.println("Problems writing to the logfile");
        }
        for (int i = 0; i < testNum; i++) {
            getFile();
//            if (i + 1 != testNum) {
//                deleteFile();
//            }
        }


        try {
            testAvg /= testNum;
            logFile.append(String.valueOf(testAvg)).append(" ms (session average)\n");
            logFile.close();
        } catch (IOException e) {
            System.out.println("Error closing logfile");
        }
    }
}
