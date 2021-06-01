public abstract class Transport {

    String name;
    int chanceOfInfection;

    public Transport(String name, int chanceOfInfection) {
        this.name = name;
        this.chanceOfInfection = chanceOfInfection;
    }

    @Override
    public String toString() {
        return "Transport{" +
                "name='" + name + '\'' +
                ", chanceOfInfection=" + chanceOfInfection +
                '}';
    }
}
