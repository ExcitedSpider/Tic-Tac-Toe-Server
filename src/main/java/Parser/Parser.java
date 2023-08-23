package Parser;

import Parser.Lexer.Lexer;
import Parser.Statement.Statement;
import Parser.Statement.StatementBuilder;
import Parser.Syntax.Sentence;
import Parser.SyntaxError.SyntaxError;

import java.util.List;


public class Parser {
    private final String input;
    public Parser(String input){
        this.input = input;
    }

    public Statement parseOneLine() throws SyntaxError {
        var lexer = new Lexer(input);
        var statementBuilder = new StatementBuilder();
        new Sentence().run(statementBuilder, lexer);
        return statementBuilder.buildStatement();
    }
}
