package Parser.Lexer;

public class SymbolToken extends Token {
    @Override
    public boolean isEof() {
        return false;
    }

    final public SymbolEnum symbol;

    SymbolToken(SymbolEnum symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toLogableString() {
        return this.symbol.toString();
    }
}
