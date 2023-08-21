package Server;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

public class Storage {
    private final String filename;

    public Storage(String filename) {
        this.filename = filename;
    }

    static String storagePath = "";
    public void save(Serializable object) throws IOException {
        Path path = this.getPath(filename);


        var file = path.toFile();
        if(!file.exists()){
            file.createNewFile();
        }

        var out = new ObjectOutputStream(Files.newOutputStream(path));
        out.writeObject(object);
    }

    public Optional<Object> load() throws IOException, ClassNotFoundException {
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
