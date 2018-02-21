import java.io.*;
import java.net.Socket;

public class FileDistributor extends Thread{
    private Socket connection;


    public FileDistributor(Socket connection) {
        this.connection = connection;
    }

    private boolean checkFile(String path, DataOutputStream dos) {
        boolean exists = new File(path).exists();
        try {
            dos.writeBoolean(exists);
            dos.flush();
        } catch (IOException e) {
            System.out.println("Error occurred while passing boolean to client");
        }
        return exists;
    }

    private void sendFile(String path, DataOutputStream dos) {
        try {
            File testFile = new File(path);
            FileInputStream fis = new FileInputStream(testFile);
            BufferedInputStream bis = new BufferedInputStream(fis);
            DataInputStream dis = new DataInputStream(bis);
            byte[] fileContents = new byte[(int) (testFile.length()/2)];
            dos.writeLong(testFile.length());

            for (int i = 0; i < 2; i++) {
                dis.readFully(fileContents, 0, fileContents.length);
                dos.write(fileContents, 0, fileContents.length);
                dos.flush();
            }
            dos.close();
        } catch (FileNotFoundException e) {
            System.out.println("File was not found");
        } catch (IOException e) {
            System.out.println("Problem with the file transfer");
        }

    }

    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String path = in.readLine();
            OutputStream out = connection.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            if (checkFile(path, dos)) {
                while (!Server.sendFile.get()) {}
                sendFile(path, dos);
            }
        } catch (IOException e) {
            System.out.println("Error encountered with the input and output streams");
        }
    }

}
