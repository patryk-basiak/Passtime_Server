/**
 *
 *  @author Basiak Patryk S30757
 *
 */

package zad1;




import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

public class Client {
    private SocketChannel clientSocket;
    private String host;
    private int port;
    private String name;

    public Client(String host, int port, String id) {
        this.host = host;
        this.port = port;
        this.name = id;

    }

    public String getName() {
        return name;
    }

    public void connect() {
        try {
            clientSocket = SocketChannel.open();
            clientSocket.connect(new InetSocketAddress(host, port));
            clientSocket.configureBlocking(false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String send(String s) {
        try {
            ByteBuffer messageBuffer = ByteBuffer.allocate(1024);
            clientSocket.write(ByteBuffer.wrap(s.getBytes(StandardCharsets.UTF_8)));
            while(true)
                if(clientSocket.read(messageBuffer) != 0) break;
            messageBuffer.flip();
            return StandardCharsets.UTF_8.decode(messageBuffer).toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
