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

public class DynamicProxy implements InvocationHandler {

    private final ActorRef targetActor;

    private final BlockingQueue<Message<?>> receivedMessages = new LinkedBlockingQueue<>();

    public DynamicProxy(ActorRef target) {
        this.targetActor = target;
    }

    public static Object intercept(ActorRef targetActor, Class<? extends Service> interfaceType) {
        return Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class[]{interfaceType},
                new DynamicProxy(targetActor));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
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
                Class<?> msgClass = Class.forName("messages." + methodName + "Message");

                Class<?>[] argTypes = method.getParameterTypes();
                Constructor<?> constructor = msgClass.getConstructor(argTypes);

                msg = (Message<?>) constructor.newInstance(args);
            } catch (ClassNotFoundException e) {
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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Invocation of " + method.getName() + " failed");
        }
        return null;
    }
}
