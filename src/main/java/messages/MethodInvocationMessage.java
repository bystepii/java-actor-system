package messages;

import actors.ActorRef;

/**
 * This class represents a method invocation message that can be sent to an Actor.
 * <p>
 * It is used by {@link actors.DynamicProxy} and {@link actors.ReflectiveActor} to
 * send method invocations to an Actor.
 *
 * @see actors.DynamicProxy
 * @see actors.ReflectiveActor
 */
public class MethodInvocationMessage extends Message<String> {

    /**
     * The arguments of the method invocation.
     */
    private final Object[] args;

    /**
     * Default constructor.
     *
     * @param sender     the Actor that sent the message.
     * @param senderName the name of the Actor that sent the message.
     * @param methodName the name of the method to invoke.
     * @param args       the arguments of the method invocation.
     */
    public MethodInvocationMessage(ActorRef sender, String senderName, String methodName, Object[] args) {
        super(sender, senderName, methodName);
        this.args = args;
    }

    /**
     * Constructor with empty sender and empty senderName.
     *
     * @param methodName the name of the method to invoke.
     * @param args       the arguments of the method invocation.
     */
    public MethodInvocationMessage(String methodName, Object[] args) {
        super(methodName);
        this.args = args;
    }

    /**
     * Constructor with only the method name.
     *
     * @param methodName the name of the method to invoke.
     */
    public MethodInvocationMessage(String methodName) {
        this(methodName, null);
    }

    /**
     * Getter for the arguments of the method invocation.
     *
     * @return the arguments of the method invocation.
     */
    public Object[] getArgs() {
        return args;
    }

    /**
     * Getter for the name of the method to invoke.
     *
     * @return the name of the method to invoke.
     */
    public String getMethodName() {
        return getBody();
    }
}
