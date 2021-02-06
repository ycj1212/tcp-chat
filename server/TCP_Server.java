import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class TCP_Server {
    private static final int PORT_NUMBER = 8888;
    public static ArrayList<PrintWriter> pwList;

    public static void main(String[] args) {
        pwList = new ArrayList<>();

        try {
            ServerSocket ss = new ServerSocket(PORT_NUMBER);

            while (true) {
                Socket s = ss.accept();

                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
                String nickname = br.readLine();

                System.out.println(nickname + "님 입장!");

                PrintWriter pw = new PrintWriter(s.getOutputStream());
                pwList.add(pw);

                for (int i=0; i<TCP_Server.pwList.size(); i++) {
                    pwList.get(i).println(nickname + "님 입장!");
                    pwList.get(i).flush();
                }

                ClientThread client = new ClientThread(s);
                client.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientThread extends Thread {
    private Socket s;
    private Sender sender;
    private Receiver receiver;

    public ClientThread(Socket s) {
        this.s = s;
    }

    @Override
    public void run() {
        super.run();

        try {
            sender = new Sender(new PrintWriter(s.getOutputStream()), new Scanner(System.in));
            receiver = new Receiver(new BufferedReader(new InputStreamReader(s.getInputStream())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        sender.start();
        receiver.start();
    }
}