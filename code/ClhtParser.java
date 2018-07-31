import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


//Class is used inside the ServerApp class to retirve information from the local disk and returns the 
//file in a string for further packaging inside the ServerApp class
public class ClhtParser
{

    private ArrayList<String> constructedPage;
   
    

    public ClhtParser(){

    }

    public String retrieve_file(String requested_file)
    {
        try {

            constructedPage = new ArrayList<String>();


            BufferedReader reader = new BufferedReader(new FileReader("../website_example/"+requested_file));
            
            String line = reader.readLine();
            while (line != null) {

                constructedPage.add(line);
                line = reader.readLine();

            }
	    //builds the file into one giant string 
            StringBuilder builder = new StringBuilder();
            for(String s : constructedPage) {
                builder.append(s);
                builder.append(System.getProperty("line.separator"));
            }
            return builder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "ERROR 404";
    }



 
}
