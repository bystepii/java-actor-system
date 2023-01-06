package actors;

import messages.Message;
import messages.MethodInvocationMessage;
import messages.QuitMessage;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Represents a dynamic proxy for an Actor, which can be used to send and receive
 * messages in a more elegant way, using method calls instead of message sending.
 */
public class DynamicProxy implements InvocationHandler {

    /**
     * The reference to the Actor.
     */
    private final ActorRef targetActor;

    /**
     * The queue of messages received from the Actor.
     */
    private final BlockingQueue<Message<?>> receivedMessages = new LinkedBlockingQueue<>();

    /**
     * Creates a new DynamicProxy for the given Actor.
     *
     * @param target the Actor to create a proxy for.
     */
    public DynamicProxy(ActorRef target) {
        this.targetActor = target;
    }

    /**
     * Creates a new DynamicProxy that implements the given interface for the given Actor.
     *
     * @param interfaceType the interface to implement, which must inherit from {@link Service} interface
     *                      in order to be able to stop the actor.
     * @param target        the Actor to create a proxy for.
     * @return a new DynamicProxy that implements the given interface.
     */
    public static Object intercept(Class<? extends Service> interfaceType, ActorRef target) {
        return Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class[]{interfaceType},
                new DynamicProxy(target));
    }

    /**
     * Processes a method call and sends a message to the Actor.
     * <p>
     * The method call is processed in the following way:
     * <ul>
     *     <li>
     *         if the method is {@link Service#quit()}, a {@link QuitMessage} is sent to the Actor.
     *     </li>
     *     <li>
     *         first, the method name is extracted from the method call and converted to the following
     *         message class name: {@code messages.<method name with capital letter at the beginning>Message}
     *         (e.g. {@code messages.SumMessage} for the {@code sum} method).
     *     </li>
     *     <li>
     *         if the class exists, it is instantiated with the arguments of the method call and sent to the Actor.
     *     </li>
     *     <li>
     *         if the class does not exist, a {@link MethodInvocationMessage} with the method name and arguments
     *         is sent to the Actor.
     *     </li>
     *     <li>
     *         if the method has a return type, the next message received by the Actor is returned as the result
     *         using the {@link ActorProxy#receive()} method.
     *     </li>
     *     <li>
     *         if the method does not have a return type, nothing is returned.
     *     </li>
     * </ul>
     *
     * @param proxy  the proxy instance that the method was invoked on.
     * @param method the {@link Method} instance corresponding to the interface method invoked on the proxy instance.
     * @param args   an array of objects containing the values of the arguments passed in the method invocation on the proxy instance.
     * @return the result of the method call.
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {

        // Handle the end() method and the equals(), hashCode() and toString() methods.
        switch (method.getName()) {
            case "end" -> {
                targetActor.send(new QuitMessage());
                return null;
            }
            case "equals" -> {
                return targetActor.equals(args[0]);
            }
            case "hashCode" -> {
                return targetActor.hashCode();
            }
            case "toString" -> {
                return targetActor.toString();
            }
        }

        try {
            // Set first letter of method name to uppercase
            String methodName = String.valueOf(method.getName().charAt(0)).toUpperCase()
                    + method.getName().substring(1);

            Message<?> msg;

            try {
                // Try to instantiate the message class
                Class<?> msgClass = Class.forName("messages." + methodName + "Message");

                Class<?>[] argTypes = method.getParameterTypes();
                Constructor<?> constructor = msgClass.getConstructor(argTypes);

                msg = (Message<?>) constructor.newInstance(args);
            } catch (ClassNotFoundException e) {
                // fallback to MethodInvocationMessage
                msg = new MethodInvocationMessage(methodName, args);
            }

            msg.setSender(receivedMessages::add);
            msg.setSenderName((targetActor instanceof Actor a ? a.getName() : "unknown") + " (DynamicProxy)");
            targetActor.send(msg);

            // If the method has a return type, we wait for the response
            if (method.getReturnType() != void.class) {
                while (true) {
                    try {
                        return receivedMessages.take().getBody();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Invocation of " + method.getName() + " failed: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
