package messages;

import actors.ActorRef;

public class MethodInvocationMessage extends Message<String> {
    private final Object[] args;

    public MethodInvocationMessage(ActorRef sender, String senderName, String methodName, Object[] args) {
        super(sender, senderName, methodName);
        this.args = args;
    }

    public MethodInvocationMessage(String methodName, Object[] args) {
        super(methodName);
        this.args = args;
    }

    public MethodInvocationMessage(String methodName) {
        this(methodName, null);
    }

    public Object[] getArgs() {
        return args;
    }

    public String getMethodName() {
        return getBody();
    }
}
