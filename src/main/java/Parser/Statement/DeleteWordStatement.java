package Parser.Statement;

import java.util.ArrayList;
import java.util.List;

public class DeleteWordStatement extends Statement{

    public final List<String> wordList;
    public final String targetDictionary;

    public DeleteWordStatement(List<String> wordList, String targetDictionary) {
        this.wordList = new ArrayList<>(wordList);
        this.targetDictionary = targetDictionary;
    }

    public DeleteWordStatement(List<String> wordList) {
        this.wordList = new ArrayList<>(wordList);
        this.targetDictionary = null;
    }

    @Override
    public StatementType type() {
        return StatementType.DeleteStatement;
    }

    @Override
    public String toString() {
        return  "DELETE" + " [" +
                String.join(",", wordList) +
                "]" +
                " FROM " +
                this.targetDictionary;
    }
}
