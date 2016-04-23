package messanger;

public class LoginDbDriver
{

    private String driver;
    private String dburl;
    private String table;
    private String username;
    private String password;

    public LoginDbDriver()
    {
    }

    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    public void setDburl(String dburl)
    {
        this.dburl = dburl;
    }

    public void setTable(String table)
    {
        this.table = table;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getDriver()
    {
        return driver;
    }

    public String getDburl()
    {
        return dburl;
    }

    public String getTable()
    {
        return table;
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    @Override
    public String toString()
    {
        return "LoginDbDriver{" + "driver=" + driver + ", dburl=" + dburl + ", table=" + table + ", username=" + username + ", password=" + password + '}';
    }

}
