package T3.components;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import T3.UIConstants;
import T3.modelview.GUIEvents;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import models.ChatMessage;

import java.util.*;

public class ChatPanel extends GridPane {

    static final private int  PREF_WIDTH = 200;
    List<ChatMessage> chatMessages = new ArrayList<>();

    private TextInputControl userInputText;

    public ChatPanel(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
        this.render();
    }

    public ChatPanel() {
        this.render();
    }

    public void putMessage(ChatMessage message) {
        if(!chatMessages.contains(message)) {
            chatMessages.add(message);
            this.getChildren().clear();
            this.render();
        }
    }

    private void onSendMessage(String text) {
        if(text != null && !text.isEmpty()){
            this.fireEvent(new GUIEvents.SendChatMessage(text));
        }
    }


    private void render() {
        this.setPrefWidth(PREF_WIDTH);
        this.setPadding(new Insets(8));
        this.setAlignment(Pos.CENTER);
        this.setVgap(8);

        var topDiv = new VBox();
        topDiv.setAlignment(Pos.CENTER);
        topDiv.setPadding(new Insets(8));
        topDiv.getChildren().add(new Label("Chat Box"));
        this.add(topDiv, 0, 0);
        topDiv.setMinHeight(48);
        GridPane.setVgrow(topDiv, Priority.NEVER);

        var msgsView = new VBox();
        msgsView.setPrefWidth(PREF_WIDTH - 16);
        msgsView.setSpacing(4);
        msgsView.setAlignment(Pos.BOTTOM_LEFT);
        for (var msg: chatMessages.subList(
                 Math.max(chatMessages.size() - 10, 0),
                chatMessages.size()
        )) {
            var singleMessageView = new Label(msg.getSender() +": " + msg.getContent());
            singleMessageView.setWrapText(true);
            msgsView.getChildren().add(singleMessageView);
        }
        if(chatMessages.isEmpty()) {
            var emptyTip = new Label("No message yet. Use the input bellow to send a message:D");
            emptyTip.setWrapText(true);
            msgsView.getChildren().add(emptyTip);
        }

        ScrollPane scrollPane = new ScrollPane(msgsView);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setPadding(new Insets(4));
        msgsView.setMinHeight(250);

        var centerNode = scrollPane;
        GridPane.setVgrow(centerNode, Priority.ALWAYS);
        this.add(centerNode, 0, 1);


        var bottomView = new VBox();
        var textarea = new TextArea();
        textarea.setPromptText("Text and press enter to send. Be friendly.");
        textarea.setPrefHeight(60);
        textarea.setPrefWidth(PREF_WIDTH - 16);
        textarea.setOnKeyPressed(w -> {
            if(w.getCode() == KeyCode.ENTER){
                onSendMessage(textarea.getText());
                textarea.clear();
            }
        });
        bottomView.getChildren().add(textarea);
        this.add(bottomView, 0, 2);
        textarea.setWrapText(true);
        this.userInputText = textarea;
        GridPane.setVgrow(bottomView, Priority.NEVER);


    }

}
