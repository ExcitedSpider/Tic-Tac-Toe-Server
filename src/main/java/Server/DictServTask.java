package Server;

import Logger.Logger;
import Model.Dictionary.DictionaryShelf;
import Model.Response.ResponseData;
import Model.Response.StatusCode;
import Model.Word.WordDefinition;
import Controller.OutputType;
import Controller.ResponseEncoder;
import Controller.RequestController;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class DictServTask implements Runnable {
    public static final String DefaultDictionaryName = "<default>";
    private final int port;
    private final Logger logger = Logger.getInstance();

    public DictionaryShelf getDictionaryShelf() {
        return dictionaryShelf;
    }

    public DictServTask(int port, DictionaryShelf shelf) {
        this.port = port;
        if(shelf != null && !shelf.hasDictionary(DefaultDictionaryName)) {
            shelf.createDictionary(DefaultDictionaryName);
        }
        this.dictionaryShelf = Objects.requireNonNullElseGet(shelf, () -> new DictionaryShelf(DefaultDictionaryName));
    }

    private final DictionaryShelf dictionaryShelf;

    @Override
    public void run() {
        try {
            AbstractAyncServer server = new AyncServer();
            var handler = new RequestController(this.dictionaryShelf);
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
