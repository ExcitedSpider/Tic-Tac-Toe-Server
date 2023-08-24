package Model.Dictionary;

import Model.Word.WordDefinition;

import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class DictionaryShelf implements Serializable {
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, WordDefinition>> dictionaryShelf = new ConcurrentHashMap<>();
    transient private String currentDictionary;
    private String defaultDictionary;

    public DictionaryShelf(String defaultDictionary) {
        this.defaultDictionary = defaultDictionary;
        this.dictionaryShelf.put(defaultDictionary, new ConcurrentHashMap<>());
        this.currentDictionary = defaultDictionary;
    }

    public boolean hasDictionary(String dictionaryName) {
        return dictionaryShelf.containsKey(dictionaryName);
    }

    public Optional<WordDefinition> lookup(String word, String dictionaryName) throws Exception {
        if (!dictionaryShelf.containsKey(dictionaryName)) {
            throw new Exception("No such dictionary " + dictionaryName);
        }
        var dictionary = dictionaryShelf.get(dictionaryName);
        if (!dictionary.containsKey(word)) {
            return Optional.empty();
        }
        ;
        return Optional.of(dictionary.get(word));
    }

    public Optional<WordDefinition> lookup(String word) throws Exception {
        return this.lookup(word, this.currentDictionary);
    }

    public void upsert(WordDefinition def, String dictionaryName) throws Exception {
        if (!dictionaryShelf.containsKey(dictionaryName)) {
            throw new Exception("No such dictionary " + dictionaryName);
        }

        var dictionary = dictionaryShelf.get(dictionaryName);
        dictionary.put(def.spelling(), def);
    }

    public void upsert(WordDefinition def) throws Exception {
        this.upsert(def, this.currentDictionary);
    }

    public void addDictionary(String dictionaryName) throws Exception {
        if (dictionaryShelf.containsKey(dictionaryName)) {
            throw new Exception("Already has this dictionary" + dictionaryName);
        }
        ;

        dictionaryShelf.put(dictionaryName, new ConcurrentHashMap<>());
    }

    public void setDictionary(String dictionaryName) throws Exception {
        if (!dictionaryShelf.containsKey(dictionaryName)) {
            throw new Exception("No such dictionary " + dictionaryName);
        }

        this.currentDictionary = dictionaryName;
    }

    public String getCurrentDictionary() {
        return currentDictionary;
    }

    public String getDefaultDictionary() {
        return defaultDictionary;
    }

    public void resetCurrentDictionary() {
        this.currentDictionary = defaultDictionary;
    }

    public void setDefaultDictionary(String defaultDictionary) {
        this.defaultDictionary = defaultDictionary;
    }

    @Override
    public String toString() {
        return "DictionaryShelf{" +
                "dictionaryShelf=" + dictionaryShelf +
                ", currentDictionary='" + currentDictionary + '\'' +
                ", defaultDictionary='" + defaultDictionary + '\'' +
                '}';
    }
}
