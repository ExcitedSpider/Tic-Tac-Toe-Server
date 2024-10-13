package models;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.io.Serializable;

public enum PlayerRole implements Serializable {
    Unknown(-1),
    Cross(1),
    Naught(2)
    ;

    private final int value;

    PlayerRole(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
