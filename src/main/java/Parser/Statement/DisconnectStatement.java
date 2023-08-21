package Parser.Statement;

public class DisconnectStatement extends Statement{

    @Override
    public StatementType type() {
        return StatementType.DisconnectStatement;
    }
}
