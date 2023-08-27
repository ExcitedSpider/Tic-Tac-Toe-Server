/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

package Parser.Syntax;

import Parser.Lexer.WordToken;
import Parser.Statement.StatementType;
import Parser.SyntaxError.SyntaxError;

public class Sentence extends Syntax {
    @Override
    protected SyntaxLambda syntax() {
        return (builder, lexer) -> {
            var token = lexer.nextToken();
            if(token instanceof WordToken wordToken){
                switch (wordToken.value) {
                    case "LIST", "LS" -> {
                        builder.setType(StatementType.DirectiveStatement);
                        builder.setDirective("LS");
                    }
                    case "USE" -> {
                        builder.setType(StatementType.UseStatement);
                        new DictUseSentence().run(builder, lexer);
                    }
                    case "CREATE" -> {
                        builder.setType(StatementType.CreateStatement);
                        new DictCreateSentence().run(builder, lexer);
                    }
                    case "DELETE" -> {
                        builder.setType(StatementType.DeleteStatement);
                        new DeleteWordsSentence().run(builder, lexer);
                    }
                    case "QUERY" -> {
                        builder.setType(StatementType.QueryStatement);
                        new QueryWordsSentence().run(builder, lexer);
                    }
                    case "UPSERT" -> {
                        builder.setType(StatementType.UpsertStatement);
                        new UpsertSentence().run(builder, lexer);
                    }
                    case "EXIT" -> builder.setType(StatementType.DisconnectStatement);
                    default -> throw new SyntaxError("Unexpected token " + wordToken.value);
                }
            } else {
                throw new SyntaxError("Unexpected End Of File");
            }
        };
    }
}

