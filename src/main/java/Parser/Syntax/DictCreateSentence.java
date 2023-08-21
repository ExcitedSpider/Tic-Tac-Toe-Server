package Parser.Syntax;

import Parser.Lexer.WordToken;
import Parser.SyntaxError.SyntaxError;

public class DictCreateSentence extends Syntax {
    @Override
    protected SyntaxLambda syntax() {
        return (builder, lexer) -> {
            var token = lexer.nextToken();
            if (token instanceof WordToken wordToken && wordToken.value.equals("DICT")) {
                var targetToken = lexer.nextToken();
                if(targetToken instanceof WordToken targetDictToken) {
                    builder.setDictionary(targetDictToken.value);
                } else {
                    throw new SyntaxError("Invalid token " + targetToken);
                }
            } else {
                throw new SyntaxError("Invalid token " + token);
            }
        };
    }
}
