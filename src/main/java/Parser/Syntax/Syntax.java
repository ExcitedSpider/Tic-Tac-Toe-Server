/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

package Parser.Syntax;

import Parser.Lexer.Lexer;
import Parser.Statement.StatementBuilder;
import Parser.SyntaxError.SyntaxError;

public abstract class Syntax {
    protected abstract SyntaxLambda syntax();
    public void run(StatementBuilder builder, Lexer lexer) throws SyntaxError {
        this.syntax().parse(builder, lexer);
    };
}
