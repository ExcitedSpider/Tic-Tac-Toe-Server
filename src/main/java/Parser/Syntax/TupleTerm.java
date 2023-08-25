package Parser.Syntax;

import Parser.Lexer.Lexer;
import Parser.Lexer.SymbolEnum;
import Parser.Lexer.SymbolToken;
import Parser.Lexer.WordToken;
import Parser.SyntaxError.SyntaxError;

import java.util.ArrayList;
import java.util.List;

public record TupleTerm(List<String> tupleItems) {

    @Override
    public String toString() {
        return tupleItems.toString();
    }

    static public TupleTerm fromLexer(Lexer lexer) throws SyntaxError {
        var token = lexer.peekNextToken();
        var items = new ArrayList<String>();
        if (token instanceof SymbolToken symbolToken && symbolToken.symbol == SymbolEnum.LBracket) {
            lexer.nextToken();
            token = lexer.nextToken();
            while (true) {
                if (token instanceof WordToken nextWordToken) {
                    items.add(nextWordToken.value);
                } else if (token instanceof SymbolToken nextSymbolToken) {
                    if (nextSymbolToken.symbol == SymbolEnum.RBracket) {
                        break;
                    } else if (nextSymbolToken.symbol != SymbolEnum.Comma) {
                        throw new SyntaxError("Unexpected Symbol in Tuple " + token);
                    }
                } else {
                    throw new SyntaxError("Unexpected Token in Token " + token);
                }
                token = lexer.nextToken();
            }
            return new TupleTerm(items);
        }
        return null;
    }
}
