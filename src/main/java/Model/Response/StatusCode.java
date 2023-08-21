package Model.Response;

public enum StatusCode {
    Success (200, "Success"),
    BadRequest(400, "Bad Request"),
    ServerError(500, "Server Error");


    public int getCodeValue() {
        return codeValue;
    }

    public String getCodeName() {
        return codeName;
    }

    private final int codeValue;
    private final String codeName;

    StatusCode(int codeValue, String codeName){
        this.codeValue = codeValue;
        this.codeName = codeName;
    }
}
