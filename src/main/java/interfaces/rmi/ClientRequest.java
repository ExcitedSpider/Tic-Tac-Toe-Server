package interfaces.rmi;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.io.Serializable;

public class ClientRequest<T> implements Serializable {
    public final String username;
    public final T data;

    public ClientRequest(String username, T data) {
        this.username = username;
        this.data = data;
    }
}
