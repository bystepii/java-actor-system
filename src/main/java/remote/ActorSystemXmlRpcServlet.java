package remote;


import org.apache.log4j.BasicConfigurator;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.server.PropertyHandlerMapping;
import org.apache.xmlrpc.server.XmlRpcHandlerMapping;
import org.apache.xmlrpc.webserver.XmlRpcServlet;
import remote.api.RpcApiController;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "ActorSystemXmlRpcServlet", urlPatterns = {"/xmlrpc"})
public class ActorSystemXmlRpcServlet extends XmlRpcServlet {
    static {
        BasicConfigurator.configure();
    }

    @Override
    protected XmlRpcHandlerMapping newXmlRpcHandlerMapping() throws XmlRpcException {
        PropertyHandlerMapping phm = new PropertyHandlerMapping();
        phm.addHandler("api", RpcApiController.class);
        return phm;
    }
}
