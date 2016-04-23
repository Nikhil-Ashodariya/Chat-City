package messanger;

import java.awt.HeadlessException;
import java.io.*;
import java.sql.*;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class MessageDispatcher extends Thread
{

    Cryptographic c = new Cryptographic();

    @Override
    public void run()
    {
        PrintWriter p = null;

        /*Getting Ads DataBase Connection and driver*/
        ResultSet adsresult = null;
        Statement adsstatement;
        Connection adsconn;
        AdsDbDriver ads = (AdsDbDriver) Global.drivers.get("ads");
        /*end of Getting Ads DataBase Connection and driver*/

        /*Getting Msg DataBase Connection and driver*/
        PreparedStatement msgstatement = null;
        Connection msgconn;
        MessageDbDriver msg = (MessageDbDriver) Global.drivers.get("message");
        /*end of Getting Ads DataBase Connection and driver*/

        /*Executing Query for harmful word*/
        Connection threatconn;
        Statement threatstatement;
        ResultSet threatresult = null;
        /*End of Executing Query for harmful word*/
        try
        {
            /*Executing Query on Ads DataBase*/
            Class.forName(ads.getDriver());
            adsconn = DriverManager.getConnection(ads.getDburl(), ads.getUsername(), ads.getPassword());
            adsstatement = adsconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);//normally the Statement created is not scrollable i.e. you can't move to previous row to move to previous row you need to create Statement as Shown
            adsresult = adsstatement.executeQuery("select * from " + ads.getTable());
            /*End of Executing Query on Ads DataBase*/

            /*Executing PreparedStatement Query on Msg DataBase*/
            Class.forName(msg.getDriver());
            msgconn = DriverManager.getConnection(msg.getDburl(), msg.getUsername(), msg.getPassword());
            msgstatement = msgconn.prepareStatement("insert into " + msg.getTable() + " values(" + "?" + "," + "?" + "," + "?" + "," + "?" + ")");
            /*End of Executing PreparedStatement Query on Msg DataBase*/

            /*Executing Query for harmful word*/
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            threatconn = DriverManager.getConnection("jdbc:derby://localhost:1527/Threat", "nikhil", "123");
            threatstatement = threatconn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            threatresult = threatstatement.executeQuery("select * from " + "THREAT_WORDS");
            /*End of Executing Query for harmful word*/

        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error in loading class in MessageDispatcher = " + ex);
        }
        catch (SQLException ex)
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error in executing SQL Statement in MessageDispacther  =  " + ex);
        }

        while (true)
        {
            System.out.println("Message load on network = " + Global.al.getNumberOfMsg());
            System.out.println("User load on network = " + GuiServer.m.size());
            Message m = Global.al.pop();//Removing a Message Object from ArrayList is Present username,source and destination are decrypted
            String str = m.getStr();
            try
            {
                /*Searching for user in database if present */
                System.out.println("destination = "+m.getDestination());
                System.out.println("Source = "+m.getSource());
                System.out.println("message = "+m.getStr());
                ResultSet rs = Login.getS().executeQuery("select * from " + GuiServer.table_name + " where name = '" + m.getDestination() + "'");
                if (rs.next())
                {
                    try
                    {
                        p = (PrintWriter) GuiServer.m.get(m.getDestination());//this object is used to send message to destination
                        PrintWriter adswriter = (PrintWriter) GuiServer.m.get(m.getSource());//this object is used if an ad is present and it is used to display in source of message side
                        if (p == null)//checking if user has signed in
                        {
                            /*sending offline message by storing it in file*/
//                            JOptionPane.showMessageDialog(null, "User logged out Sending offline message");
                            adswriter.println(c.encrypt("User logged out"));
                            FileWriter f = new FileWriter("D:\\" + m.getDestination() + ".txt", true);
                            PrintWriter pw = new PrintWriter(new BufferedWriter(f), true);
                            pw.println(c.encrypt(m.toString()));//storing message in file
                            f.close();//closing file 
                            /*end of  sending offline message by storing it in file*/
                        }

                        else if (str.equalsIgnoreCase("newuserstart"))
                        {
//                            p.println(e.encrypt("newuserstart"));
                            Message u = Global.al.pop();
                            String s = u.getStr();
                            while (!s.equalsIgnoreCase("newuserend"))
                            {
                                p.println(c.encrypt(m.getStr()));
                                u = Global.al.pop();
                                s = u.getStr();
                            }
                            p.println(c.encrypt("newuserend"));
                        }
                        else
                        {
                            p.println(c.encrypt(m.toString()));
                            adswriter.println(c.encrypt(m.toString()));

                        }
                        Date d = new Date();//Creating Date object so that Message can be Stored in Database 

                        /*Configuring (getting Data ) to Execute PreparedStatement*/
                        msgstatement.setString(1, m.getSource());
                        msgstatement.setString(2, m.getDestination());
                        msgstatement.setString(3, c.encrypt(str));
                        msgstatement.setString(4, d.toString());
                        /*end of Configuring (getting Data ) to Execute PreparedStatement*/
                        msgstatement.execute();//Executing PreparedStatement on Message DataBase 

                        /*Checking for Threat in DataBase*/
                        while (threatresult.next())
                        {
                            String threat = threatresult.getObject("words").toString();
                            if (str.toLowerCase().contains(threat.toLowerCase()))
                            {
                                System.out.println("User sended a Harmful Message please check it");
                                System.out.println(m);
                            }
                        }
                        while (threatresult.previous());//moving to the first record in the list
                        /*End of Checking for Threat in DataBase*/

                        /*Quering Ads DataBase to get the Ads Stored in DataBase*/
                        while (adsresult.next())
                        {
                            String service = adsresult.getObject("service").toString();//service store the key word that the company gives as ads
                            if (str.toLowerCase().contains(service))//Checking if the message contains any word that matchs to any ads
                            {
                                adswriter.println(c.encrypt("image"));
                                adswriter.println(c.encrypt(adsresult.getString("imageurl")));

                                p.println(c.encrypt("image"));
                                p.println(c.encrypt(adsresult.getString("imageurl")));
                            }
                        }
                        while (adsresult.previous());//moving up to the markup so that on next message it can check from starting since it is scrollable you can move up
                    }
                    catch (HeadlessException | IOException | SQLException asdf)
                    {
                        Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, asdf);
                        System.out.println("Error = " + asdf);
                    }
                }
                else
                {
                    p = (PrintWriter) GuiServer.m.get(m.getSource());//Getting write object of source since the destination is absent
                    p.println(c.encrypt("No Such User"));//Sending message to client that no account with that name exsits
                }
            }
            catch (Exception e)
            {
                Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, e);
                System.out.println("Exception = " + e);
            }
        }
    }
}
