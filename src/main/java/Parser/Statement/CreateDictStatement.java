package Parser.Statement;

public class CreateDictStatement extends Statement {
    public final String dictionary;


    public CreateDictStatement(String dictionary) {
        this.dictionary = dictionary;
    }

    @Override
    public StatementType type() {
        return StatementType.CreateStatement;
    }

    @Override
    public String toString() {
        return "CreateDictStatement{" +
                "dictionary='" + dictionary + '\'' +
                '}';
    }
}
