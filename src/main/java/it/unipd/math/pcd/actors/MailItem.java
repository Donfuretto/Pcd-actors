package it.unipd.math.pcd.actors;

/**
 * Struttura MailItem.
 *
 * @author David Tessaro
 * @version 1.0
 * @since 1.0
 */

class MailItem<T extends Message> {
    private final T message;
    private final ActorRef<T> sender;

    /**
     * Costruttore
     *
     * @param message messaggio e mittente
     */
    public MailItem(T message, ActorRef<T> sender) {
        this.sender = sender;
        this.message = message;

    }

    /**
     * Ritorna messaggio associato in MailBag
     *
     * @return messaggio
     */
    public T getMessage() {

        return message;
    }

    /**
     * ritorna mittente
     *
     * @return mittente
     */
    public ActorRef<T> getSender() {

        return sender;
    }
}