/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

package Parser.Syntax;

import Parser.Parser;
import Parser.Statement.QueryStatement;
import org.junit.Test;

import java.util.Objects;

public class QueryWordsSentenceTest {
    @Test
    public void evaluatesExpression() throws Exception {
        var statement = new Parser("QUERY \"Yi-da-ki\" ck abc FROM dict1").parseOneLine();
        if(statement instanceof QueryStatement queryStatement) {
            queryStatement.wordList.stream().forEach(System.out::println);
            assert queryStatement.wordList.size() == 3;
            assert Objects.equals(queryStatement.targetDictionary, "dict1");
            assert queryStatement.wordList.stream().distinct().count() == 3;
        } else {
            throw new Exception("Error Statement Type");
        }
    }
}