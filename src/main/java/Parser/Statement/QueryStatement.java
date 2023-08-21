package Parser.Statement;

import java.util.ArrayList;
import java.util.List;

public class QueryStatement extends Statement{
    public final List<String> wordList;
    public final String targetDictionary;
    @Override
    public StatementType type() {
        return StatementType.QueryStatement;
    }

    public QueryStatement(List<String> wordList, String targetDictionary) {
        this.wordList = new ArrayList<>(wordList);
        this.targetDictionary = targetDictionary;
    }

    public QueryStatement(List<String> wordList) {
        this.wordList = new ArrayList<>(wordList);
        this.targetDictionary = null;
    }

    @Override
    public String toString() {
        return "QueryStatement{" +
                "wordList=" + wordList +
                ", targetDictionary='" + targetDictionary + '\'' +
                '}';
    }
}
