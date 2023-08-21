package Model.Response;

public class ResponseData<T> {
    private StatusCode statusCode;
    private T data;
    private String message;
    private String errorMessage;

    public ResponseData() {}

    public ResponseData(StatusCode statusCode, T data, String message, String errorMessage) {
        this.statusCode = statusCode;
        this.data = data;
        this.message = message;
        this.errorMessage = errorMessage;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
