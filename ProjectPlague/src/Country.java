import javax.swing.*;
import java.util.ArrayList;

public class Country {

    String name;
    int population;
    int area;
    Climate climate;
    int xCord, yCord;
    ArrayList<Upgrade> uprgrades = new ArrayList<>();
    ArrayList<Connection> connections = new ArrayList<>();
    int constInfectionRate; //stala zakazen
    int healthy, sick, dead;
    int boatBanLimit, carBanLimit, planeBanLimit; //limit dla panstwa
    boolean boatBanned, carBanned, planeBanned;
    JLabel mapElement;
    int vaccStatus = 0; //ile mamy zrobionej szczepionki w %

    public Country(String name, int population, int area, Climate climate, int boatBanLimit, int carBanLimit, int planeBanLimit, int xCord, int yCord) {
        this.name = name;
        this.population = population;
        this.area = area;
        this.climate = climate;
        this.healthy = this.population;
        this.dead = 0;
        this.sick = 0;
        this.constInfectionRate = (int) (Math.random() * 9 + 1); //stala dziennie dla krajow
        this.boatBanLimit = boatBanLimit;
        this.carBanLimit = carBanLimit;
        this.planeBanLimit = planeBanLimit;
        this.xCord = xCord;
        this.yCord = yCord;
    }


    public void checkForBanTransport() {
        int infectionRate = (sick + dead) * 100 / this.population; // obliczamy na krzyz
        if (infectionRate > boatBanLimit && !boatBanned) { //sprawdza czy nalezy zbanowac oraz czy nie zostalo juz zbanowane
            Main.log(this.name + " has banned boats ", true);
            boatBanned = true;
            for (Connection connection : this.connections) {
                if (connection.transport.getClass() == TransportBoat.class) { // porównanie klas, ponieważ mogą byc to rożne obiekty.
                    connection.isActive = false;
                }
            }

        } else if (infectionRate > carBanLimit && !carBanned) {
            Main.log(this.name + " has banned cars ", true);
            carBanned = true;
            for (Connection connection : this.connections) {
                if (connection.transport.getClass() == TransportCar.class) { // porównanie klas, ponieważ mogą byc to rożne obiekty.
                    connection.isActive = false;
                }
            }
        } else if (infectionRate > planeBanLimit && !planeBanned) {
            Main.log(this.name + " has banned planes ", true);
            planeBanned = true;
            for (Connection connection : this.connections) {
                if (connection.transport.getClass() == TransportPlane.class) { // porównanie klas, ponieważ mogą byc to rożne obiekty.
                    connection.isActive = false;
                }
            }
        }

    }

    //lodzie zamykamy przy 20% zarazonych
    //Auta zamykamy przy 30% zarazonych
    //Samoloty zamykamy przy 50% zarazaonych


    public void dayPass() {
        //  ile ma byc nowych zakazonych, ile ma sie pojawic

        checkForBanTransport();
        int climatEffect = this.climate.climate;
        int infectionDecrease = 0;
        int vaccSpeed = 1; //Ile zostanie dodane kazdego dnia

        for (Upgrade u : uprgrades) {
            if (u.blocksClimateInfluence) {
                climatEffect = 0;
            }

            infectionDecrease += u.infectionSpeedDecrease;
            vaccSpeed += u.vaccSpeed;
        }

//        vaccSpeed - predkosc tworzenia szczepionka
        this.vaccStatus += vaccSpeed;
        if (vaccStatus > 100) {
            Main.log(name + " has developed a cure!",true);
            constInfectionRate -= 10; //by ludzie sie szybko leczyli
            climatEffect = -4; //odejmujemu od zarazonych
            vaccStatus = 0;

        }


        int percentOfInfected = (int) (sick * 100 / population);
        //jak szybko beda zakazenia rosnac

        int newInfected;
        if (isInfected()) {
            newInfected = (int) (constInfectionRate + percentOfInfected + climatEffect - infectionDecrease) / 700 * sick; // wynikaja z dnia
        } else {
            newInfected = 0;
        }


        for (Connection c : connections) {
            if (c.isActive) {
                if (c.c1 == this && c.c2.isInfected() || c.c2 == this && c.c1.isInfected()) { //c1  i c2 kraj
                    if (GameEngine.randomNum(100) < c.transport.chanceOfInfection) ; //jesli true dochodzi do zarazenia
                    newInfected += GameEngine.randomNum(GameEngine.newPossibleInfections); // dodajemy  ze do 20 osob moze zarazic sie przy jednym transporcie
                }

            }
        }


        //nowe smierci

        int newDeaths = GameEngine.randomNum(5) * sick / 100;
        int newHealed = GameEngine.randomNum(5) * sick / 100;

        healthy += newHealed;
        sick -= newHealed;

        dead += newDeaths;
        sick -= newDeaths;

        healthy -= newInfected;
        sick += newInfected;


        //Aktualizacja tooltipu
        if (this.mapElement == null) {
            return;
        }
        this.mapElement.setToolTipText("<html>" + name + ":<br>     -Healthy:" + healthy + "<br>   -Sick:" + sick + "<br>    -Dead:" + dead + "<br>Vaccine:" + vaccStatus + "%</html>");

    }

    public boolean isInfected() {
        return this.sick > 0;
    }
}
