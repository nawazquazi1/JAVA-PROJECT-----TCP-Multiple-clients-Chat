import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server1 implements Runnable {
    private ServerSocket serverSocket;
    private ArrayList<ClinentHandler> list;
    private boolean done;
    private ExecutorService poll;

    Server1() {
        list = new ArrayList<>();
        done = false;
    }

    @Override
    public void run() {

        try {
            serverSocket = new ServerSocket(5555);
            poll = Executors.newCachedThreadPool();
            while (!done) {
                Socket client = serverSocket.accept();
                ClinentHandler clinentHandler = new ClinentHandler(client);
                list.add(clinentHandler);
                poll.execute(clinentHandler);
            }
        } catch (IOException i) {
            shutdown();
        }
    }

    public void shutdown() {
        try {
            done = true;
            poll.shutdown();
            if (!serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (ClinentHandler ch : list) {
                ch.shutdown();
            }
        } catch (IOException o) {
            o.printStackTrace();
        }
    }

    public void broadCast(String message) {
        for (ClinentHandler ch : list) {
            if (ch != null) {
                ch.sendMessage(message);
            }
        }
    }

    public class ClinentHandler implements Runnable {

        private Socket socket;
        private BufferedReader bufferedReader;
        private PrintWriter bufferedWriter;
        private String name;

        public ClinentHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                 bufferedWriter= new PrintWriter(socket.getOutputStream(), true);
                bufferedWriter.println("please enter  nickname");
                name = bufferedReader.readLine();
                System.out.println(name + " has connected!");
                broadCast(name + " joined the chat !");
                String message;
                while ((message = bufferedReader.readLine()) != null) {
                    if (message.startsWith("/nick")) {
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            broadCast(name + " renamed themselves to " + messageSplit[1]);
                            System.out.println(name + " renamed themselves to " + messageSplit[1]);
                            name = messageSplit[1];
                            bufferedWriter.println("SuccessFully Changed nickname to " + name);
                        } else {
                            bufferedWriter.println("No Nickname provided");
                        }
                    } else if (message.startsWith("/quit")) {
                        broadCast(name + " left the chat!");
                        shutdown();
                    } else {
                        broadCast(name + " : " + message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        public void sendMessage(String message) {
                bufferedWriter.println(message);
        }

        public void shutdown() {
            try {
                bufferedReader.close();
                bufferedWriter.close();
                if (!socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        Server1 server1 = new Server1();
        System.out.println("server is starting");
        server1.run();
    }

}
