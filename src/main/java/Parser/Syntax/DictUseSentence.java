package Parser.Syntax;

import Parser.Lexer.WordToken;
import Parser.SyntaxError.SyntaxError;

public class DictUseSentence extends Syntax {
    @Override
    protected SyntaxLambda syntax() {
        return (builder, lexer) -> {
            var token = lexer.nextToken();
            if(token instanceof WordToken wordToken){
                builder.setDictionary(wordToken.value);
            } else {
                throw new SyntaxError("Unexpected End Of File");
            }
        };
    }
}
