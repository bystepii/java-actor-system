import Decorators.EncryptionDecorator;
import actors.*;
import messages.*;

public class Main {
    public static void main(String[] args) {
        /*
        Actor a1 = ActorContext.spawnActor("hw1", new HelloWorldActor());
        a1.send(new Message(null, "Hello World!"));
        try {
            Thread.sleep(2000);
        } catch (InterruptedException ignored) {

        }
        a1.send(new QuitMessage());

         */

        /*
        RingActor[] actors = new RingActor[12];
        actors[actors.length - 1] = new RingActor(null);
        ActorContext.spawnActor("a12", actors[actors.length - 1]);
        for (int i = actors.length - 2; i >= 0; i--) {
            actors[i] = new RingActor(actors[i + 1]);
            ActorContext.spawnActor("a" + i, actors[i]);
        }
        actors[actors.length - 1].setNext(actors[0]);
        actors[0].send(new RingMessage(null, "Ring!"));
         */

        /*
        Actor insult = ActorContext.spawnActor("insult1",new InsultActor());
        insult.send(new AddInsultMessage("You are a bad person"));
        insult.send(new AddInsultMessage("A bad person"));
        insult.send(new GetInsultMessage());
        insult.send(new GetInsultMessage());
        insult.send(new GetInsultMessage());
        insult.send(new QuitMessage());
        Message result = insult.receive();
        System.out.println(result.getText());

         */

        Actor hw = ActorContext.spawnActor("hw2", new EncryptionDecorator(new HelloWorldActor()));
        hw.send(new Message("Hello World!"));
        hw.send(new QuitMessage());
    }
}