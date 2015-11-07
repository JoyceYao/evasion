package evasion.connections;
public interface GameWithPublisherSocket {
    public void ReceivedMessageFromPublisherSocket(String message);
    public void ConnectionMadeWithPublisherSocket();
}
