package messanger;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;

/*This class parse the XML document whose path is hard coded and it takes the driver from the file*/
public class Handler
{

    static void parse(String path) throws Exception
    {
        /*Creates the object to store the drivers*/
        LoginDbDriver logindriver = new LoginDbDriver();
        AdsDbDriver adsdriver = new AdsDbDriver();
        MessageDbDriver messagedriver = new MessageDbDriver();
        AdminDbDriver admindriver = new AdminDbDriver();
        /*end of Creating the object to store the drivers*/

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();//gets DOM instance
        DocumentBuilder db = dbf.newDocumentBuilder();//gets Document Reader type of object
        Document doc = db.parse(new File(path));//parse the document and returns the top node
        /*
         0 this is node given by parse function (level 0)
         |
         0 this is root node present in the document  (level 1)
         /\
         0 0 this are sub nodes in the document    (level 2)
         */
        NodeList nl = doc.getChildNodes();//gives the address of the node at level 1
        ArrayList<Node> nodeset = new ArrayList<>(); //Array List to add the node in list
        nodeset.add(nl.item(0));//adds the node that is at level 1
        while (nodeset.size() > 0)//it acts as queue all the elements are pushed the back and poped from the front
        {
            Node n = nodeset.remove(0);
            if (n.hasChildNodes())
            {
                NodeList temp = n.getChildNodes();//it returns the list of  all the child node if present
                for (int j = 0; j < temp.getLength(); j++)
                {
                    nodeset.add(temp.item(j));
                }

                String content = n.getTextContent();
                String name = n.getNodeName();
                System.out.println("name = " + name);
                System.out.println("content = " + content);

                //loading Ads Driver
                if (name.equalsIgnoreCase("adsdbdriver"))
                {
                    adsdriver.setDriver(content);
                }

                if (name.equalsIgnoreCase("adsdburl"))
                {
                    adsdriver.setDburl(content);
                }

                if (name.equalsIgnoreCase("adsdbtablename"))
                {
                    adsdriver.setTable(content);
                }

                if (name.equalsIgnoreCase("adsdbusername"))
                {
                    adsdriver.setUsername(content);
                }

                if (name.equalsIgnoreCase("adsdbpassword"))
                {
                    adsdriver.setPassword(content);
                }
                //end of loading Ads Driver

                //Loding of Message Driver
                if (name.equalsIgnoreCase("msgdbdriver"))
                {
                    messagedriver.setDriver(content);
                }

                if (name.equalsIgnoreCase("msgdburl"))
                {
                    messagedriver.setDburl(content);
                }

                if (name.equalsIgnoreCase("msgdbtablename"))
                {
                    messagedriver.setTable(content);
                }

                if (name.equalsIgnoreCase("msgdbusername"))
                {
                    messagedriver.setUsername(content);
                }

                if (name.equalsIgnoreCase("msgdbpassword"))
                {
                    messagedriver.setPassword(content);
                }
                //end of Loding of Message Driver

                //loding the login driver here
                if (name.equalsIgnoreCase("logindbdriver"))
                {
                    logindriver.setDriver(content);
                }

                if (name.equalsIgnoreCase("logindburl"))
                {
                    logindriver.setDburl(content);
                }

                if (name.equalsIgnoreCase("logindbtablename"))
                {
                    logindriver.setTable(content);
                }

                if (name.equalsIgnoreCase("logindbusername"))
                {
                    logindriver.setUsername(content);
                }

                if (name.equalsIgnoreCase("logindbpassword"))
                {
                    logindriver.setPassword(content);
                }
                //end of loding the login driver here

                //loding admin driver
                if (name.equalsIgnoreCase("admindbdriver"))
                {
                    admindriver.setDriver(content);
                }

                if (name.equalsIgnoreCase("admindburl"))
                {
                    admindriver.setDburl(content);
                }

                if (name.equalsIgnoreCase("admindbtablename"))
                {
                    admindriver.setTable(content);
                }

                if (name.equalsIgnoreCase("admindbusername"))
                {
                    admindriver.setUsername(content);
                }

                if (name.equalsIgnoreCase("admindbpassword"))
                {
                    admindriver.setPassword(content);
                }
                //end of loding the admin driver here

                //end of loding the driver here
                /*This is used if attributes are present */
//                NamedNodeMap nnm = n.getAttributes();
//                if (nnm != null)
//                {
//                    for (int i = 0; i < nnm.getLength(); i++)
//                    {
//                        System.out.println("Name = " + nnm.item(i).getNodeName());
//                        System.out.println("Context = " + nnm.item(i).getTextContent());
//                    }
//                }
//                else
//                {
//                    System.out.println("no Attribute ");
//                }
            }
        }

        /*Adding drivers to hashmap so that they can be Retrieved from any where in the program */
        Global.drivers.put("ads", adsdriver);
        Global.drivers.put("login", logindriver);
        Global.drivers.put("message", messagedriver);
        Global.drivers.put("admin", admindriver);
    }
}