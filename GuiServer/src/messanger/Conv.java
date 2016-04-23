package messanger;

import java.io.*;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conv implements Runnable
{

    private Socket soc;
    private String username;
    Cryptographic c;

    Conv(Socket soc, String username)
    {
        this.soc = soc;
        this.username = username;
        c = new Cryptographic();
    }

    @Override
    public void run()
    {
        BufferedReader read = null;
        PrintWriter write = null;
        String str = null, dest = null;
        /*Creating read and write object*/
        try
        {
            read = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            write = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true);
        }
        catch (IOException ex)
        {
            System.out.println("Error in creating read or write object in Conv thread = " + ex);
        }
        /*finished Creating read and write object*/

        /*Reading data */
        try
        {
            str = read.readLine();//this is encrypted message
            str = c.decrypt(str);
            if (str.equalsIgnoreCase("fraccept"))//if this condition is true then str is not encrypted because on sender side the data is encrypted only by the send button and not by the accept friend request button
            {
                String firstuser = c.decrypt(read.readLine());
                String seconduser = c.decrypt(read.readLine());

                Class.forName("org.apache.derby.jdbc.ClientDriver");//driver class
                Connection userconnect = DriverManager.getConnection("jdbc:derby://localhost:1527/Friends", "nikhil", "123");
                Statement s = userconnect.createStatement();

                s.executeUpdate("insert into Friends values('" + firstuser + "','" + seconduser + "')");
                s.executeUpdate("insert into Friends values('" + seconduser + "','" + firstuser + "')");
            }
            else if (str.equalsIgnoreCase("frrequest"))
            {
                String who_is_sending = c.decrypt(read.readLine());
                String to_whom_is_sending = c.decrypt(read.readLine());
                FileWriter f = new FileWriter("D:\\" + to_whom_is_sending + "fr.txt", true);
                PrintWriter pw = new PrintWriter(new BufferedWriter(f), true);
                pw.println(c.encrypt(who_is_sending));//storing the username in file
                f.close();//closing file 
            }
            else if (str.equalsIgnoreCase("end"))
            {
                GuiServer.m.remove(this.username);
            }
            else
            {
                dest = read.readLine();//this is encrypted destinaiton
                dest = c.decrypt(dest);
            }
        }
        catch (IOException ex)
        {
            try
            {
                System.out.println("Client Seems to have abruptly closed the connection in Conv");
                write.println(c.encrypt("end"));
                GuiServer.m.remove(this.username);
            }
            catch (Exception ex1)
            {
                Logger.getLogger(Conv.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        try
        {
            while (!str.equalsIgnoreCase("end"))
            {
                try
                {
                    if (!str.equalsIgnoreCase("frrequest") && !str.equalsIgnoreCase("fraccept"))
                    {
                        Message m = new Message(this.username, str, dest);//username, str and dest is decrypted here
                        Global.al.push(m);
                        str = read.readLine();
                        dest = read.readLine();
                        str = c.decrypt(str);
                        dest = c.decrypt(dest);
                    }
                }
                catch (IOException ex)
                {
                    System.out.println("Client Seems to have abruptly closed the connection in second Catch in Conv");
                    write.println(c.encrypt("end"));
                    GuiServer.m.remove(this.username);
                    break;
                }
            }
            write.println(c.encrypt("end"));
            GuiServer.m.remove(this.username);
        }
        catch (Exception e)
        {
            GuiServer.m.remove(this.username);
            System.out.println(" Exception = " + e);
        }
        /*end of Reading data */
    }
}
