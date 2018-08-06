
public class TransportLayer
{

    private NetworkLayer networkLayer;
    private  int propagation_delay, transmission_delay;
    private static boolean persistant = false;
    private boolean handshake=false, connected=false;
    private byte[] byteArray;

    //Server is true if the application is a server (should listen) or false if it is a client (should try and connect)
    public TransportLayer(boolean server, int propagation_delay, int transmission_delay)
    {
        networkLayer = new NetworkLayer(server,propagation_delay,transmission_delay);
        this.propagation_delay = propagation_delay;
        this.transmission_delay = transmission_delay;
    }

    //Code only holds with the assumption that the server is started up first.
    public TransportLayer(boolean server)
    {
        networkLayer = new NetworkLayer(server,propagation_delay,transmission_delay);
    }

    public void enablePersistentProtocol()
    {
        persistant=true;

    }
    //Sends the data after doing handhake procedures if needed
    //and resets the connection boolean if needed.
    public void send(byte[] payload)
    {
        handshake(null);
        networkLayer.send( payload );

        if(persistant==false) {
            handshake=false;
            connected=false;
        }
    }

    //Receives data from networklayer and runs handshake method to
    //proceed with the next required step.
    public byte[] receive()
    {
        byte[] payload = networkLayer.receive();
        handshake(payload);    
        return payload;
    }


    //Checks whether connected and connection type. 
    //Checks if given input signifies a handshake term.
    //Given the input and booleans it follows one of the 3 cases.
    //Sending syn when connections has not been established.
    //Sending ack when a connection request has been made. 
    //Setting connection to true when connection request confirmed. 
    private void handshake(byte[] payload)
    {
        if(persistant==false || connected ==false){
        if(handshake==false){


            if(payload != null){
                String sload = new String (payload);
                 if(sload.equals("syn"))
                  {
                    sendAck();
                }
                else if(sload.equals("ack"))
                  {
                    handshake=true;
                    connected=true;
                }

                }
                
        }
            else{
                sendSyn();
        }
    }
    }

    

    private void sendAck()
    {
              String line = "ack";
    byteArray = line.getBytes();
    send(byteArray);
                handshake = true; 
                connected=true;

    }

    //Sends syn message and waits till connection is established.
    private void sendSyn()
    {
              String line = "syn";
    byteArray = line.getBytes();
    send(byteArray);


     while( handshake==false )
        {
            byte[] byteArray = networkLayer.receive();
            if(byteArray!= null) handshake(byteArray);
    
    }
}
}
