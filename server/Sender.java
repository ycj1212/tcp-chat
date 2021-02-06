import java.io.PrintWriter;
import java.util.Scanner;

class Sender extends Thread {
    private PrintWriter pw;
    private Scanner sc;

    public Sender(PrintWriter pw, Scanner sc) {
        this.pw = pw;
        this.sc = sc;
    }

    @Override
    public void run() {
        super.run();

        while (true) {
            String line = sc.nextLine();

            pw.println(line);
            pw.flush();
        }
    }
}