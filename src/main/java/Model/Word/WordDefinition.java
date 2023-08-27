/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */
package Model.Word;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

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
            stringBuilder.append(":").append(meaning);
        }

        Map<String, String> secondaryInfo = new HashMap<>();
        if(type!=null) secondaryInfo.put("type", type);
        if(pronounce!=null) secondaryInfo.put("pronounce", pronounce);
        if(!secondaryInfo.isEmpty()) {
            stringBuilder.append("\n");
            for (Map.Entry<?, ?> entry : secondaryInfo.entrySet()) {
                stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
            }
        }
        return stringBuilder.toString();
    }
}
