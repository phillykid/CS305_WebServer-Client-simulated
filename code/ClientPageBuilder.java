import java.io.BufferedReader;
import java.io.StringReader;
import java.io.IOException;
import java.util.ArrayList;



//Used in the ClientApp class to maintain the necessary components of the webpage and also puts 
//the page together using the compenents to be displayed by the ClientApp. 
public class ClientPageBuilder {
    private ArrayList<String> linkedPages = new ArrayList<String>();
    private ArrayList<String> constructedPage = new ArrayList<String>();
    private ArrayList<String> embeddedImages = new ArrayList<String>();

    private int lineTracker = 0;
    private int linkTracker = 0;

    public ClientPageBuilder()
    {

    }

	//Used to empty out the data structures and variables when a new page is requested
	//so as not to get any data collisions and overwrites 
    public void reset_page_builder()
    {
        linkedPages = new ArrayList<String>();
        constructedPage = new ArrayList<String>();
        embeddedImages = new ArrayList<String>();
        lineTracker = 0;
        linkTracker = 0;
    }
	//Pareses the string that is served up from the ServerApp using the guidelines 
	//provided in the project spec for .clht files.
    public void parse_file(String clht_file) {
        try {


            BufferedReader reader = new BufferedReader(new StringReader(clht_file));
            String line = reader.readLine();
            while (line != null) {
                if (line.contains("href")) {
                    String[] broken_down_by_spaces = line.split("\\s+");
                    constructedPage.add(linkTracker + 1 +". "+ broken_down_by_spaces[3]);
                    linkedPages.add(broken_down_by_spaces[2]);
                    line = reader.readLine();
                    lineTracker++;
                    linkTracker++;

                } else if (line.contains("img")) {

                    String[] broken_down_by_spaces = line.split("\\s+");
                    embeddedImages.add(broken_down_by_spaces[2]);

                    constructedPage.add("IMG");
                    line = reader.readLine();
                    lineTracker++;

                } else {

                    constructedPage.add(line);
                    line = reader.readLine();
                    lineTracker++;

                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public ArrayList<String> getEmbeddedImages() {
        return embeddedImages;
    }

	//Generates a string model of the page using the necessary components.
    public String buildPage()
    {
        StringBuilder builder = new StringBuilder();
        for (String s : constructedPage) {
            builder.append(s);
            builder.append(System.getProperty("line.separator"));
        }
        String str = builder.toString();

        return str;
    }

	//Inserts the supplied image in its correct posistion in the page array.
    public void insert_embedded_image(String image)
    {


            for (int i = 0; i<constructedPage.size(); i++) {

            if(constructedPage.get(i).contains("IMG"))
            {
                constructedPage.set(i,image);
                return;
            }
        }
    }

	//Locates the page at the requested link number and provides it to the
	//ClientApp for requesting purposes. 
    public String retrieve_link_path(int link_number)
    {
        return linkedPages.get(link_number-1);
    }
}
