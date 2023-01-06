package remote;


import org.apache.log4j.BasicConfigurator;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;

import javax.servlet.annotation.WebServlet;

/**
 * This class is a servlet that handles all XML-RPC requests on the "/xmlrpc" endpoint.
 */
@WebServlet(name = "ActorSystemXmlRpcServlet", urlPatterns = {"/xmlrpc"})
public class ActorSystemXmlRpcServlet extends XmlRpcServlet {

    static {
        BasicConfigurator.configure();
    }

    /**
     * Default constructor.
     */
    public ActorSystemXmlRpcServlet() {

    }

    /**
     * Define the handler mapping for the XML-RPC server.
     *
     * @return the handler mapping.
     * @throws XmlRpcException if an error occurs.
     */
    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        phm.addHandler("api", RpcApiController.class);
        return phm;
    }
}
