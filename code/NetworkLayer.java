
public class NetworkLayer
{

    private LinkLayer linkLayer;
    private static int propagation_delay, transmission_delay;

    public NetworkLayer(boolean server, int propagation_delay, int transmission_delay)
    {
        linkLayer = new LinkLayer(server);
        this.propagation_delay = propagation_delay;
        this.transmission_delay = transmission_delay;

    }
    
    public void send(byte[] payload)
    {

        try {



            Thread.sleep(propagation_delay + (transmission_delay * payload.length));
        }catch (Exception e){
            System.out.println("Sleep error line 26/27 1");
        }
        linkLayer.send( payload );
    }

    public byte[] receive()
    {

        byte[] payload = linkLayer.receive();
        try {
                if(payload == null) return payload;


        }catch (Exception e){
        }
        return payload;
    }
}
