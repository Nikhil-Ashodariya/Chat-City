package messanger;

import java.util.ArrayList;

public class MessageQueue<T>
{
    private ArrayList<T> msg = new ArrayList<>();

    synchronized public void push(T m)
    {
        msg.add(m);
        notify();//it tells that the push has happen and the threads which are waiting can resume their execution
    }

    synchronized public T pop()
    {
        if (msg.isEmpty())
        {
            try
            {
                wait();//it waits till no one notifys that is it no tells that something as happen and wakes this thread
            }
            catch (InterruptedException ex)
            {
                System.out.println(ex);
            }
        }
        return msg.remove(0);//since this thread will reach here only when data is there in the arraylist or if data is not there then it will wait
    }
    synchronized public int getNumberOfMsg()
    {
        return msg.size();
    }
}
