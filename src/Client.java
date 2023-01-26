import javax.naming.ldap.SortKey;
import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {

    private Socket client;
    public BufferedReader bufferedReader;
    public BufferedWriter bufferedWriter;
    private String userNAme;

    public Client(Socket client, String userNAme) {
        try {
            this.client = client;
            this.bufferedReader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
            this.userNAme = userNAme;
        } catch (IOException o) {
            closeEveryThing(client, bufferedReader, bufferedWriter);
        }
    }

    public void sendMessage() {
        try {
            bufferedWriter.write(userNAme);
            bufferedWriter.newLine();
            bufferedWriter.flush();

            Scanner sc = new Scanner(System.in);
            while (client.isConnected()) {
                String message = sc.nextLine();
                bufferedWriter.write(userNAme + " : " + message);
                bufferedWriter.newLine();
                bufferedWriter.flush();
            }
        } catch (IOException o) {
            closeEveryThing(client, bufferedReader, bufferedWriter);
        }
    }

    public void listenForMessage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String message;
                while (client.isConnected()) {
                    try {
                        message = bufferedReader.readLine();
                        System.out.println(message);
                    } catch (IOException o) {
                        closeEveryThing(client, bufferedReader, bufferedWriter);
                    }
                }
            }
        }).start();
    }

    public void closeEveryThing(Socket client, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (client != null) {
                client.close();
            }
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);
        System.out.println("Enter your username for the group chat !");
        String username = s.nextLine();
        Socket socket = new Socket("127.0.0.1", 1999);
        Client client = new Client(socket, username);
        client.listenForMessage();
        client.sendMessage();
    }
}
