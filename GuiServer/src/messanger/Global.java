package messanger;

import java.util.HashMap;

public class Global
{
    static HashMap drivers = new HashMap();
    static MessageQueue<Message> al = new MessageQueue<>();
}