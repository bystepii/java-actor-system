package remote;

import com.googlecode.jsonrpc4j.JsonRpcServer;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * This class is a servlet that handles all JSON-RPC requests on the "/jsonrpc" endpoint.
 */
@WebServlet(name = "ActorSystemJsonRpcServlet", urlPatterns = {"/jsonrpc"})
public class ActorSystemJsonRpcServlet extends HttpServlet {

    /**
     * The JSON-RPC server instance.
     */
    private JsonRpcServer jsonRpcServer;

    /**
     * Default constructor.
     */
    public ActorSystemJsonRpcServlet() {

    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        jsonRpcServer.handle(req, resp);
    }

    public void init(ServletConfig config) {
        this.jsonRpcServer = new JsonRpcServer(new RpcApiController(), RpcApiController.class);
    }
}
