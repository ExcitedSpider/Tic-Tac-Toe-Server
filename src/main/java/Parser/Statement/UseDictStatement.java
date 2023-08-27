/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

package Parser.Statement;

import Parser.SyntaxError.SyntaxError;

public class UseDictStatement extends Statement {
    @Override
    public StatementType type() {
        return StatementType.UseStatement;
    }

    public final String dictionary;

    public UseDictStatement() throws SyntaxError {
        throw new SyntaxError("Must provide target dictionary");
    }

    public UseDictStatement(String dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public String toString() {
        return "UseDictStatement{" +
                "dictionary='" + dictionary + '\'' +
                '}';
    }
}
