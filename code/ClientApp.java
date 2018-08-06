import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;


//Run with java ClientApp false 1.1 in terminal or
//java ClientApp true 1.1 animals.clht
// True for auto and false for interactive


//This class represents the client application
public class ClientApp {
    private TransportLayer transportLayer = new TransportLayer(false);
    private BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    private ClientPageBuilder cbuilder = new ClientPageBuilder();
    private String line;


    //<File Name, Entry<Last Modified, File Contents>
    HashMap<String, File> cache =new HashMap<String, File>();

    public class File{
        private String last_mod;
        private String data;
    private File(String last_modified, String data)
    {
        this.last_mod=last_modified;
        this.data=data;
    }

    public String getMod()
    {
        return last_mod;
    }
    public String getData()
    {
        return data;
    }
}

    private String HTTP_PROTOCOL;
    private Boolean MODE;
    private String EXPR_WEBSITE;

    private String FILE_NAME;
    private String DATA;

    private String COMMAND_MESSAGE = "Please enter the file you wish to request from server ie. animals.clht\n";
    private String LINK_SELECTION_MESSAGE = "Enter the number of the link you want to select or q to exit";



    /**
     * main
     * @param args[0] - "true" (experiment mode) "false" (interactive mode)
     * @param args[1] - tcp connection type - "1.1" (persistent) "1.0" (non-persistent)
     * @param args[2] - name of the file (ex."animals.clht" )
     */
    public static void main(String[] args) throws Exception {

        if(args[0].equals("true"))
        {
         new ClientApp(args[0],args[1],args[2]); //Experiment mode constructor 
        } else{
            new ClientApp(args[0],args[1]); //Interactive mode constructor
        }

    }

    /**
     * Retrieve file modified date from cache through file name and actual data
     * @param String fn - filname
     * @param String data -content of file
     */
    public String get_modified(String fn){
        String mod =cache.get(fn).getMod();
        return mod;
    }

    /**
     * add file, content of the file, and date of last modified to cache
     * @param String fn - file name
     * @param String data - content of the file
     * @param String lastMod - file last modified date
     */
    public void add_to_cache(String fn, String data, String lastMod){
        cache.put(fn,new File(lastMod,data));
    }

    public boolean check_cache(String fn){
        if(cache.containsKey(fn)) return true;
        else return false;
    }

    public void set_http_protocol(String protocol){
        HTTP_PROTOCOL =protocol;
        if(protocol.equals("1.1")) transportLayer.enablePersistentProtocol();
    }

    public String get_http_protocol(){
        return HTTP_PROTOCOL;
    }

    public ClientApp(String mode, String protocol){
        set_http_protocol(protocol);
        MODE = Boolean.parseBoolean(mode);
        System.out.print(COMMAND_MESSAGE);

        try {
            line = reader.readLine();
            while (line != null && !line.equals("")) {
                //convert lines into byte array, send to transport layer and wait for response

                send_data(line);

                request_and_build_page(wait_for_data());

                //read next line
                line = reader.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ClientApp(String mode, String protocol, String website){
        set_http_protocol(protocol);
        MODE = Boolean.parseBoolean(mode);
        EXPR_WEBSITE = website;

        try {

            long startTime = System.nanoTime();

            send_data(website);

            request_and_build_page(wait_for_data());
            long timeTaken = TimeUnit.MILLISECONDS.convert(System.nanoTime()-startTime,TimeUnit.NANOSECONDS);

            System.out.println("___________________\n"
                +"\nTime Taken: " +timeTaken +" ms\n"
                +"___________________\n");
            //read next line
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


	//Waits for the required response from user and triggers a related action
    public int wait_for_response() {
        System.out.println(LINK_SELECTION_MESSAGE);
        try {
            line = reader.readLine();

            while (line != null && !line.equals("")) {

                byte[] byteArray = line.getBytes();
                String response = new String(byteArray);

                if (response.equals("q")) System.exit(0);

                return Integer.parseInt(response);

            }
            line = reader.readLine();

        } catch (IOException e) {
            System.out.print("Please enter valid respone.");
        }
        return 0;
    }

    public String request_linked_page(int link_number)
    {
        send_data(cbuilder.retrieve_link_path(link_number));
        return wait_for_data();
    }

   
    
    /**
     * Send HTTP request message to Server through transport layer
     * @param: data_to_send - name of the file
     */
    public void send_data(String data_to_send) 
    {
        HTTP http =new HTTP(data_to_send);
        http.set_http_protocol(HTTP_PROTOCOL);

        FILE_NAME =data_to_send;
        if(check_cache(data_to_send)==true){ 
            http.set_file_exists(true); 
            http.set_last_modified_client(get_modified(FILE_NAME));


        }
        else http.set_file_exists(false);

        transportLayer.send(http.get_request(data_to_send).getBytes());
    }

    /**
     * Receive response from Server 
     * Also check if the file is in cache. If it is not, add the file and data, modified date to cache,
     * otherwise, do nothing
     */
    public String wait_for_data() {
        while (true) {
            byte[]  byteArray = transportLayer.receive();

            if (byteArray != null) {
                String a =new String(byteArray);

                    HTTPParser parser =new HTTPParser();
                    parser.parse_response_message(a);

                    DATA =parser.get_data();





                    if(check_cache(FILE_NAME)==false){
                        add_to_cache(FILE_NAME, parser.get_data(), parser.get_last_modified());
                    
                    } 
                    else if(parser.get_status().equals("304"))  
                    {

                        return cache.get(FILE_NAME).getData();
                    }    

                    return DATA;
                
            }
        }
    }

    //Takes in the data string from the ServerApp and builds the web page while requesting the
    //embedded components and afterwards depending on mode ask for futher user interaction.	
    public void request_and_build_page(String page_file)
    {
        cbuilder.reset_page_builder();
        cbuilder.parse_file(page_file);

        
        if (!cbuilder.getEmbeddedImages().isEmpty()) {
	//If there are missing components
            for (String img : cbuilder.getEmbeddedImages()) {

                send_data(img);



                cbuilder.insert_embedded_image(wait_for_data());

            }
        }
        System.out.println(cbuilder.buildPage());

        if (MODE == false) {
            request_and_build_page(request_linked_page(wait_for_response()));

        }
    }

}
