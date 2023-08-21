package Parser.Lexer;

public class WordToken extends Token {
    public final String value;
    public WordToken(String value) {
        this.value = value;
    }

    @Override
    public boolean isEof() {
        return false;
    }

    @Override
    public String toLogableString() {
        return this.value;
    }
}
