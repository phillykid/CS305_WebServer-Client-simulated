import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.TimeZone;
import java.util.Map;
import java.util.HashMap;
import java.io.File;
import java.text.SimpleDateFormat;



//Run from terminal using [java Server "number" "number"]
//Launches as java SeverApp "1"(propagation_delay) "1"(transmission_delay)
public class Server {
 private static int dprop;
 private static int dtrans;
 private String HTTP_PROTOCOL;


 /**
  * main
  * @param args[0] - propagation delay 
  * @param args[1] - transmission delay
  */
 public static void main(String[] args) throws Exception {
  dprop = Integer.parseInt(args[0]);
  dtrans = Integer.parseInt(args[1]);
  new Server(dprop, dtrans);
 }

 /**
  * retrieve file last modified date directly from the folder/file
  * @param String requested_file - name of the file
  */
 public String get_last_modified(String requested_file) {
  File file = new File("../website_example/" + requested_file);
  SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z");
  sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
  String d = sdf.format(file.lastModified());
  return d;
 }

 public void set_http_protocol(String protocol) {
  HTTP_PROTOCOL = protocol;
 }

 public String get_http_protocol() {
  return HTTP_PROTOCOL;
 }

 public Server(int propagation_delay, int transmission_delay) {

  try {
   //create a new transport layer for server (hence true) (wait for client)
   ClhtParser parser = new ClhtParser();
   TransportLayer transportLayer = new TransportLayer(true, propagation_delay, transmission_delay);

   System.out.println("Server Running......");


   while (true) {



    //receive message from client, and send the "received" message back.
    byte[] byteArray = transportLayer.receive();

    //if client disconnected
    if (byteArray == null)
     break;

    String str3 = new String(byteArray);


    String res = new String(byteArray); //HTTP message from client
    HTTPParser hp = new HTTPParser();
    hp.parse_request_message(res);
    String str = hp.get_file_name();
    if (HTTP_PROTOCOL == null) {
     set_http_protocol(hp.get_protocol()); //Server being informed of the requested connection type
     if (HTTP_PROTOCOL.equals("1.1")) transportLayer.enablePersistentProtocol();

    }

    String file;
    HTTPModule http;

    if (hp.get_modified_since() != null) {



    }

    if (hp.get_modified_since() != null &&
     hp.get_modified_since().equals(get_last_modified(str))) {

     http = new HTTP(null);
     http.set_last_modified_server(get_last_modified(str));
     http.set_last_modified_client(hp.get_modified_since());

     transportLayer.send(http.get_response(null, HTTP_PROTOCOL).getBytes());


    } else {
     file = parser.retrieve_file(str);
     http = new HTTPModule(file);
     http.set_last_modified_server(get_last_modified(str));

     byteArray = file.getBytes();
     transportLayer.send(http.get_response(file, HTTP_PROTOCOL).getBytes());
    }





   }
  } catch (Exception inserver) {
   System.out.println("Please close all opened terminals and try again(error is being worked on)");
   System.out.println(inserver);
  }

 }

}