/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import Logger.Logger;
import Model.Dictionary.DictionaryShelf;
import Server.DictServTask;
import Server.Storage;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        var storage = new Storage("dictionary");
        Logger logger = Logger.getInstance();
        var loadedShelf = storage.loadStorage();
        DictServTask sever = new DictServTask(8888, loadedShelf.orElseGet(() -> {
            logger.logInfo("Cannot load dictionary. Create a new one instead.");
            return new DictionaryShelf("<default>");
        }));
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