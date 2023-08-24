package Server;

import Logger.Logger;

import java.io.*;
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
        try {
            var stream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            return stream.readUTF();
        } catch (EOFException exception) {
            Logger.getInstance().logInfo("Socket input stream closed");
            return null;
        }
    }

    public void writeString(String text) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(this.socket.getOutputStream());
        writer.write(text);
        writer.flush();
    }
}
