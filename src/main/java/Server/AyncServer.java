package Server;

import Logger.Logger;
import Model.Response.ResponseData;
import Model.Response.StatusCode;
import Parser.Parser;
import Parser.Statement.DisconnectStatement;
import Parser.Statement.Statement;
import Parser.SyntaxError.SyntaxError;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class AyncServer {
    ServerSocket serverSocket = null;
    boolean isHalt = false;
    private final Logger logger = Logger.getInstance();

    private final List<Consumer<ClientHandle>> connectionCallbacks = new ArrayList<>();
    private final List<BiConsumer<Statement, ClientHandle>> requestCallback = new ArrayList<>();
    private final List<BiConsumer<Exception, ClientHandle>> errorCallbacks = new ArrayList<>();
    private final List<Consumer<ClientHandle>> disconnectionCallbacks = new ArrayList<>();
    private MyThreadPool executor = new MyThreadPool(2);

    AtomicLong currentClients = new AtomicLong(0);

    public void listen(int port) throws Exception {
        this.listen(port, (serverSocket) -> {
            logger.logInfo("Server is running on " + serverSocket.getLocalPort());
        });
    }

    public void listen(int port, Consumer<ServerSocket> callback) throws Exception {
        if (this.serverSocket != null && !this.serverSocket.isClosed()) {
            throw new IOException("Server is already running");
        }
        this.serverSocket = new ServerSocket(port);
        callback.accept(this.serverSocket);
        eventLoop();
    }

    public void onConnection(Consumer<ClientHandle> callback) {
        this.connectionCallbacks.add(callback);
    }

    public void onDisconnection(Consumer<ClientHandle> callback) {
        this.disconnectionCallbacks.add(callback);
    }


    public void onRequest(BiConsumer<Statement, ClientHandle> consumer) {
        this.requestCallback.add(consumer);
    }

    public void onError(BiConsumer<Exception, ClientHandle> biConsumer) {
        this.errorCallbacks.add(biConsumer);
    }

    public void halt() {
        this.isHalt = true;
    }

    private void eventLoop() throws Exception {
        while (!this.isHalt) {
            Socket clientSocket = serverSocket.accept();
            ClientHandle client = new ClientHandle(clientSocket);

            Runnable clientTask = () -> {
                try {
                    this.connectionCallbacks.forEach(w -> w.accept(client));
                    var numOfClients =currentClients.addAndGet(1);
                    logger.logInfo("Connected 1 client " + clientSocket.getInetAddress() + " " + "Current Clients:" + numOfClients);
                    do {
                        var line = client.readRequest();
                        if(line == null || line.isEmpty()){
                            break;
                        }
                        var parser = new Parser(line);
                        try {
                            var statement = parser.parseOneLine();
                            logger.logInfo("Receive: "+statement);
                            if (statement instanceof DisconnectStatement){
                                clientSocket.close();
                            }
                            this.requestCallback.forEach(w -> w.accept(statement, client));
                        } catch (SyntaxError error) {
                            String errorMsg = "Parse Error" + ": " + error.getMessage() + ". " + "Raw Input: " + line;
                            logger.logErr(errorMsg);
                            logger.logErr(error);
                            client.writeResponse(new ResponseData<String>(StatusCode.BadRequest, null, null, errorMsg)); ;
                        }
                    } while (!clientSocket.isClosed() || !Thread.currentThread().isInterrupted());
                } catch (Exception exception) {
                    try {
                        this.errorCallbacks.forEach(w -> w.accept(exception, client));
                        clientSocket.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } finally {
                    var numOfClients = currentClients.addAndGet(-1);
                    this.disconnectionCallbacks.forEach(w -> w.accept(client));
                    logger.logInfo("Disconnect 1 client " + clientSocket.getInetAddress()+ " " + "Current Clients:" + numOfClients);
                }
            };
            this.executor.execute(clientTask);
        }
    }

}
