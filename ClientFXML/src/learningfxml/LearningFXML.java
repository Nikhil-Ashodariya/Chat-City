/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package learningfxml;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author Nikhil Smart
 */
public class LearningFXML extends Application
{

    static Stage s;

    @Override
    public void start(Stage stage) throws Exception
    {
        s = stage;
//        stage.initStyle(StageStyle.TRANSPARENT);
        Parent root = FXMLLoader.load(getClass().getResource("Login.fxml"));


        Scene scene = new Scene(root);
//        stage.initStyle(StageStyle.TRANSPARENT);

        stage.sizeToScene();

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws IOException
    {
        launch(args);
    }
}
