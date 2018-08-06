import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import java.util.ArrayList;


public class HTTPParser
{
    protected String FILE_NAME;
    protected String HTTP;
    protected String PROTOCOL;
    protected String STATUS;
    protected String HOST;
    protected String IF_MODIFIED_SINCE;
    protected int IF_MODIFIED_SUBSTRING=20; //where to retain string from
    protected String LAST_MODIFIED; 
    protected String DATE, CONTENT_TYPE;
    protected String DATA;

    /**
     * Parse HTTP request message on Server side
     * @param String msg - whole chunk of message from Client
     */
    public void parse_request_message(String msg){
        try{
            Scanner sc =new Scanner(new StringReader(msg));
            while(sc.hasNextLine()){
                try{


                    String line =sc.nextLine();
                    Scanner lsc =new Scanner(line);
                    if(lsc.hasNext()){
                        String firstWord =lsc.next();
                        switch(firstWord){
                            case "GET":
                            {
                                FILE_NAME =lsc.next().substring(1); //file name
                                String tmp =lsc.next();
                                String tmpArray[] =tmp.split("/");
                                HTTP =tmpArray[0];
                                PROTOCOL =tmpArray[1]; //protocol type
                                break;
                            }
                            case "Host:":
                            {
                                HOST =lsc.next();
                                break;
                            }           
                            case "If-Modified-Since:":
                            {
                
                                
                                    IF_MODIFIED_SINCE=line.substring(IF_MODIFIED_SUBSTRING);
                                
                        

                                break;
                            }                       
                        }                 
                    }
                } catch(NoSuchElementException n){
                    System.out.println("HTTP Request message corrupted.");
                }
            }
            sc.close();
        } catch(Exception e){
            System.out.println("HTTP message not found!!!!");
        }

    }

    public String get_protocol()
    {
        return PROTOCOL;
    }
    
    /**
     * Parse HTTP response message on Client side
     * @param String msg - whole chunk of message from Server
     */
    public void parse_response_message(String msg){
        try{

            Scanner sc =new Scanner(new StringReader(msg));
            while(sc.hasNextLine()){
                try{
                    String line =sc.nextLine();
                    Scanner lsc =new Scanner(line);
                    if(lsc.hasNext()){
                        String firstWord =lsc.next();
                        switch(firstWord){
                            case "HTTP/1.1":
                            {
                                STATUS =lsc.next();
                                break;
                            }
                            case "HTTP/1.0":
                            {
                                STATUS =lsc.next();
                                break;
                            }
                            case "Date:": 
                            {
                                DATE =line.substring(6);                                
                                break;
                            }                             
                            case "Server:":
                            {
                                String server =lsc.next();  
                                break;
                            }   
                            case "Last-Modified:":
                            {
                                LAST_MODIFIED =line.substring(14);
                                line =sc.nextLine();
                                break;
                            }
                            case "Content-Type:":
                            {
                                String content_type =lsc.next();
                                break;
                            }
                            case "Data:":
                            {
                                


                                DATA =line.substring(6)+"\n";
                                line =sc.nextLine();
                                while(sc.hasNextLine())
                                {
                                    DATA+=line+"\n";                                   
                                    line=sc.nextLine();
                                }
                                DATA+=line+"\n";
                                break;
                            }
                        }                 
                    }
                } catch(NoSuchElementException n){
                    System.out.println("HTTP Response message corrupted.");
                }
            }
            sc.close();
        } catch(Exception e){
            System.out.println("HTTP message not found.");
        }
    }
    
    public String get_data(){ 
        return DATA;
    }
    
    public String get_last_modified(){ 
        return LAST_MODIFIED;
    }

    public String get_modified_since(){ 
        return IF_MODIFIED_SINCE;
    }
    
    public String get_file_name(){
        return FILE_NAME;
    }

    public String get_status(){
        return STATUS;
    }

}
