package messanger;

import java.io.*;
import java.net.Socket;
import java.sql.*;
import static messanger.GuiServer.table_name;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Login extends Thread
{
    Cryptographic c;
    private Socket soc;
    static Statement s;
    private String username = null;

    Login(Socket soc)
    {
        this.soc = soc;
        c=new Cryptographic();
    }

    public static Statement getS()
    {
        return s;
    }

    @Override
    public void run()
    {
        try
        {
            /*Creating login driver that is loading driver and db_url*/
            LoginDbDriver login = (LoginDbDriver) Global.drivers.get("login");
            table_name = login.getTable();
            /*end of Creating login driver that is loading driver and db_url*/

            /*Getting conncetion for login driver*/
            Class.forName(login.getDriver());
            Connection connect = DriverManager.getConnection(login.getDburl(), login.getUsername(), login.getPassword());
            Statement s = connect.createStatement();
            Login.s = s;
            System.out.println("Connection done");
            /*end of Getting conncetion for login driver*/

            BufferedReader read = null;
            PrintWriter write = null;
            int flag = 0;
            int adminflag = 0;
            String password = null;
            /*Creating read and write object*/
            try
            {
                read = new BufferedReader(new InputStreamReader(soc.getInputStream()));
                write = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true);
            }
            catch (IOException ex)
            {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
            }
            /*Finished Creating read and write object*/

            int asha = 0;
            /*Checking for whether user is correct user or not till the time user enters correct data */
            do
            {
                try
                {
                    String mode = read.readLine();
                    username = read.readLine();
                    password = read.readLine();

                    mode=c.decrypt(mode);
                    username=c.decrypt(username);
                    password=c.decrypt(password);
                    if (mode.equalsIgnoreCase("newuser"))
                    {
                        ResultSet rs = s.executeQuery("select * from " + table_name + " where name= '" + username + "'");
                        if (rs.next() == true)
                        {
                            write.println("notok");
                            asha = 0;
                            flag = 0;
                        }
                        else
                        {
                            asha = 1;
                            s.executeUpdate("insert into users values('" + username + "','" + password + "','" + "2" + "')");
                        }
                    }
                    else
                    {
                        asha = 1;
                    }
                    if (asha == 1)
                    {
                        ResultSet rs = s.executeQuery("select * from " + table_name + " where name= '" + username + "'" + "and password = '" + password + "'");

                        if (rs.next())
                        {
                            flag = 1;
                            write.println(c.encrypt("ok"));

                            Conv con = new Conv(soc, username);
                            Thread t = new Thread(con);
                            t.start();
                            GuiServer.m.put(username, write);
                        }
                        else
                        {
                            write.println(c.encrypt("notok"));
                            flag = 0;
                        }
                    }
                }
                catch (SQLException ex)
                {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (IOException ex)
                {
                    System.out.println("Client abruptly stopped in login = " + ex);
                    break;
                }
                catch (Exception ex)
                {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            while ((flag != 1) && (adminflag != 1));
            /*end for whether user is correct user or not since it reached here so is a correct user */

            /*Showing the users name in List */
            if (flag == 1)
            {
                /*Printing users*/
                try
                {

                    /* All this things are hard coded so change it when u get time*/
                    Class.forName("org.apache.derby.jdbc.ClientDriver");//driver class
//                    Class.forName(login.getDriver());
                    Connection userconnect = DriverManager.getConnection("jdbc:derby://localhost:1527/Friends", "nikhil", "123");
                    Statement userstatement = userconnect.createStatement();

                    /* All this things are hard coded so change it when u get time*/
                    write.println(c.encrypt("userstart"));
                    
                    ResultSet rs = userstatement.executeQuery("select * from " + "Friends " + " where username='" + username + "'");//friends is the table-name it is hard coded
                    while (rs.next())
                    {
                        write.println(c.encrypt(rs.getString("friend")));
                    }
                    write.println(c.encrypt("**ONLINE USERS ↓ **"));
                    for (String key : GuiServer.m.keySet())
                    {
                        write.println(c.encrypt(key));

                        PrintWriter p = GuiServer.m.get(key);
                        if (!p.equals(write))
                        {
                            p.println(c.encrypt("newuserstart"));
                            p.println(c.encrypt(username));
                            p.println(c.encrypt("newuserend"));
                        }
                    }
                    write.println(c.encrypt("userend"));
                    /*sending list of all the users*/
                    write.println(c.encrypt("allusersstart"));
                    ResultSet r = s.executeQuery("select * from " + table_name);
                    while (r.next())
                    {
                        write.println(c.encrypt(r.getString("name")));
                    }
                    write.println(c.encrypt("allusersends"));
                    /*end of sending list of all the users*/

                    //now writing if any friend request
                    if (new File("D:\\" + username + "fr.txt").exists())
                    {
                        write.println(c.encrypt("prstarts"));
                        BufferedReader br = new BufferedReader(new FileReader("D:\\" + username + "fr.txt"));
                        String temp = br.readLine();
                        while (temp != null)
                        {
                            write.println(temp);
                            temp = br.readLine();
                        }
                        write.println(c.encrypt("prends"));
                        br.close();
                        File f = new File("D:\\" + username + "fr.txt");
                        if (f.delete())
                        {
                            System.out.println("File deleted");
                        }
                        else
                        {
                            System.out.println("File not deleted");

                        }
                    }
                    else
                    {
                        write.println(c.encrypt("no pr requests"));
                        System.out.println("There is no friend requests");
                    }
                    //end of writing friend request
                }
                catch (ClassNotFoundException ex)
                {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
                /*ending Printing users*/

                /*Since the above data was received by the main thread on the client side so there was no need to encrypt it but 
                 now on the client side the conv thread has started to read data and conv thread decrypts the data so
                 it is necessary to encrypt the username before sending it to client
                 */
                write.println(c.encrypt(username));//Printing username that is the current user of the thread

                /*Sending old message that user had i.e sending offline messages*/
                try
                {
                    if (new File("D:\\" + username + ".txt").exists())
                    {
                        BufferedReader br = new BufferedReader(new FileReader("D:\\" + username + ".txt"));
                        String temp = br.readLine();
                        Message m = new Message("offline Message", "offline Message", username);
                        write.println(c.encrypt(m.toString()));
                        while (temp != null)
                        {
                            write.println(temp);
                            temp = br.readLine();
                        }
                        Message m1 = new Message("End of offline Message", "End of  offline Message", username);
                        write.println(c.encrypt(m1.toString()));
                        br.close();
                        File f = new File("D:\\" + username + ".txt");
                        if (f.delete())
                        {
                            System.out.println("File deleted");
                        }
                        else
                        {
                            System.out.println("File not deleted");

                        }
                    }
                }
                catch (IOException ex)
                {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
                catch (Exception ex)
                {
                    Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        catch (ClassNotFoundException ex)
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (SQLException ex)
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Login.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        /*Finished Creating read and write object*/
    }
}
