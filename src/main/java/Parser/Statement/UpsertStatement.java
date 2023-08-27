/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

package Parser.Statement;

import Model.Word.WordDefinition;

import java.util.Set;

public class UpsertStatement extends Statement{
    public final Set<WordDefinition> newWords;
    public final String targetDictionary;

    @Override
    public StatementType type() {
        return StatementType.UpsertStatement;
    }

    @Override
    public String toString() {
        return "UpsertStatement{" +
                "newWords=" + newWords +
                ", targetDictionary='" + targetDictionary + '\'' +
                '}';
    }

    public UpsertStatement(Set<WordDefinition> wordList, String targetDictionary) {
        this.newWords = wordList;
        this.targetDictionary = targetDictionary;
    }

    public UpsertStatement(Set<WordDefinition> wordList) {
        this.newWords = wordList;
        this.targetDictionary = null;
    }
}
