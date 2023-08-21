package Parser.Lexer;

import Parser.SyntaxError.SyntaxError;
import org.junit.Test;

import java.util.stream.Stream;


public class LexerTest {

    @Test
   public void nextTokenTest() throws SyntaxError  {
        var lexer = new Lexer("UPSERT (Nunya, \"Thank you\") (Yuwei, \"Until we meet again\") (Boorie, \"Boy, child\", boa-rie, Noun) into Dict1");

        Stream.generate(() -> {
            try {
                return lexer.nextToken();
            } catch (SyntaxError e) {
                throw new RuntimeException(e);
            }
        }).takeWhile(w -> !w.isEof()).peek(System.out::println).count();
    }
}