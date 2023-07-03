import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.nio.channels.Channels;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SimpleServerClass {
    private List<PrintWriter> clientWriters = new ArrayList<>();
    private List<String> nicknames = new ArrayList<>();

    private void sendAll(String message) {
        for (PrintWriter writer : clientWriters) {
            writer.println(message);
            writer.flush();
        }
    }

    private void clientHandler(SocketChannel client) {
        try {
            PrintWriter clientWriter = new PrintWriter(Channels.newWriter(client, StandardCharsets.UTF_8));
            clientWriters.add(clientWriter);
            BufferedReader clientReader = new BufferedReader(Channels.newReader(client, StandardCharsets.UTF_8));
            //Request and store nickname
            clientWriter.println("NICK");
            clientWriter.flush();
            String clientNick = clientReader.readLine();
            System.out.println("Nick is: " + clientNick);
            sendAll(clientNick + " joined!");

            String clientMessage;
            while ((clientMessage = clientReader.readLine()) != null) {
                System.out.println(clientNick + ": " + clientMessage);
                sendAll(clientNick + ": " + clientMessage);
            }
        } catch (Exception e) {
            System.out.println("Error while handling a client: " + e);
            try{
                client.close();
            }catch (Exception ex){
                System.out.println("Error while closing a client connection: "+ex);
            }
        }
    }

    public void operate() {
        ExecutorService threadPool = Executors.newCachedThreadPool();
        try {
            InetSocketAddress serverAddress = new InetSocketAddress("localhost",33333);
            ServerSocketChannel server = ServerSocketChannel.open();
            server.bind(serverAddress);
            while (server.isOpen()) {
                //Accept connection
                SocketChannel client = server.accept();
                System.out.println("Connected with " + client.getRemoteAddress());

                threadPool.submit(() -> clientHandler(client));
            }
        } catch (IOException e) {
            System.out.println("Error while operating: " + e);
        }
    }

    public static void main(String[] args) {
        new SimpleServerClass().operate();
    }
}
