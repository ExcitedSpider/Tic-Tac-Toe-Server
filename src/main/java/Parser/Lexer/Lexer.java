package Parser.Lexer;

import Parser.SyntaxError.SyntaxError;

import java.util.regex.Pattern;

class LexerHelper {
    public static boolean shouldOmit(Character input) {
        return input == ' ' || input == '\n' || input == '\t';
    }

    private static Pattern illegalPattern = Pattern.compile("[%#$<>]");

    private static Pattern wordPattern = Pattern.compile("[\\w-_]");

    public static java.util.regex.Matcher matchILegalWord(String word) {
        return LexerHelper.illegalPattern.matcher(word);
    }

    public static boolean isLegalWordChar(char character) {
        return LexerHelper.wordPattern.matcher(String.valueOf(character)).matches();
    }
}

public class Lexer {
    private final String input;
    private int currentLocation = 0;

    public Lexer(String input) {
        this.input = input;
    }

    // Peek next token but do not move poiner
    public Token peekNextToken() throws SyntaxError {
        var oldPosition = this.currentLocation;
        var token = this.nextToken();
        this.currentLocation = oldPosition;
        return token;
    }

    // Get Next token and move pointer
    public Token nextToken() throws SyntaxError {
        var stringBuilder = new StringBuilder();
        if (this.isEof()) {
            return new EofToken();
        }

        while (LexerHelper.shouldOmit(input.charAt(currentLocation))) {
            this.currentLocation++;
            if (this.isEof()) {
                return new EofToken();
            }
        }

        var symbolToken = parseSymbolToken();
        if(symbolToken!=null) {
            return symbolToken;
        }

        var startWithQuote = input.charAt(currentLocation) == '"';
        if (startWithQuote) {
            quotedWord(stringBuilder);
        } else {
            unquotedWord(stringBuilder);
        }

        var word = stringBuilder.toString();

        var scan = LexerHelper.matchILegalWord(word);
        if (!scan.matches()) {
            return new WordToken(word);
        } else {
            throw new SyntaxError("Word\"" + word + "(" + ")" + "\"contains illegal character "+ scan.toString() +". Only English letters and numbers are allowed and it should starts with a letter.");
        }

    }

    private SymbolToken parseSymbolToken() {
        var character = input.charAt(currentLocation);
        var token = switch (character) {
            case '(' -> new SymbolToken(SymbolEnum.LBracket);
            case ')' -> new SymbolToken(SymbolEnum.RBracket);
            case ',' -> new SymbolToken(SymbolEnum.Comma);
            case '.' -> new SymbolToken(SymbolEnum.End);
            default -> null;
        };
        if(token != null) {
            this.currentLocation ++;
        };
        return token;
    }

    private void unquotedWord(StringBuilder stringBuilder) {
        var currentChar = input.charAt(currentLocation);

        while (!LexerHelper.shouldOmit(currentChar) && LexerHelper.isLegalWordChar(currentChar)) {
            stringBuilder.append(currentChar);
            this.currentLocation++;
            if (this.isEof()) {
                break;
            }
            currentChar = input.charAt(currentLocation);
        }
    }

    private void quotedWord(StringBuilder stringBuilder) throws SyntaxError {
        this.currentLocation ++;
        while (input.charAt(currentLocation) != '"'){
            stringBuilder.append(input.charAt(currentLocation));
            this.currentLocation ++;
            if(this.isEof()){
                throw new SyntaxError("Quote word is not finished: \"" + stringBuilder.toString() + "\nPlease check your sentence.");
            }
            if(input.charAt(currentLocation) == '\\'){
                char escapedChar = input.charAt(currentLocation + 1);
                if(escapedChar == '\"') {
                    stringBuilder.append('"');
                } else if(escapedChar == 'n') {
                    stringBuilder.append('\n');
                } else if(escapedChar == 't') {
                    stringBuilder.append('\t');
                } else  {
                    throw  new SyntaxError("Unsupported Escape Character \"\\" + escapedChar + "\"");
                }
                this.currentLocation += 2;
            }
        }
        this.currentLocation ++;
    }


    private boolean isEof() {
        return input.length() == currentLocation;
    }
}
