package RequestHandler;

import Model.Response.ResponseData;
import Model.Response.StatusCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

public class ResponseEncoder {
    private final OutputType outputType;

    public ResponseEncoder(OutputType outputType) {
        this.outputType = outputType;
    }

    public String encode(ResponseData responseData) {
        if (this.outputType == OutputType.TEXT) {
            StatusCode statusCode = responseData.getStatusCode();
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(statusCode.getCodeValue()).append(" ").append(statusCode.getCodeName()).append("\n");

            if (responseData.getMessage() != null) {
                stringBuilder.append(responseData.getMessage().stripTrailing()).append("\n");
            } else if (responseData.getErrorMessage() != null) {
                stringBuilder.append(responseData.getErrorMessage().stripTrailing()).append("\n");
            }
            var dataModel = responseData.getData();
            if (dataModel != null) {
                if (dataModel instanceof List<?> listModel) {
                    stringBuilder.append(encodeModel(listModel));
                }
            }
            return stringBuilder.toString();
        } else {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.writeValueAsString(responseData) + "\n";
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String encodeModel(List<?> model) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object wordDef : model) {
            stringBuilder.append(wordDef).append("\n");
        }
        return stringBuilder.toString();
    }

}