package Parser.Statement;


import Model.Word.WordDefinition;
import Parser.SyntaxError.SyntaxError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StatementBuilder {
    private StatementType targetType = null;
    private String targetDictionary = null;
    private final List<String> wordsList = new ArrayList<>();

    private final Set<WordDefinition> wordsDefSet = new HashSet<>();
    private String directive = null;

    public String getDirective() {
        return directive;
    }

    public void setDirective(String directive) {
        this.directive = directive;
    }

    public void setType(StatementType type){
        this.targetType = type;
    }
    public void setDictionary(String dictionary){
        this.targetDictionary = dictionary;
    }
    public void appendWord(String word) {this.wordsList.add(word);}

    public void addWordDefinition(WordDefinition wd) {
        this.wordsDefSet.add(wd);
    }

    public Statement buildStatement() throws SyntaxError {
        return switch (this.targetType) {
            case UseStatement -> new UseDictStatement(this.targetDictionary);
            case DeleteStatement ->
                    this.targetDictionary != null ? new DeleteWordStatement(this.wordsList, this.targetDictionary) : new DeleteWordStatement(this.wordsList);
            case QueryStatement ->
                    this.targetDictionary != null ? new QueryStatement(this.wordsList, this.targetDictionary) : new QueryStatement(this.wordsList);
            case UpsertStatement ->
                this.targetDictionary != null ? new UpsertStatement(this.wordsDefSet, this.targetDictionary) : new UpsertStatement(this.wordsDefSet);
            case CreateStatement -> new CreateDictStatement(this.targetDictionary);
            case DirectiveStatement -> new DirectiveStatement(this.directive);
            default -> throw new SyntaxError("No provided target type");
        };
    }
}
