import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Handler;

public class Client1 implements Runnable {
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    public static boolean done;

    @Override
    public void run() {
        try {
            client = new Socket("127.0.0.1", 9999);
            out =new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            InputHandler handler = new InputHandler();
            Thread thread = new Thread(handler);
            thread.start();
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println(message);
            }
        } catch (IOException e) {
            shutdown();
        }
    }

    public void shutdown() {
        done = true;
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
        } catch (IOException i) {

        }
    }

    public class InputHandler implements Runnable {
        @Override
        public void run() {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                while (!done) {
                    String message = reader.readLine();
                    if (message.equals("/quit")) {
                        out.println(message);
                        reader.close();
                        shutdown();
                    } else {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        Client1 client=new Client1();
        client.run();
    }

}
