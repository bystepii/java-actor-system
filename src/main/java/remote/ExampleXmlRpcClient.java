package remote;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

/**
 * Example XML-RPC client that connects to the XML-RPC server.
 */
public class ExampleXmlRpcClient {

    /**
     * Default constructor.
     */
    public ExampleXmlRpcClient() {

    }

    /**
     * Main method.
     *
     * @param args the command line arguments.
     */
    public static void main(String[] args) {
        try {
            XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
            config.setServerURL(new URL("http://localhost:8080/xmlrpc"));

            XmlRpcClient client = new XmlRpcClient();
            config.setConnectionTimeout(60 * 1000);
            config.setReplyTimeout(60 * 1000);
            client.setConfig(config);

            @SuppressWarnings("unchecked")
            Map<String, Object> r1 = (Map<String, Object>) client.execute(
                    "api.spawnActor",
                    new Object[]{"hw", "HelloWorldActor"}
            );
            System.out.println(r1);

            @SuppressWarnings("unchecked")
            Map<String, Object> r2 = (Map<String, Object>) client.execute(
                    "api.spawnActor",
                    new Object[]{"hw2", "HelloWorldActor"}
            );
            System.out.println(r2);

            @SuppressWarnings("unchecked")
            Map<String, Object> names = (Map<String, Object>) client.execute(
                    "api.getNames",
                    new Object[]{});
            System.out.println(names);

            Object[] actorNames = (Object[]) names.get("result");

            System.out.println(Arrays.toString(actorNames));

            client.execute(
                    "api.send",
                    new Object[]{"hw", "Hello"}
            );

        } catch (XmlRpcException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
}
