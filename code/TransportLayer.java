
public class TransportLayer
{

    private NetworkLayer networkLayer;
    private static int propagation_delay, transmission_delay;
    private boolean persistant=false, handshake=false;

    //server is true if the application is a server (should listen) or false if it is a client (should try and connect)
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

    public void send(byte[] payload)
    {
        networkLayer.send( payload );
    }

    public byte[] receive()
    {
        byte[] payload = networkLayer.receive();    
        return payload;
    }

    private handshake(byte[] payload)
    {

        if(payload != null){
        String sload = new String (payload);
        if(sload.equals("ack"))
        {
            handshake = true;
        }
        }
        else{
            String line = "ack";
                byteArray = line.getBytes();
                send(byteArray);
        }
        

    }
}
