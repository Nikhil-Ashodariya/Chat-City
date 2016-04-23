package learningfxml;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
//import javafx.scene.control.Dialog;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javax.crypto.NoSuchPaddingException;
import javax.swing.JOptionPane;

public class Conv implements Runnable
{
    Cryptographic c ;
    BufferedReader nis;
    PrintWriter nos;
    ImageView adsview;
    TextArea messagearea;
    ListView<String> userlist;
    ObservableList<String> observablelist;

    public Conv(BufferedReader nis, PrintWriter nos, ImageView adsview, TextArea messagearea, ListView<String> userlist, ObservableList<String> temp)
    {
        c = new Cryptographic();
        this.nis = nis;
        this.nos = nos;
        this.adsview = adsview;
        this.messagearea = messagearea;
        this.userlist = userlist;
        this.observablelist = temp;
    }

    @Override
    public void run()
    {
        try
        {
            String temp = nis.readLine();
            System.out.println("Encrypted message before = " + temp);
            temp=c.decrypt(temp);
            String str = new String(temp);
            System.out.println("message recieved = " + str);
            while (!str.equalsIgnoreCase("end"))
            {
                System.out.println("Temp = "+temp);
                if (str.equals("User logged out"))
                {
                    JOptionPane.showMessageDialog(null, "User logged out Sending offline message");
                }
                else if (str.equalsIgnoreCase("newuserstart"))//this is because server is sending uncrypted data for newusers
                {
                    System.out.println("in user start");
                    str = nis.readLine();
                    str=c.decrypt(str);
                    System.out.println("temp before adding= " + str);
                    observablelist.add(str);
                    str = nis.readLine();//this will read newuserend;
                    str = c.decrypt(str);
                    this.userlist.setItems(observablelist);
                }
                else if (str.equals("image"))//checks if the image is to be displayed the image is a ad
                {
                    String imagepath = nis.readLine();//this is useless as it contains the path of the image
                    imagepath=c.decrypt(imagepath);
//                    System.out.println("encrypted image path = " + imagepath);
                    System.out.println("decrypted image path = " + imagepath);

                    adsview.setVisible(true);
                    adsview.setImage(new Image(new File(imagepath).toURI().toString()));
                    Thread.sleep(3000);
                    adsview.setVisible(false);
                }
                else if (str.equalsIgnoreCase("No Such User"))//if the destination is not their in the database then this message is given
                {
//                    Dialogs.showErrorDialog(Login.stage, "No Such User");
                    JOptionPane.showMessageDialog(null, "No such user in conv of client");
                }
                else
                {
                    messagearea.appendText(str + "\n");
                }

                temp = nis.readLine();
                System.out.println("Encrypted message = " + temp);
                str = c.decrypt(temp);
                System.out.println("decryped message = " + str);
            }
        }
        catch (IOException ex)
        {
            Logger.getLogger(Conv.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (InterruptedException ex)
        {
            Logger.getLogger(Conv.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Conv.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}