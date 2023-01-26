import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class ClinentHandler implements Runnable {

    private static ArrayList<ClinentHandler> clinentHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader;
    private BufferedWriter bufferedWriter;
    private String name;

    public ClinentHandler(Socket socket) {
        try {
            this.socket = socket;
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.name = bufferedReader.readLine();
            clinentHandlers.add(this);
            broadCast("server : " + name + " has entered the chat");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    @Override
    public void run() {
        String messageFromClient;
        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine();
                broadCast(messageFromClient);
            } catch (IOException o) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close();
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (!socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void broadCast(String message) {
        try {
            for (ClinentHandler ch : clinentHandlers) {
                if (ch != null) {
                    ch.bufferedWriter.write(message);
                    ch.bufferedWriter.newLine();
                    ch.bufferedWriter.flush();
                }
            }
        } catch (Exception o) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }

    public void removeClientHandler() {
        clinentHandlers.remove(this);
        broadCast("SERVER : " + name + " has left the chat !");
    }
}