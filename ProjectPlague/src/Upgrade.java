public class Upgrade {



    String name;

    int vaccSpeed; //rozwijanie szczepionki
    int infectionSpeedDecrease; //spowolnienie rozwoju choroby
    boolean blocksClimateInfluence; // czy klimat ma znaczenie

    public Upgrade(String name, int vaccSpeed, int infectionSpeedDecrease, boolean blocksClimateInfluence) {
        this.name = name;
        this.vaccSpeed = vaccSpeed;
        this.infectionSpeedDecrease = infectionSpeedDecrease;
        this.blocksClimateInfluence = blocksClimateInfluence;
    }
}
