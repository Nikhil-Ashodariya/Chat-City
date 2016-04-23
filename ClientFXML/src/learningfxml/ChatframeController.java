package learningfxml;

import java.awt.Font;
import java.awt.GridLayout;
import java.awt.List;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import javafx.fxml.FXML;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.util.Duration;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * FXML Controller class
 *
 * @author Nikhil Smart
 */
public class ChatframeController implements Initializable
{

    int removefriendindex;

    JPanel jp = new JPanel(new GridLayout(5, 2));
    JFrame jf = new JFrame();
    List allusers = new List();
    JButton sendfr = new JButton("Send Request");
    List pendingrequestlist = new List();
    JLabel sendrequestlabel = new JLabel("Send Request");
    JLabel pendingrequestlabel = new JLabel("Pending Request");
    JButton acceptbutton = new JButton("Accept Request");
    JButton rejectbutton = new JButton("Reject Request");

    ObservableList<String> temp = FXCollections.observableList(new ArrayList());
    BufferedReader nis = Login.nis;
    PrintWriter nos = Login.nos;
    @FXML
    private ImageView adsview;

    @FXML
    public ListView<String> userlist;

    @FXML
    private Button modifyfriends;

    @FXML
    private Label status;

    @FXML
    private TextField destination;

    @FXML
    private Button signout;

    @FXML
    private Button sendbutton;

    @FXML
    private TextField message;

    @FXML
    private TextArea messagearea;

    @FXML
    void sendmessage(ActionEvent event) throws Exception
    {
        FadeTransition ft = new FadeTransition(Duration.millis(3000), sendbutton);
        ft.setFromValue(1.0);
        ft.setToValue(0.1);
        ft.setCycleCount(2);
        ft.setAutoReverse(true);
        ft.play();

        Cryptographic c = new Cryptographic();

        String msg = message.getText();
        String newmsg = c.encrypt(msg);
        String dest = c.encrypt(destination.getText());
        message.setText("");
        destination.setText("");
        nos.println(newmsg);
        nos.println(dest);
    }

    @FXML
    void signout(ActionEvent event) throws Exception
    {
        Cryptographic c = new Cryptographic();
        nos.println(c.encrypt("end"));
        Login.stage.close();
        Platform.exit();
        System.exit(0);
    }

    public void modifyFriendJFrame()
    {
        Cryptographic c = new Cryptographic();
        sendrequestlabel.setFont(new Font("monotype corsiva", Font.PLAIN, 20));
        pendingrequestlabel.setFont(new Font("monotype corsiva", Font.PLAIN, 20));

        /*Adding action listener to get the selected text*/
        pendingrequestlist.addActionListener(e
                ->
                {
                    String t = pendingrequestlist.getSelectedItem();
                    System.out.println("Selected string in pending list= " + t);
                    removefriendindex = pendingrequestlist.getSelectedIndex();
                });
        /*End of Adding action listener to get the selected text*/

        /*Adding action listener to the reject button*/
        rejectbutton.addActionListener(e
                ->
                {
                    pendingrequestlist.remove(removefriendindex);
                });
        /*End of Adding action listener to the reject button*/

        acceptbutton.addActionListener(e
                ->
                {
                    try
                    {
                        nos.println(c.encrypt("fraccept"));
                        nos.println(c.encrypt(Login.g.getUsername()));
                        if (pendingrequestlist.getSelectedItem() != null)
                        {
                            nos.println(c.encrypt(pendingrequestlist.getSelectedItem()));
                            pendingrequestlist.remove(removefriendindex);
                        }
                        else
                        {
                            nos.println(c.encrypt("nofriends"));
                        }
                    }
                    catch (Exception ex)
                    {
                        System.out.println("General error");
                    }
                });

        sendfr.addActionListener(e
                ->
                {
                    try
                    {
                        nos.println(c.encrypt("frrequest"));
                        nos.println(c.encrypt(Login.g.getUsername()));
                        nos.println(c.encrypt(allusers.getSelectedItem()));
                        allusers.remove(allusers.getSelectedItem());
                    }
                    catch (Exception ex)
                    {
                        System.out.println("Error in send friend request");
                    }
                });

        jp.add(pendingrequestlabel);
        jp.add(pendingrequestlist);
        jp.add(acceptbutton);
        jp.add(rejectbutton);

        jp.add(sendrequestlabel);
        jp.add(allusers);
        jp.add(sendfr);

        jf.add(jp);
        jf.setSize(500, 500);
        jf.setVisible(true);

//        temp.add("hello world");
        this.userlist.setItems(temp);
    }

    @FXML
    void modifyfriends(ActionEvent event) throws IOException
    {
        modifyFriendJFrame();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        //messagearea.setDisable(true);
        Cryptographic c = new Cryptographic();
        Login.stage.setTitle(Login.g.getUsername().toUpperCase());
        message.setPromptText("Enter messages");
        destination.setPromptText("Destination");
        BufferedReader fin;
        String stat = "";
        try
        {
            fin = new BufferedReader(new FileReader("D:\\n\\status.txt"));
            stat = fin.readLine();
        }
        catch (FileNotFoundException ex)
        {
            stat = "No Status Set";
        }
        catch (IOException ex)
        {
            System.out.println("Error in input output");
        }
        finally
        {
            status.setText(stat);
        }

        userlist.getSelectionModel().selectedItemProperty()
                .addListener(new ChangeListener<String>()
                {
                    public void changed(ObservableValue<? extends String> ov,
                            String old_val, String new_val)
                    {
                        destination.setText(new_val);
                    }
                });
        try
        {
            /*Printing users in List*/
            String str = nis.readLine();
            str = c.decrypt(str);
            if (str.equalsIgnoreCase("userstart"))
            {
                str = nis.readLine();//this will read ***
                str = c.decrypt(str);
                while (!str.equalsIgnoreCase("userend"))
                {
                    temp.add(str);
                    str = nis.readLine();
                    str = c.decrypt(str);
                }
            }
            userlist.setItems(temp);
            System.out.println(" out of the loop");
            /*ending Printing users in List*/
        }
        catch (IOException ex)
        {
            System.out.println("Error in chatframe input output");
        }
        catch (Exception ex)
        {
            System.out.println("General exception in chatframe controller");
        }

        try
        {
            /*Printing users in List*/
            String str = nis.readLine();
            str = c.decrypt(str);
            if (str.equalsIgnoreCase("allusersstart"))
            {
                str = nis.readLine();
                str = c.decrypt(str);
                while (!str.equalsIgnoreCase("allusersends"))
                {
                    allusers.add(str);
                    str = nis.readLine();
                    str = c.decrypt(str);
                }
            };
            System.out.println(" out of the loop");
            /*ending Printing users in List*/

            //now reading pending friend request from other clients
            String t = nis.readLine();
            t = c.decrypt(t);
            System.out.println("Error is t = "+t);
            if (t.equalsIgnoreCase("prstarts"))
            {
                t = nis.readLine();
                System.out.println("Error is t = "+t);
                t = c.decrypt(t);
                System.out.println("Error is t = "+t);
                while (!t.equalsIgnoreCase("prends"))
                {
                    pendingrequestlist.add(t);
                    t = nis.readLine();
                    t = c.decrypt(t);
                }
            }
            //end of reading pending friend request from other clients

            //reading all the user names
            //end of reading all the user name
        }
        catch (IOException ex)
        {
            Logger.getLogger(ChatframeController.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(ChatframeController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Thread t = new Thread(new Conv(nis, nos, adsview, messagearea, userlist, temp));
        t.start();
    }
}
