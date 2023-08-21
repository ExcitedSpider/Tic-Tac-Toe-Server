package Parser.Lexer;

public class EofToken extends Token{
    @Override
    public boolean isEof() {
        return true;
    }

    @Override
    public String toLogableString() {
        return "EOF";
    }
}
