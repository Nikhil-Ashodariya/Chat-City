package messanger;

import java.io.*;
import java.sql.*;

public class AdminQuery extends Thread
{
    private BufferedReader read;
    private PrintWriter write;

    public AdminQuery(BufferedReader read, PrintWriter write)
    {
        this.read = read;
        this.write = write;
    }

    @Override
    public void run()
    {
        String str = null;
        do
        {
            try
            {

                String user1 = read.readLine();
                String user2 = read.readLine();
                MessageDbDriver msg = (MessageDbDriver) Global.drivers.get("message");
                Class.forName(msg.getDriver());
                Connection conn = DriverManager.getConnection(msg.getDburl(), msg.getUsername(), msg.getPassword());
                Statement s = conn.createStatement();
                ResultSet rs = s.executeQuery("select * from " + msg.getTable() + " where source = '" + user1 + "' and destination = '" + user2 + "'");
                while (rs.next())
                {
                    String source = rs.getString("source");
                    String destination = rs.getString("destination");
                    String message = rs.getString("message");
                    String date = rs.getString("date");
                    write.println("Source =  " + source + "  message =   " + message + "   Destination =   " + destination + "   Date =   " + date);
                }
                write.println("end");

                rs = s.executeQuery("select * from " + msg.getTable() + " where source = '" + user2 + "' and destination = '" + user1 + "'");

                while (rs.next())
                {
                    String source = rs.getString("source");
                    String destination = rs.getString("destination");
                    String message = rs.getString("message");
                    String date = rs.getString("date");
                    write.println("Source =  " + source + "  message =   " + message + "   Destination =   " + destination + "   Date =   " + date);
                }
                write.println("end");
            }

            catch (IOException ex)
            {
                System.out.println("Error in reading the admin users  =  " + ex);
            }
            catch (ClassNotFoundException ex)
            {
                System.out.println("Error in loading the class = " + ex);
            }
            catch (SQLException ex)
            {
                System.out.println("Error in getting Connection = " + ex);
            }

            try
            {
                str = read.readLine();//check if other Query Came
            }
            catch (IOException ex)
            {
                System.out.println("Connection in while admin query reset  =  " + ex);
                break;
            }
        } while (!str.equalsIgnoreCase("query ended"));
    }
}