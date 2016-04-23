/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package learningfxml;

import java.io.*;
import java.net.*;
import java.util.ResourceBundle;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 *
 * @author Nikhil Smart
 */
public class Login implements Initializable
{
    Cryptographic c ;
    static GuiClient g;
    static Stage stage;
    int port = 1028;
    static BufferedReader nis;
    static PrintWriter nos;
    String uname;
    String upass;

    @FXML
    private Label login_status;

    @FXML
    private Button newuser;

    @FXML
    private PasswordField password;

    @FXML
    private Button loginbutton;

    @FXML
    private TextField username;

    public Login() throws IOException
    {
        this.g = new GuiClient();
        c=new Cryptographic();
    }

    @FXML
    void checkusername(ActionEvent event) throws IOException, InterruptedException, Exception
    {
        nis = g.getNis();
        nos = g.getNos();
        System.out.println("Socket created");
        uname = username.getText();
        upass = password.getText();
        System.out.println("name = " + uname);
        System.out.println("pass = " + upass);

        Path path = new Path();
        path.getElements().add(new MoveTo(20, 20));
        path.getElements().add(new CubicCurveTo(380, 0, 380, 120, 200, 120));
        path.getElements().add(new CubicCurveTo(0, 120, 0, 240, 380, 240));
        PathTransition pathTransition = new PathTransition();
        pathTransition.setDuration(Duration.millis(4000));
        pathTransition.setPath(path);
        pathTransition.setNode(loginbutton);
        pathTransition.setOrientation(PathTransition.OrientationType.ORTHOGONAL_TO_TANGENT);
        pathTransition.setCycleCount(2);
        pathTransition.setAutoReverse(true);
        pathTransition.play();

        nos.println(c.encrypt("olduser"));
        nos.println(c.encrypt(uname));
        nos.println(c.encrypt(upass));
        seeStatus();
    }

    @FXML
    void newuser(ActionEvent event) throws IOException, InterruptedException, Exception
    {
        nis = g.getNis();
        nos = g.getNos();
        uname = username.getText();
        upass = password.getText();
        System.out.println("uname = "+uname.length());
        System.out.println("upass = "+upass.length());
        if (uname.length() <= 3 || upass.length() <= 3)
        {
            login_status.setText("Uname and upass should >= 3");
        }
        else
        {
            nos.println(c.encrypt("newuser"));
            nos.println(c.encrypt(uname));
            nos.println(c.encrypt(upass));
            seeStatus();
        }
    }

    private void seeStatus() throws Exception
    {
        String status = nis.readLine();
        status=c.decrypt(status);
        if (status.equals("ok"))
        {
            login_status.setText("Logining Please Wait");
            g.setUsername(uname);

//            Parent root = FXMLLoader.load(getClass().getResource("Chatframe.fxml"));
//            Scene scene = new Scene(root);
//
//            stage.setScene(scene);
//            stage.show();
            Parent root = FXMLLoader.load(getClass().getResource("Chatframe.fxml"));
            Scene scene = new Scene(root);

            stage.setScene(scene);
            stage.show();
        }
        else
        {
            login_status.setText("Please Wait");
            System.out.println("Wrong username or Password");
            login_status.setText("Wrong Username or Password");
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

        // TODO
        stage = LearningFXML.s;
        loginbutton.setShape(new Circle(5));
        newuser.setShape(new Circle(5));
        System.out.println("initialize method is called");
    }
}
