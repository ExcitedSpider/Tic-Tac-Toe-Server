package Server;

import Parser.Statement.Statement;

import java.net.ServerSocket;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface AbstractAyncServer {
    void listen(int port) throws Exception;

    void listen(int port, Consumer<ServerSocket> callback) throws Exception;
    /**
     * Add an event listener to client connection
     */
    void onConnection(Consumer<ClientHandle> callback);

    void onDisconnection(Consumer<ClientHandle> callback);
    /**
     * Add an event listener to a new request
     */
    void onRequest(BiConsumer<Statement, ClientHandle> consumer);

    void onError(BiConsumer<Exception, ClientHandle> biConsumer);

    void halt();
}
