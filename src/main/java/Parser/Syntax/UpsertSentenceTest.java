package Parser.Syntax;

import Parser.Parser;
import Parser.Statement.UpsertStatement;
import org.junit.Test;

import static org.junit.Assert.*;

public class UpsertSentenceTest {
    @Test
    public void evaluatesExpression() throws Exception {
        var statement = new Parser("UPSERT (Nunya, \"Thank you\") (Yuwei, \"Until we meet again\") (Boorie, \"Boy or child\", boa-rie, Noun) into Dict1").parse();

        if(statement instanceof UpsertStatement upsertStatement) {

        } else {
            throw new Exception("Invalid Statement Type");
        }
    }
}