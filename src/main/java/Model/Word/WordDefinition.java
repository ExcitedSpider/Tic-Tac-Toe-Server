package Model.Word;

import java.io.Serializable;

public record WordDefinition(String spelling, String meaning, String type, String pronounce) implements Serializable {
    @Override
    public int hashCode() {
        return this.spelling.hashCode();
    }

    @Override
    public String toString() {
        var stringBuilder = new StringBuilder();
        stringBuilder.append(spelling);
        if (meaning != null) {
            stringBuilder.append(": ").append(meaning);
        }
        if(type != null) {
            stringBuilder.append(type).append("\n");
        }
        if(pronounce != null) {
            stringBuilder.append(pronounce).append("\n");
        }
        return stringBuilder.toString();
    }
}
