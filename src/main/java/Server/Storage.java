package Server;

import Logger.Logger;
import Model.Dictionary.DictionaryShelf;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Storage {
    private final String filename;
    Logger logger = Logger.getInstance();

    public Storage(String filename) {
        this.filename = filename;
    }

    static String storagePath = "";

    public Optional<DictionaryShelf> loadStorage() {
        try {
            var maybeShelf = this.load();
            if(maybeShelf.isPresent()) {
                var loadedShelf = maybeShelf.get();
                if(loadedShelf instanceof DictionaryShelf dictionaryShelf) {
                    dictionaryShelf.resetCurrentDictionary();
                    logger.logInfo("Success load Dictionary from device");
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
    public void save(Serializable object) throws IOException {
        Path path = this.getPath(filename);


        var file = path.toFile();
        if(!file.exists()){
            file.createNewFile();
        }

        var out = new ObjectOutputStream(Files.newOutputStream(path));
        out.writeObject(object);
        logger.logInfo("Success Save Dictionary");
    }

    private Optional<Object> load() throws IOException, ClassNotFoundException {
        Path pathOfFile = this.getPath(filename);
        File file = pathOfFile.toFile();
        if (file.exists()){
            var in = new ObjectInputStream(Files.newInputStream(pathOfFile));
            return Optional.of(in.readObject());
        } else {
            return Optional.empty();
        }
    }

    private Path getPath(String filename) {
        return Path.of(storagePath + filename);
    }
}
