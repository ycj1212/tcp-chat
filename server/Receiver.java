import java.io.BufferedReader;
import java.io.IOException;

class Receiver extends Thread {
    private BufferedReader br;

    public Receiver(BufferedReader br) {
        this.br = br;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            String msg = null;

            try {
                msg = br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (msg != null) {
                System.out.println(msg);

                for (int i=0; i<TCP_Server.pwList.size(); i++) {
                    TCP_Server.pwList.get(i).println(msg);
                    TCP_Server.pwList.get(i).flush();
                }
            }
        }
    }
}