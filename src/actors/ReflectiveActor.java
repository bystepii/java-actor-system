package actors;

import messages.Message;
import messages.MethodInvocationMessage;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * This reflective actor is an actor that automatically maps the processing of
 * messages to methods invocation. The method name is the name of the message
 * class, with the first letter in lower case and without the "Message" suffix.
 * For example, the method that will be invoked when a AddInsultMessage message
 * is received is addInsult().
 */
public class ReflectiveActor extends AbstractActor {

    /**
     * The object on which the methods will be invoked.
     */
    private final Service implementation;

    /**
     * Default constructor for the ReflectiveActor class.
     *
     * @param implementation the object on which the methods will be invoked.
     */

    public ReflectiveActor(Service implementation) {
        this.implementation = implementation;
    }

    /**
     * This method is called when a message is received by the actor.
     *
     * @param msg the message received by the actor.
     */
    @Override
    protected void process(Message<?> msg) {
        String methodName;
        Class<?>[] parameterTypes;
        Object[] args;

        // If the message is a MethodInvocationMessage, we use the method name
        // specified in the message.
        if (msg instanceof MethodInvocationMessage m) {
            methodName = m.getMethodName();
            args = m.getArgs();
            parameterTypes = args == null ?
                    null : Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);
        } else {
            // Get the name of the message class.
            Class<?> msgClass = msg.getClass();
            methodName = msgClass.getSimpleName();

            // Convert the first letter to lower case and remove the "Message" suffix
            methodName = methodName.substring(0, 1).toLowerCase() + methodName.substring(1);
            methodName = methodName.substring(0, methodName.length() - 7);

            // Get the parameter types of the method for the corresponding message body type
            // new Class<?>[0] indicates that the method has no parameters
            parameterTypes = msg.getBody() == null ? null : new Class<?>[]{msg.getBody().getClass()};

            // Get the array of arguments for the method invocation
            // new Object[0] indicates that the method has no arguments
            args = msg.getBody() == null ?
                    new Object[0] : new Object[]{msg.getBody()};
        }

        try {

            // Get the method for the given name and parameter types from the implementation object
            Method method = implementation.getClass().getMethod(methodName, parameterTypes);

            // Invoke the method on the implementation object with the given arguments and get the result
            Object result = method.invoke(implementation, args);

            // If the method returns a value, send it back to the sender
            Class<?> returnType = method.getReturnType();
            if (returnType != void.class)
                msg.getSender().send(new Message<>(this, name, result));
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No method with name " + methodName + " in class " + implementation.getClass().getName());
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}
