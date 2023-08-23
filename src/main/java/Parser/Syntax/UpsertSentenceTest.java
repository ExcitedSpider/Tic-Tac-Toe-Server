package Parser.Syntax;

import Parser.Parser;
import Parser.Statement.UpsertStatement;
import org.junit.Test;

public class UpsertSentenceTest {
    @Test
    public void evaluatesExpression() throws Exception {
        var statement = new Parser("UPSERT (Nunya, \"Thank you\") (Yuwei, \"Until we meet again\") (Boorie, \"Boy or child\", boa-rie, Noun) into Dict1").parseOneLine();

        if(statement instanceof UpsertStatement upsertStatement) {

        } else {
            throw new Exception("Invalid Statement Type");
        }
    }
}