import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleClientClass {
    private BufferedReader br;
    private PrintWriter pw;
    private final Scanner consoleScanner = new Scanner(System.in,StandardCharsets.UTF_8);
    private String nickname;


    //Connecting to server
    private void connectToServer() {
        InetSocketAddress serverAddress = new InetSocketAddress("localhost", 33333);
        try  {
            SocketChannel server = SocketChannel.open(serverAddress);
            br = new BufferedReader(Channels.newReader(server, StandardCharsets.UTF_8));
            pw = new PrintWriter(Channels.newWriter(server, StandardCharsets.UTF_8));
            System.out.println("Connected to server.");
        } catch (Exception e) {
            System.out.println("Error when connecting to server: " + e);
        }
    }

    private void writeToServer() {
        try {
            while (true) {
                String message = consoleScanner.nextLine();
                pw.println(message);
                pw.flush();
            }
        } catch (Exception e) {
            System.out.println("Error when writing to server: " + e);
        }
    }

    private void sendNickToServer() {
        pw.println(nickname);
        pw.flush();
    }

    //Listening to Server and Sending Nickname
    private void readFromServer() {
        String message;
        try {
            //Receive Message From Server
            //If 'NICK' Send Nickname
            while ((message = br.readLine()) != null) {
                if (message.equals("NICK")){
                    sendNickToServer();
                    continue;
                }
                System.out.println(message);
            }
        } catch (IOException e) {
            System.out.println("Error when reading from server: " + e);
        }
    }

    private void operate() {
        //Choosing nickname
        System.out.println("Enter your nickname:");
        nickname = consoleScanner.nextLine();
        connectToServer();
        ExecutorService threadPool = Executors.newFixedThreadPool(2);
        //Starting Threads For Listening And Writing
        threadPool.submit(()->readFromServer());
        threadPool.submit(()->writeToServer());
    }

    public static void main(String[] args) {

        new SimpleClientClass().operate();
    }
}
