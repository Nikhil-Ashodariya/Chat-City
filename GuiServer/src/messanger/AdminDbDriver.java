package messanger;

public class AdminDbDriver
{
    private String driver;
    private String dburl;
    private String table;
    private String username;
    private String password;

    public AdminDbDriver()
    {
    }

    public String getDriver()
    {
        return driver;
    }

    public void setDriver(String driver)
    {
        this.driver = driver;
    }

    public String getDburl()
    {
        return dburl;
    }

    public void setDburl(String dburl)
    {
        this.dburl = dburl;
    }

    public String getTable()
    {
        return table;
    }

    public void setTable(String table)
    {
        this.table = table;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    @Override
    public String toString()
    {
        return "AdminDbDriver{" + "driver=" + driver + ", dburl=" + dburl + ", table=" + table + ", username=" + username + ", password=" + password + '}';
    }

}
