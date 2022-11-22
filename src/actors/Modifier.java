package actors;

import messages.Message;

public interface Modifier {
    void modify(Message msg);
}
