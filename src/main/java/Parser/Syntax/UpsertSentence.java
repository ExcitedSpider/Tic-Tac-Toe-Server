/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

package Parser.Syntax;

import Model.Word.WordDefinition;
import Parser.Lexer.WordToken;
import Parser.SyntaxError.SyntaxError;

import java.util.List;

public class UpsertSentence extends Syntax {

    @Override
    protected SyntaxLambda syntax() {
        return (builder, lexer) -> {
            this.wordDefsClause.parse(builder, lexer);
            this.intoClause.parse(builder, lexer);
        };
    }

    SyntaxLambda wordDefsClause = (builder, lexer) -> {
        TupleTerm tupleTerm = TupleTerm.fromLexer(lexer);
        if (tupleTerm == null) {
            throw new SyntaxError("The definition of word should be tuples like (Nunya, \"Thank you\")");
        }
        var definition = this.toWordDefinition(tupleTerm);
        builder.addWordDefinition(definition);

        this.restWordsDef.parse(builder, lexer);
    };

    SyntaxLambda restWordsDef = (builder, lexer) -> {
        final var nextToken = lexer.peekNextToken();
        if (nextToken.isEof()) {
            return;
        }
        TupleTerm tupleTerm = TupleTerm.fromLexer(lexer);
        if (tupleTerm != null) {
            var definition = this.toWordDefinition(tupleTerm);
            builder.addWordDefinition(definition);
            this.restWordsDef.parse(builder, lexer);
        }
    };

    SyntaxLambda intoClause = (builder, lexer) -> {
        var token = lexer.nextToken();
        if(token instanceof WordToken wordToken && wordToken.value.equals("INTO")){
            final var dictionary = lexer.nextToken();
            if(dictionary instanceof WordToken dictionaryToken) {
                builder.setDictionary(dictionaryToken.value);
            } else {
                throw new SyntaxError("UPSERT FROM Clause needs a dictionary name");
            }
        }
    };

    private WordDefinition toWordDefinition(TupleTerm tupleTerm) throws SyntaxError {
        List<String> items = tupleTerm.tupleItems();
        if (items.size() < 2) {
            throw new SyntaxError("""
                    Word definition should be at least length 2.
                    The first is the spelling, and the second is the meaning
                    """ + " " + tupleTerm);
        } else if (items.size() > 4) {
            throw new SyntaxError("""
                    Word definition should be at most length 4.
                    As (Spelling, Meaning, Pronounce, Type)
                    """ + " " + tupleTerm);
        }
        return new WordDefinition(
                items.get(0),
                items.get(1),
                items.size() > 2 ? items.get(2) : null,
                items.size() > 3 ? items.get(3) : null
        );
    }
}
