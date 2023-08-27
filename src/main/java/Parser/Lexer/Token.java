/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */
package Parser.Lexer;

public abstract class Token {
    abstract public boolean isEof();

    abstract public String toLogableString();

    @Override
    public String toString() {
        return this.toLogableString();
    }
}
