package Controller;

import Model.Dictionary.DictionaryShelf;
import Model.Response.ResponseData;
import Model.Response.StatusCode;
import Model.Word.WordDefinition;
import Parser.Statement.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class RequestController {
    private DictionaryShelf shelf = null;
    private final OutputType outputType;

    public RequestController(DictionaryShelf shelf, OutputType outputType) {
        this.shelf = shelf;
        this.outputType = outputType;
    }

    public RequestController(DictionaryShelf shelf) {
        this.shelf = shelf;
        this.outputType = OutputType.TEXT;
    }

    public String resolve(Statement statement) {
        if (statement instanceof QueryStatement queryStatement) {
            return this.encodeByType(this.queryHandler.apply(queryStatement));
        }
        if (statement instanceof UpsertStatement upsertStatement) {
            return this.encodeByType((this.upsertHandler.apply(upsertStatement)));
        }
        if (statement instanceof CreateDictStatement createDictStatement) {
            return this.encodeByType(this.createDictHandler.apply(createDictStatement));
        }
        if (statement instanceof UseDictStatement useDictStatement) {
            return this.encodeByType(this.useDictHandler.apply(useDictStatement));
        }
        throw new RuntimeException("Unexpected statement type");
    }

    private String encodeByType(ResponseData responseData) {
        ResponseEncoder encoder = new ResponseEncoder(this.outputType);
        return encoder.encode(responseData);
    }

    private final Function<UpsertStatement, ResponseData<List<WordDefinition>>> upsertHandler = upsertStatement -> {
        var targetDictionary = upsertStatement.targetDictionary == null ? shelf.getCurrentDictionary() : upsertStatement.targetDictionary;
        var wordList = upsertStatement.newWords;

        ResponseData<List<WordDefinition>> responseData = new ResponseData<>();

        if (!this.shelf.hasDictionary(targetDictionary)) {
            responseData.setStatusCode(StatusCode.BadRequest);
            responseData.setErrorMessage("No Such Dictionary");
        } else {
            try {
                for (WordDefinition word : wordList) {
                    this.shelf.upsert(word, targetDictionary);
                }
                responseData.setStatusCode(StatusCode.Success);
                responseData.setData(wordList.stream().toList());
            } catch (Exception e) {
                responseData.setStatusCode(StatusCode.ServerError);
                responseData.setMessage("Error");
                responseData.setErrorMessage(e.getMessage());
            }
        }

        return responseData;
    };


    private final Function<QueryStatement, ResponseData<List<WordDefinition>>> queryHandler = statement -> {
        var targetDictionary = statement.targetDictionary == null ? shelf.getCurrentDictionary() : statement.targetDictionary;
        var wordList = statement.wordList;

        ResponseData<List<WordDefinition>> responseData;
        if (!this.shelf.hasDictionary(targetDictionary)) {
            responseData = new ResponseData<>(StatusCode.BadRequest, null, null, "No such dictionary " + targetDictionary);
        } else {
            try {
                List<WordDefinition> defs = new ArrayList<>();
                for (String searchWord : wordList) {
                    Optional<WordDefinition> definition = this.shelf.lookup(searchWord, targetDictionary);
                    if (definition.isEmpty()) {
                        defs.add(new WordDefinition(searchWord, "Not Found", null, null));
                    } else {
                        defs.add(definition.get());
                    }
                }
                responseData = new ResponseData<>(StatusCode.Success, defs, null, null);
            } catch (Exception e) {
                responseData = new ResponseData<>(StatusCode.ServerError, null, null, e.getMessage());
            }
        }

        return responseData;
    };

    private final Function<CreateDictStatement, ResponseData<String>> createDictHandler = createDictStatement -> {
        String dictionary = createDictStatement.dictionary;
        if (this.shelf.hasDictionary(dictionary)) {
            return new ResponseData<>(StatusCode.BadRequest, null, null, "Dictionary " + dictionary + " already exists");
        }
        try {
            this.shelf.addDictionary(dictionary);
            return new ResponseData<>(StatusCode.Success, null, "Create Success", null);
        } catch (Exception e) {
            return new ResponseData<>(StatusCode.ServerError, null, null, "Create Dictionary " + dictionary + " failed." + e.getMessage());
        }
    };

    private final Function<UseDictStatement, ResponseData<String>> useDictHandler = useDictStatement -> {
        String dictionary = useDictStatement.dictionary;
        if(dictionary.equals("DEFAULT")) {
            this.shelf.resetCurrentDictionary();
            return new ResponseData<>(StatusCode.Success, null, null, "Change Dictionary to default one:" + this.shelf.getCurrentDictionary());
        }
        if (!this.shelf.hasDictionary(dictionary)) {
            return new ResponseData<>(StatusCode.BadRequest, null, null, "Dictionary " + dictionary + " do not exists");
        }
        try {
            this.shelf.setDictionary(dictionary);
            return new ResponseData<>(StatusCode.Success, null, null, null);
        } catch (Exception e) {
            return new ResponseData<>(StatusCode.ServerError, null, null, "Change Dictionary " + dictionary + "Failed." + e.getMessage());
        }
    };
}
