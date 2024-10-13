package interfaces.rmi;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.io.Serializable;

public class ServerResponse<T> implements Serializable {
    public final String message;

    public final T data;

    // 200 success
    // 400 Bad Request
    // 500 Server Error
    public final int code;

    public ServerResponse(int code, String message, T data) {
        this.message = message;
        this.data = data;
        this.code = code;
    }

    public static <T> ServerResponse<T> success(T data) {
        return new ServerResponse<T>(200, "Success", data);
    }
    public static <T> ServerResponse<T> serverError(String message) {
        return new ServerResponse<T>(500, message, null);
    }
    public static <T> ServerResponse<T> badRequest(String message) {
        return new ServerResponse<T>(400, message, null);
    }


    @Override
    public String toString() {
        return "ServerResponse{" +
                "message='" + message + '\'' +
                ", data=" + data +
                ", code=" + code +
                '}';
    }
}
