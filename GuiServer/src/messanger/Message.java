package messanger;

public class Message
{
    private String destination;
    private String str;
    private String source;

    public String getDestination()
    {
        return destination;
    }

    public String getStr()
    {
        return str;
    }

    public String getSource()
    {
        return source;
    }

    
    Message(String source, String str, String destination)
    {
        this.source = source;
        this.str = str;
        this.destination = destination;
    
    }

    
    @Override
    public String toString()
    {
        
        return   source + ": "+ str  + "";
    }

    public void setStr(String str)
    {
        this.str = str;
    }

}
