package actors;

import messages.Message;
import messages.QuitMessage;

import java.lang.reflect.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class DynamicProxy implements InvocationHandler {

    private final ActorRef targetActor;

    private final BlockingQueue<Message> receivedMessages = new LinkedBlockingQueue<>();

    public DynamicProxy(ActorRef target) {
        this.targetActor = target;
    }

    public static Object intercept(ActorRef targetActor, Class<?> interfaceType) {
        return Proxy.newProxyInstance(interfaceType.getClassLoader(),
                new Class[]{interfaceType},
                new DynamicProxy(targetActor));
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object result = null;
        try {
            if (method.getName().equals("end")) {
                targetActor.send(new QuitMessage());
                return null;
            }

            String methodName = String.valueOf(method.getName().charAt(0)).toUpperCase()
                    + method.getName().substring(1);

            Class<?> msgClass = Class.forName("messages." + methodName + "Message");

            Class<?>[] argTypes = method.getParameterTypes();
            Constructor<?> constructor = msgClass.getConstructor(argTypes);

            Message msg = (Message) constructor.newInstance(args);

            msg.setFrom(new ActorRef() {
                @Override
                public void send(Message msg) {
                    receivedMessages.add(msg);
                }

                @Override
                public String getName() {
                    return targetActor.getName();
                }
            });

            targetActor.send(msg);

            if (method.getName().startsWith("get")) {
                while (true) {
                    try {
                        return receivedMessages.take().getText();
                    } catch (InterruptedException ignored) {
                    }
                }
            }
        } catch (InvocationTargetException ite) {
            throw ite.getTargetException();
        } catch (Exception e) {
            System.err.println("Invocation of " + method.getName() + " failed");
        }
        return result;
    }
}
