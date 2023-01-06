package messages;

import actors.ActorRef;

/**
 * This class represents a generic message that can be sent to an Actor.
 *
 * @param <T> the type of the message.
 */
public class Message<T> {

    /**
     * The Actor that sent the message.
     * This field is transient because it is not serializable.
     */
    private transient ActorRef sender;

    /**
     * The name of the Actor that sent the message.
     */
    private String senderName;

    /**
     * The content of the message.
     */
    private T body;

    /**
     * Default constructor.
     *
     * @param sender     the Actor that sent the message.
     * @param senderName the name of the Actor that sent the message.
     * @param body       the body of the message.
     */
    public Message(ActorRef sender, String senderName, T body) {
        this.sender = sender;
        this.senderName = senderName;
        this.body = body;
    }

    /**
     * Creates a new Message with empty body.
     *
     * @param sender     the Actor that sent the message.
     * @param senderName the name of the Actor that sent the message.
     */
    public Message(ActorRef sender, String senderName) {
        this(sender, senderName, null);
    }

    /**
     * Creates a new Message with empty sender and empty senderName.
     * <p>
     * This constructor should be used only when the sender is not known, for example, when
     * the message is sent to an ActorProxy from a user.
     *
     * @param body the body of the message.
     */
    public Message(T body) {
        this(null, null, body);
    }

    /**
     * Creates a new Message with empty body and empty sender.
     */
    public Message() {
        this(null, null, null);
    }

    /**
     * Getter for the sender reference.
     *
     * @return the Actor that sent the message.
     */
    public ActorRef getSender() {
        return sender;
    }

    /**
     * Setter for the sender reference.
     *
     * @param sender the Actor that sent the message.
     */
    public void setSender(ActorRef sender) {
        this.sender = sender;
    }

    /**
     * Getter for the sender name.
     *
     * @return the name of the Actor that sent the message.
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Setter for the sender name.
     *
     * @param senderName the name of the Actor that sent the message.
     */
    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    /**
     * Getter for the body of the message.
     *
     * @return the body of the message.
     */
    public T getBody() {
        return body;
    }


    /**
     * Setter for the body of the message.
     *
     * @param body the body of the message.
     */
    public void setBody(T body) {
        this.body = body;
    }
}
