/**
 *
 *  @author Basiak Patryk S30757
 *
 */

package zad1;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Set;

public class Server extends Thread {
    private int port;
    private String host;
    private Selector selector = null;
    private ServerSocketChannel socket;
    private ArrayList<String> log;
    private HashMap<String, ArrayList<String>> clientLog;
    private HashMap<SocketChannel, String> clients;

    public Server(String host, int port) {
        this.port = port;
        this.host = host;
        clients = new HashMap<>();
        log = new ArrayList<>();
        clientLog = new HashMap<>();
    }

    public void startServer() {
        try {
            socket = ServerSocketChannel.open();

            socket.bind(new InetSocketAddress(host, port));
            socket.configureBlocking(false);
            selector = Selector.open();

            socket.register(selector, SelectionKey.OP_ACCEPT);
            this.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopServer() {
        try {
            this.interrupt();
            selector.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getServerLog() {
        return String.join("\n", log);
    }

    public void run() {
        while (!this.isInterrupted()) {
            try {
                selector.select();

                if (this.isInterrupted()) {
                    break;
                }
                Set<SelectionKey> event = selector.selectedKeys();
                for (SelectionKey selectionKey : event) {
                    if (selectionKey.isAcceptable()) {
                        SocketChannel socketChannel = socket.accept();
                        socketChannel.configureBlocking(false);
                        socketChannel.register(selector, SelectionKey.OP_READ);
                    }
                    if (selectionKey.isReadable()) {
                        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
                        ByteBuffer messageBuffer = ByteBuffer.allocate(1024);
                        if (socketChannel.read(messageBuffer) == -1) {
                            selectionKey.cancel();
                            continue;
                        }
                        messageBuffer.flip();
                        String mess = StandardCharsets.UTF_8.decode(messageBuffer).toString();
                        socketChannel.write(ByteBuffer.wrap(respond(mess, socketChannel).getBytes(StandardCharsets.UTF_8)));
                        this.addLog(mess, clients.get(socketChannel));

                    }
                }
                event.clear();
            } catch (RuntimeException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
    private String respond(String message, SocketChannel socketChannel){
        if(message.startsWith("login")){
            clients.put(socketChannel,message.split(" +")[1]);
            clientLog.put(clients.get(socketChannel), new ArrayList<>());
            clientLog.get(clients.get(socketChannel)).add("logged in \n");
            return "logged in";
        }else if(message.equals("bye")){
            clientLog.get(clients.get(socketChannel)).add("logged out \n");
            return "logged out";
        }else if(message.equals("bye and log transfer")){
            clientLog.get(clients.get(socketChannel)).add("logged out \n");
            return getClientLog(clients.get(socketChannel));
        }else{
            clientLog.get(clients.get(socketChannel)).add("Request: " + message + "\n");
            String reply = Time.passed(message.split(" +")[0], message.split(" +")[1]);
            clientLog.get(clients.get(socketChannel)).add("Result: \n" + reply + "\n");
            return reply;
        }
    }
    private void addLog(String message, String client){
        String l = client;
        if (message.startsWith("login")){
            l += " logged in at ";
        }else if(message.equals("bye")){
            l += " logged out at ";
        }else if(message.equals("bye and log transfer")){
            l += " logged out at ";
        }else{
            l += " request at ";
        }

        l += new SimpleDateFormat("HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
        if(!message.equals("bye") && !message.startsWith("login") && !message.equals("bye and log transfer")){
            l += ": " + '"'+ message + '"';
        }
        log.add(l);
    }
    private String getClientLog(String Client){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n=== ").append(Client).append(" log start ===\n");
        for(String s: clientLog.get(Client)){
            stringBuilder.append(s);
        }
        stringBuilder.append("=== ").append(Client).append(" log end ===\n");
        return stringBuilder.toString();
    }
}
