import java.lang.StringBuilder;
import java.util.NoSuchElementException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Class that generates HTTP message to both Client and Server
 */
public class HTTP
{
    protected static final String HOST ="../website_example";
    protected static final String HTTP ="HTTP/";
    protected String STATUS ="";
    protected boolean FILE_EXISTS =false;
    protected int STATUS_CODE;
    protected String DATA;

    String PROTOCOL;
    String LAST_MODIFIED_CLIENT="1";
    String LAST_MODIFIED_SERVER="2";

    /**
     * Constructor
     * @param String data - pass in data of the file
     */
    public HTTP(String data){
        this.DATA =data;
    }

    public String get_http_protocol(){
        return PROTOCOL;
    }

    public void set_http_protocol(String protocol){
        PROTOCOL =protocol;
    }
    
    /**
     * get current date to help build the request format
     */
    public String get_current_date(){
        Date now =new Date();
        SimpleDateFormat sdf =new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(now);
    }
    
    public void set_last_modified_client(String lastM){
        LAST_MODIFIED_CLIENT = lastM;
    }
    
    public String get_last_modified_client(){
        return LAST_MODIFIED_CLIENT;
    }

    public void set_last_modified_server(String lastM){
        LAST_MODIFIED_SERVER = lastM;
    }
    
    public String get_last_modified_server(){
        return LAST_MODIFIED_SERVER;
    }
    
    public void set_file_exists(boolean exists){
        FILE_EXISTS =exists;
    }
    
    public boolean get_file_exists(){
        return FILE_EXISTS;
    }
    
    /**
     * Build HTTP request message based on the file name 
     * @param String filename - pass in name of the file 
     */
    public String get_request(String filename){               
        StringBuilder sb =new StringBuilder();
        sb.append("GET /" +filename +" " +HTTP +get_http_protocol() +"\n");
        sb.append("Host: " +HOST +"\n");
        
        if(FILE_EXISTS==true){
            sb.append("If-Modified-Since: " +get_last_modified_client() +"\n");
        } else if(FILE_EXISTS==false){            
        }

        String request =sb.toString();
        //System.out.println("\n" +request +"\n");
        return request;
    }

    /**
     * Build HTTP response message based on the file name and HTTP protocol
     * @param String filename - file name
     * @param String proto - HTTP prototype
     */
    public String get_response(String filename, String proto){
        StringBuilder sb =new StringBuilder();
        check_file();
        if(FILE_EXISTS==true && STATUS_CODE==200){ // modified
            sb.append(HTTP+proto + " " + filename + " " +get_status_message(get_status_code()) +"\n");
            sb.append("Date: " +get_current_date() +"\n");
            sb.append("Server: Apache/1.3.0 (Unix)\n");
            sb.append("Last-Modified: " +LAST_MODIFIED_SERVER +"\n");
            sb.append("Content-Type: image/CLHT\n");
            sb.append("Data: \n\n" +DATA);
        } else if(FILE_EXISTS==true && STATUS_CODE==304){
            sb.append(HTTP+proto + " " +get_status_message(get_status_code()) +"\n");
            sb.append("Date: " +get_current_date() +"\n");
            sb.append("Server: Apache/1.3.0 (Unix)\n");
        } else{
            sb.append(HTTP+proto + " " +get_status_message(get_status_code()) +"\n");
            sb.append("Date: " +get_current_date() +"\n");
            sb.append("Server: Apache/1.3.0 (Unix)\n");
            sb.append("Last-Modified: " +get_last_modified_server() +"\n");
            sb.append("Content-Type: image/CLHT\n");
            sb.append("Data: \n\n" +DATA);
        }
       
        String response =sb.toString();
        //System.out.println("\n" +response +"\n");
        return response;
    }

    /**
     * Check if the file modified date in cache in client side and actual server matches or not
     */
    public int check_file(){
        System.out.println("jawnnn");
        System.out.println(get_last_modified_client());
        System.out.println(get_last_modified_server());

        if(get_last_modified_client().equals(get_last_modified_server())){
            set_status_code(304);
            System.out.println("STAUSSSSSSSSSSS");
        } else if(!get_last_modified_client().equals(get_last_modified_server()) || get_file_exists()==false){
            set_status_code(200);
        } else{
            set_status_code(404);
        }
        return get_status_code();
    }

    public void set_status_code(int i){
        STATUS_CODE =i;
    }
    
    public int get_status_code(){
        return STATUS_CODE;
    }
    
    /**
     * Return status message based on status code
     * @param int sc - status code
     */
    public String get_status_message(int sc){
        String msg ="";
        if(sc==200){
            msg ="200 OK"; //Contents changed since last visit            
        } else if(sc==304){ //304
            msg ="Page 304. Not Modified";
        }  else{
            msg ="Error 404. File Not Found";
        }
        //System.out.println("status message: " +msg);
        return msg;
    }

    public String get_host(){
        return HOST;
    }


    public String get_data(){
        return DATA;
    }
}
