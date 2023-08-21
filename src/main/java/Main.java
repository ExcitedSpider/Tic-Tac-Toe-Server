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
        var loadedShelf = loadStorage(storage, logger);
        if (loadedShelf.isPresent()) {
            logger.logInfo(loadedShelf.get());
        }
        SimpleServer sever = new SimpleServer(8888, loadedShelf.orElseGet(() -> new DictionaryShelf("<default>")));
        var serverThread = new Thread(sever);
        serverThread.start();

        Runnable beforeShutdown = () -> {
            try {
                var shelf = sever.getDictionaryShelf();
                storage.save(shelf);
            } catch (IOException e) {
                logger.logErr("Cannot save dictionary before shutdown");
                logger.logErr(e);
            }
        };
        Runtime.getRuntime().addShutdownHook(new Thread(beforeShutdown));
    }

    private static Optional<DictionaryShelf> loadStorage(Storage storage, Logger logger) {
        try {
            var maybeShelf = storage.load();
            if(maybeShelf.isPresent()) {
                var loadedShelf = maybeShelf.get();
                if(loadedShelf instanceof DictionaryShelf dictionaryShelf) {
                    dictionaryShelf.resetCurrentDictionary();
                    return Optional.of(dictionaryShelf);
                }
            }
            logger.logInfo("Cannot find dictionary storage, create new one instead");

        } catch (Exception e) {
            logger.logErr("Error while loading dictionary");
            logger.logErr(e);
        }
        return Optional.empty();
    }
}