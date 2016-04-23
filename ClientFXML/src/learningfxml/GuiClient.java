package learningfxml;

import java.io.*;
import java.net.*;

public class GuiClient
{
    private String username;
    private Socket soc;
    private BufferedReader nis;
    private PrintWriter nos;

    public GuiClient() throws IOException
    {
        soc = new Socket("127.0.0.1", 27015);//ur current ip address can be used to connect to a particular port number
        nis = new BufferedReader(new InputStreamReader(soc.getInputStream()));
        nos = new PrintWriter(new BufferedWriter(new OutputStreamWriter(soc.getOutputStream())), true);
    }

    public Socket getSoc()
    {
        return soc;
    }

    public BufferedReader getNis()
    {
        return nis;
    }

    public void setNis(BufferedReader nis)
    {
        this.nis = nis;
    }

    public PrintWriter getNos()
    {
        return nos;
    }

    public void setNos(PrintWriter nos)
    {
        this.nos = nos;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    
}