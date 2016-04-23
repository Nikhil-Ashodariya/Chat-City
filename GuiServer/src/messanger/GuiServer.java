package messanger;

import java.io.PrintWriter;
import java.net.*;
import java.util.HashMap;

public class GuiServer
{

    static String table_name = new String();
    static HashMap<String,PrintWriter> m = new HashMap();

    public static void main(String[] args) throws Exception
    {
        Handler.parse("DataBaseOnServer.xml");

        /*Turning MessageDispatcher on */
        MessageDispatcher md = new MessageDispatcher();
        md.setDaemon(true);
        md.start();
        /*end of Turning MessageDispatcher on */

        System.out.println("Server Signing on");

        ServerSocket ss = new ServerSocket(27015);//creating a Server Socket object that is making this program a server

        for (int i = 0; i < 100; i++)
        {
            Socket soc = ss.accept();

            /*ip filteration is not possible because to do ip filteration we should know port forward*/
//            String temp = soc.getRemoteSocketAddress().toString();
//            if (temp.equalsIgnoreCase("192.0.0.1"))
//            {
//                System.out.println("this is the ip");
//            }
            Login l = new Login(soc);
            l.start();
            System.out.println("Server Says Connection Established");
        }
        //cannot Accept more Socket object i.e. no more client
        System.out.println("Server Signing Off");
    }
}
