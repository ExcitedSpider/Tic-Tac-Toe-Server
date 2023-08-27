/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

package Parser.Syntax;

import Parser.Lexer.WordToken;
import Parser.SyntaxError.SyntaxError;

public class QueryWordsSentence extends Syntax{
    @Override
    protected SyntaxLambda syntax() {
        return (statementBuilder, lexer) -> {
            wordListClause.parse(statementBuilder, lexer);
            fromClause.parse(statementBuilder, lexer);
        };
    }

    SyntaxLambda wordListClause = (builder, lexer) -> {
        final var token = lexer.peekNextToken();
        if(token instanceof WordToken wordToken){
            if(wordToken.value.equals("FROM")) {
                throw new SyntaxError("Query function need at least one word");
            }
            builder.appendWord(wordToken.value);
            lexer.nextToken();
            this.restWordsClause.parse(builder, lexer);
        } else {
            throw new SyntaxError("Unexpected End Of File");
        }
    };

    SyntaxLambda restWordsClause = (builder, lexer) -> {
        final var token = lexer.peekNextToken();
        if(token instanceof WordToken wordToken && !wordToken.value.equals("FROM")){
            builder.appendWord(wordToken.value);
            lexer.nextToken();
            this.restWordsClause.parse(builder, lexer);
        }
    };

    SyntaxLambda fromClause = (builder, lexer) -> {
        var token = lexer.nextToken();
        if(token instanceof WordToken wordToken && wordToken.value.equals("FROM")){
            final var dictionary = lexer.nextToken();
            if(dictionary instanceof WordToken dictionaryToken) {
                builder.setDictionary(dictionaryToken.value);
            } else {
                throw new SyntaxError("Query From Clause needs a dictionary name");
            }
        }
    };
}
