package Parser;

import Parser.Lexer.Lexer;
import Parser.Statement.Statement;
import Parser.Statement.StatementBuilder;
import Parser.Syntax.Sentence;
import Parser.SyntaxError.SyntaxError;


public class Parser {
    private final Lexer lexer;
    private final StatementBuilder statementBuilder = new StatementBuilder();
    public Parser(String input){
        this.lexer = new Lexer(input);
    }

    public Statement parse() throws SyntaxError {
        new Sentence().run(this.statementBuilder, this.lexer);
        return this.statementBuilder.buildStatement();
    }
}
