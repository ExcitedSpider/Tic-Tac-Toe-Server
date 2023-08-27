/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

package Server;

import Controller.OutputType;
import Controller.ResponseEncoder;
import Logger.Logger;
import Model.Response.ResponseData;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

public class ClientHandle {
    private final Socket socket;

    ClientHandle(Socket socket) {
        this.socket = socket;
    }

    public Socket getSocket() {
        return socket;
    }

    public String readRequest() throws IOException {
        try {
            var stream =  new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            var c = stream.readLine();
            while (c!= null && !c.equals(".")) {
                System.out.println(c);
                sb.append(c).append("\n");
                c = stream.readLine();
            }
            return preprocessInput(sb.toString());
        } catch (EOFException exception) {
            Logger.getInstance().logInfo("Socket input stream closed");
            return null;
        }
    }

    private String preprocessInput(String queryInput) {
        String charsToRemove = "\t\n.";
        String output = queryInput;
        for (char c : charsToRemove.toCharArray()) {
            output = output.replace(String.valueOf(c), "");
        }

        return output;
    }

    public void writeResponse(ResponseData data, OutputType outputType) throws IOException {
        this.writeString(new ResponseEncoder(outputType).encode(data));
    }

    public void writeResponse(ResponseData data) throws IOException {
        this.writeResponse(data, OutputType.TEXT);
    }

    public void writeString(String text) throws IOException {
        OutputStreamWriter writer = new OutputStreamWriter(this.socket.getOutputStream(), StandardCharsets.UTF_8);
        writer.write(text);
        writer.flush();
    }
}
