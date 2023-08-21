package Parser.Syntax;

import Parser.Lexer.Lexer;
import Parser.Statement.StatementBuilder;
import Parser.SyntaxError.SyntaxError;

public interface SyntaxLambda {

    public void parse(StatementBuilder builder, Lexer lexer) throws SyntaxError;
}
