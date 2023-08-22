import Logger.Logger;
import Model.Dictionary.DictionaryShelf;
import Server.SimpleServer;
import Server.Storage;

import java.io.IOException;
import java.util.Optional;

public class Main {

    public static void main(String[] args) {
        var storage = new Storage("dictionary");
        Logger logger = Logger.getInstance();
        var loadedShelf = storage.loadStorage();
        SimpleServer sever = new SimpleServer(8888, loadedShelf.orElseGet(() -> new DictionaryShelf("<default>")));
        var serverThread = new Thread(sever);
        serverThread.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                var shelf = sever.getDictionaryShelf();
                storage.save(shelf);
            } catch (IOException e) {
                logger.logErr("Cannot save dictionary before shutdown");
                logger.logErr(e);
            }
        }));
    }
}