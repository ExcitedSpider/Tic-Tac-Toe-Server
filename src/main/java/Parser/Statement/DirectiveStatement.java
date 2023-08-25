package Parser.Statement;

public class DirectiveStatement extends Statement{
    public final String directive;

    public DirectiveStatement(String directive) {
        this.directive = directive;
    }

    @Override
    public StatementType type() {
        return StatementType.DirectiveStatement;
    }

    @Override
    public String toString() {
        return "DirectiveStatement{" +
                "directive='" + directive + '\'' +
                '}';
    }
}
