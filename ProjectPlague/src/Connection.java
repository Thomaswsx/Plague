public class Connection {
    Transport transport;
    Country c1, c2;
    boolean isActive;

    public Connection(Transport transport, Country c1, Country c2) {
        this.transport = transport;
        this.c1 = c1;
        this.c2 = c2;
        isActive = true;
    }

    @Override
    public String toString() {
        return "Connection{" +
                "transport=" + transport +
                ", c1=" + c1 +
                ", c2=" + c2 +
                ", isActive=" + isActive +
                '}';
    }
}
