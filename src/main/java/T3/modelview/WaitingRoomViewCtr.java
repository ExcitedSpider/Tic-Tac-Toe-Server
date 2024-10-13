package T3.modelview;/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;

import java.util.Random;

public class WaitingRoomViewCtr {
    public TextField usernameField;
    public Label about;
    public Text errorMessage;
    public Button submitBtn;
    public Text infoMessage;

    public record EnterGameAction(String username) {
    }

    public void setUsername(String username) {
        this.usernameField.setText(username);
    }

    public void setLoading(boolean loading) {
        if(loading){
            errorMessage.setVisible(false);
            submitBtn.setDisable(true);
            infoMessage.setVisible(true);
            usernameField.setDisable(true);
        }else {
            errorMessage.setVisible(true);
            submitBtn.setDisable(false);
            infoMessage.setVisible(false);
            usernameField.setDisable(false);
        }
    }

    public void setErrorMessage(String message) {
        this.errorMessage.setText(message);
        this.errorMessage.setVisible(true);
    }


    public void submit(ActionEvent actionEvent) {
        String username = usernameField.getText();

        usernameField.getScene().getRoot().fireEvent(new GUIEvents.LoginEvent(username));
    }

    @FXML
    public void initialize() {
        usernameField.setText("User" + new Random().nextInt(100));
        about.setText(
                "Java Version:" + System.getProperty("java.version") + ". Powered By JavaFX(" + System.getProperty("javafx.version") + "). Author: Chew F."
        );
    }
}
