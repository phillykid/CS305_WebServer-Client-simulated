import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.TimeZone;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.text.SimpleDateFormat;




//Launches as java SeverApp "1"(propagation_delay) "1"(transmission_delay)
public class ServerApp
{
    private boolean connected = false, persistent = false;
    private static int dprop;
    private static int dtrans;
    private String HTTP_PROTOCOL;

    /**
     * main
     * @param args[0] - propagation delay 
     * @param args[1] - transmission delay
     */
    public static void main(String[] args) throws Exception
    {
        dprop =Integer.parseInt(args[0]);
        dtrans =Integer.parseInt(args[1]);
        ServerApp serv = new ServerApp(dprop, dtrans);
    }

    /**
     * retrieve file last modified date directly from the folder/file
     * @param String requested_file - name of the file
     */
    public String get_last_modified(String requested_file){
        File file =new File("../website_example/"+requested_file);     
        SimpleDateFormat sdf =new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        String d =sdf.format(file.lastModified());
        return d;
    }

    public void set_http_protocol(String protocol){
        HTTP_PROTOCOL =protocol;
    }

    public String get_http_protocol(){
        return HTTP_PROTOCOL;
    }

    public ServerApp(int propagation_delay, int transmission_delay)
    {

        try{
        //create a new transport layer for server (hence true) (wait for client)
        ClhtParser parser = new ClhtParser();
        TransportLayer transportLayer = new TransportLayer(true, propagation_delay, transmission_delay);

        while( true )
        {
            //receive message from client, and send the "received" message back.
            byte[] byteArray = transportLayer.receive();

            //if client disconnected
            if(byteArray==null)
                break;

            String str3 = new String ( byteArray );

	
                String res = new String(byteArray); //HTTP message from client
                HTTPParser hp =new HTTPParser();
                hp.parse_request_message(res);
                String str =hp.get_file_name();
                if(HTTP_PROTOCOL == null) set_http_protocol(hp.get_protocol()); //Server being informed of the requested connection type

                String file = parser.retrieve_file(str);
                HTTP http =new HTTP(file);

                
                byteArray = file.getBytes();
                http.set_last_modified_server(get_last_modified(str));
                transportLayer.send(http.get_response(file,HTTP_PROTOCOL).getBytes());
                if(HTTP_PROTOCOL.equals("1.0")) connected = false; //Disconnects after sending if a non-persistent connection 
            }
        }catch(Exception inserver)
        {
                System.out.println("Please close all opened terminals and try again(error is being worked on)");
                System.out.println(inserver);
        }
        
    }

}
