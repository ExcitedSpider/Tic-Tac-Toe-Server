package Server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandle {
    private final Socket socket;

    ClientHandle(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public String readLine() throws IOException {
        var stream = new DataInputStream(socket.getInputStream());
        return stream.readUTF();
    }

    public void writeString(String text) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(this.socket.getOutputStream());
        writer.write(text);
        writer.flush();
    }
}
