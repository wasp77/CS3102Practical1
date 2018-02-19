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
            e.printStackTrace();
        }
        return exists;
    }

    private void sendFile(String path, DataOutputStream dos) {
        try {
            File testFile = new File(path);
            byte[] fileContents = new byte[(int) testFile.length()];
            FileInputStream fis = new FileInputStream(testFile);
            BufferedInputStream bis = new BufferedInputStream(fis);

            DataInputStream dis = new DataInputStream(bis);
            dis.readFully(fileContents, 0, fileContents.length);

            dos.writeLong(fileContents.length);
            dos.write(fileContents, 0, fileContents.length);
            dos.flush();
            dos.close();

            System.out.println("file sent");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void run() {
        System.out.println("thread: " + Thread.currentThread().getId());
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String path = in.readLine();
            OutputStream out = connection.getOutputStream();
            DataOutputStream dos = new DataOutputStream(out);
            if (checkFile(path, dos)) {
                sendFile(path, dos);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return;
    }

}
