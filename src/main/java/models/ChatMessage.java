package models;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import java.io.Serializable;
import java.util.Objects;

public class ChatMessage implements Serializable {
    final String content;
    final String sender;
    final String time;
    public ChatMessage(String content, String sender, String time) {
        this.content = content;
        this.sender = sender;
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return sender;
    }

    public String getTime() {
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatMessage that = (ChatMessage) o;
        return Objects.equals(content, that.content) && Objects.equals(sender, that.sender) && Objects.equals(time, that.time);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, sender, time);
    }

    @Override
    public String toString() {
        return "Message{" +
                "content='" + content + '\'' +
                ", sender='" + sender + '\'' +
                ", time='" + time + '\'' +
                '}';
    }
}
