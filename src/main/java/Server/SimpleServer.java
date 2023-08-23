package Server;

import Logger.Logger;
import Model.Dictionary.DictionaryShelf;
import Model.Response.ResponseData;
import Model.Response.StatusCode;
import Model.Word.WordDefinition;
import RequestHandler.OutputType;
import RequestHandler.ResponseEncoder;
import RequestHandler.StatementHandler;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class SimpleServer implements Runnable {
    private final int port;
    private final Logger logger = Logger.getInstance();

    public DictionaryShelf getDictionaryShelf() {
        return dictionaryShelf;
    }

    public SimpleServer(int port, DictionaryShelf shelf) {
        this.port = port;
        this.dictionaryShelf = Objects.requireNonNullElseGet(shelf, () -> new DictionaryShelf("<default>"));
    }

    private final DictionaryShelf dictionaryShelf;

    @Override
    public void run() {
        try {
            AyncServer server = new AyncServer();
            var handler = new StatementHandler(this.dictionaryShelf);
            server.onRequest((statement, client) -> {
                try {
                    String response = handler.resolve(statement);
                    client.writeString(response);
                    logger.logInfo("Sent: \n" + response.stripTrailing());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });

            server.onError((exception, clientHandle) -> {
                try {
                    ResponseEncoder encoder = new ResponseEncoder(OutputType.TEXT);
                    var response = new ResponseData<List<WordDefinition>>(StatusCode.ServerError, null, null, null);
                    clientHandle.writeString(encoder.encode(response));
                    clientHandle.getSocket().close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    logger.logErr(exception);
                }
            });

            server.listen(port);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
