import actors.*;
import messages.AddInsultMessage;
import messages.GetAllInsultsMessage;
import messages.GetInsultMessage;
import messages.QuitMessage;

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

        //ActorProxy hw = ActorContext.spawnActor("hw2", new DummyDecorator(new DummyDecorator(new HelloWorldActor(), "Inner"), "Outer"));
        //hw.send(new Message("Hello World!"));
        //hw.send(new AddClosureMessage((msg) -> msg.getText().toLowerCase().contains("hello")));
        //hw.send(new Message("Hello World!"));
        //hw.send(new Message("Hey World!"));
        //hw.send(new QuitMessage());

        System.out.println("Testing DynamicProxy and InsultService");

        ActorProxy insult = ActorContext.spawnActor("insult2", new InsultActor());
        InsultService insulter = (InsultService) DynamicProxy.intercept(insult, InsultService.class);

        insulter.addInsult("Insult1");
        insulter.addInsult("Insult2");
        insulter.addInsult("Insult3");

        System.out.println(insulter.getInsult());
        System.out.println(insulter.getInsult());
        System.out.println(insulter.getInsult());

        System.out.println(insulter.getAllInsults());

        insulter.end();


        System.out.println("Testing ReflectiveActor and InsultServiceImpl");

        ActorProxy insult2 = ActorContext.spawnActor("insult3", new ReflectiveActor(new InsultServiceImpl()));

        insult2.send(new AddInsultMessage("Insult4"));
        insult2.send(new AddInsultMessage("Insult5"));
        insult2.send(new AddInsultMessage("Insult6"));

        insult2.send(new GetInsultMessage());
        insult2.send(new GetInsultMessage());
        insult2.send(new GetInsultMessage());

        System.out.println(insult2.receive().getBody());
        System.out.println(insult2.receive().getBody());
        System.out.println(insult2.receive().getBody());

        insult2.send(new GetAllInsultsMessage());

        System.out.println(insult2.receive().getBody());


        insult2.send(new QuitMessage());
    }
}
