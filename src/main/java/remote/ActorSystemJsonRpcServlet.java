package remote;

import com.googlecode.jsonrpc4j.JsonRpcServer;
import remote.api.RpcApiController;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ActorSystemJsonRpcServlet", urlPatterns = {"/jsonrpc"})
public class ActorSystemJsonRpcServlet extends HttpServlet {
    private JsonRpcServer jsonRpcServer;

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        jsonRpcServer.handle(req, resp);
    }

    public void init(ServletConfig config) {
        this.jsonRpcServer = new JsonRpcServer(new RpcApiController(), RpcApiController.class);
    }
}
