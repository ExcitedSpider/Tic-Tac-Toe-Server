/*
 * Name: Chew-Yi
 * Surname: Feng
 * StudentID: 1431319
 */
module T3 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires io.reactivex.rxjava3;
    requires org.apache.commons.csv;
    exports T3;
    exports T3.models;
    exports T3.modelview;
    exports T3.components.board;
    exports models;
    exports interfaces.rmi;
    exports T3Server;
    exports T3.controller;
    exports T3.eventbus;
    exports T3.components;
    exports T3Server.model;
    exports T3Server.controller;
}
