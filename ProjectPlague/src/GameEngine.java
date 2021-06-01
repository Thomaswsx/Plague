import javax.swing.*;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class GameEngine extends Thread {

    Date data = new Date();
    Gui gui;
    static int newPossibleInfections; // proporcjonalnie zmieni sie do poziomu trudnosci
    int points;
    int dayPassed; //kazdy dzien to zdobyte punkty
    static boolean isThere = false;
    boolean isRunning = false;

    ArrayList<Upgrade> allUpgrades = new ArrayList<>(
            Arrays.asList(
                    new Upgrade("Speed research", 10, 0, false),
                    new Upgrade("Lockdown", 0, 15, false),
                    new Upgrade("Free Vitamin C", 0, 0, true),
                    new Upgrade("Doctor Online", 0, 10, true),
                    new Upgrade("SocMed Compain", 5, 6, false),
                    new Upgrade("Healthy Food", 2, 2, true),
                    new Upgrade("Free Disinfectants", 1, 6, false),
                    new Upgrade("Med school gtants", 10, 0, false),
                    new Upgrade("Face Masks", 0, 5, false),
                    new Upgrade("Charlatan's Advice", randomNegNum(), randomNegNum(), randomBool())
            )
    );

    GameEngine() { //model singleton
        if (isThere) {
            System.out.println("Instancja GameEngine już istnieje");
            return; //zeby nie zwrocic obiektu
        }
        isThere = true; //zwroci obiekt
        points = 0;
        dayPassed = 0;

    }

    ArrayList<Country> allCountries = new ArrayList<Country>(
            Arrays.asList(
                    new Country("United States", 311002651, 9147420, Climate.Cold, 10, 20, 30, 180, 130),
                    new Country("China", 1439232775, 9388211, Climate.Humid, 10, 20, 30, 455, 145),
                    new Country("Indonesia", 273523615, 1811570, Climate.Humid, 10, 20, 30, 486, 202),
                    new Country("Brazil", 212559417, 8358140, Climate.Humid, 10, 20, 30, 245, 210),
                    new Country("Nigeria", 206139589, 910770, Climate.Dry, 10, 20, 30, 322, 177),
                    new Country("Russia", 145934462, 16376870, Climate.Cold, 10, 20, 30, 420, 90),
                    new Country("Mexico", 128932753, 1943950, Climate.Dry, 10, 20, 30, 175, 165),
                    new Country("Japan", 126476476, 364555, Climate.Humid, 10, 20, 30, 495, 130),
                    new Country("Egypt", 10234404, 995450, Climate.Hot, 10, 20, 30, 355, 160),
                    new Country("United Kingdom", 67886011, 241930, Climate.Cold, 10, 20, 30, 310, 112),
                    new Country("Poland", 38000000, 306230, Climate.Cold, 10, 20, 30, 330, 107),
                    new Country("Canada", 37742154, 9093510, Climate.Cold, 10, 20, 30, 200, 110),
                    new Country("Austrlia", 25499884, 7682300, Climate.Dry, 10, 20, 30, 500, 240),
                    new Country("United Arab Emirates", 9890402, 83600, Climate.Dry, 10, 20, 30, 387, 159),
                    new Country("Mauritius", 1271768, 2030, Climate.Cold, 10, 20, 30, 392, 223)
            ));


    ArrayList<Country> activeCountries = new ArrayList<>(); // biora udzial w grze

    void startGame() {
        isRunning = true;
        gui = new Gui(this); // z poziomu GameEngine mamy lepszy dostep do gui
        gui.onStartDialog();
    }

    @Override
    public void run() {

        //Losownie wybranych panstw
        Collections.shuffle(allCountries); //pomieszanie listy


        for (int i = 0; i < 10; i++) {

            activeCountries.add(allCountries.get(i)); // dodajemy do activeCountries (panstwa biorace udzial w grze)
        }

        switch (gui.difficultyLevel) {
            case Easy:
                sparkPandemic(1);
                break;
            case Medium:
                sparkPandemic(10);
            case Hard:
                sparkPandemic(100);
                break;
        }


        createConnections();

        for (int i = 0; i < 100; i++) {

            //Is running break
            while (!isRunning) {
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Main.log("Upłynął dzień (total: " + dayPassed + ")",true);
            updateCurrentStatus();
            dayPassed++;
            try {
                hasGameEnded();
            } catch (IOException e) {
                e.printStackTrace();
            }
            newInfectionInCountries();
            points += 1;

            generateStatsString();

            try {
                sleep(5000);
            } catch (InterruptedException e) {
                //Nastąpi po wywołaniu metody ".interrupt()" na tym obiekcie}
                break; // break zakonczy petle;
            }
        }
    }

    private void generateStatsString() {

        String result = "<html>"; // otworzenie znacznika html
        for (Country c : activeCountries) {
            result += c.name + ":<br>     -Healthy:" + c.healthy + "<br>   -Sick:" + c.sick + "<br>    -Dead:" + c.dead + "<br><br>";
        }

        result += "</html>";

        gui.statsLabel.setText(result);
        SwingUtilities.updateComponentTreeUI(gui.statsWindow); //odswiezamy to co w argumencie

    }

    public void newInfectionInCountries() {
        for (Country c : activeCountries) {
            c.dayPass();
        }
    }

    static int randomNum(int limit) { //zwraca lb miedzy 0 a limit
        return (int) (Math.random() * limit + 1);
    }

    static int randomNegNum(){
        int mod = (int) (Math.random() * 10) % 2 == 0 ? 1 : -1;
        return (int)(Math.random() * 12 + 1) * mod;
    }

    static boolean randomBool(){
        return (int)(Math.random() * 10) % 2 == 0;
    }




    public void createConnections() {
        Country rndCountry = null;
        Transport rndTransport = null;
        ArrayList<Connection> connections = new ArrayList<>();
        for (Country c : activeCountries) {
            for (int i = 0; i < GameEngine.randomNum(activeCountries.size() - 1); i++) { //randomowa liczba polaczen
                rndCountry = activeCountries.get(GameEngine.randomNum(activeCountries.size() - 1)); //randomowe panstwo do polaczenia
                if (rndCountry == c) { //jezeli wylosujemy to samo panstwo, nie dodajemy takiego polaczenia
                    continue;
                }

                //tworzenie transportu
                switch (GameEngine.randomNum(2)) {
                    case 0:
                        rndTransport = new TransportBoat(GameEngine.randomNum(15));
                        break;
                    case 1:
                        rndTransport = new TransportCar(GameEngine.randomNum(15));
                        break;

                    case 2:
                        rndTransport = new TransportPlane(GameEngine.randomNum(15));
                        break;
                }
                connections.add(new Connection(rndTransport, c, rndCountry)); //dodajemy nowe poleczenie do ogolnej listy
            }
        }
        //Dodajemy stworzone polaczenia do adekwatnych panstw

        for (Connection connection : connections) {
            connection.c1.connections.add(connection);
            connection.c2.connections.add(connection);
        }

    }

    public void sparkPandemic(int infectedNumber) { //infectedNumber - ile osob chorych na start
        Country startCountry = activeCountries.get((int) (Math.random() * activeCountries.size()));
        Main.log("Infection  has started in: " + startCountry.name, true);

        startCountry.healthy -= infectedNumber;
        startCountry.sick += infectedNumber;
    }

    public void updateCurrentStatus() {
        int dead = 0;
        int sick = 0;
        long healthy = 0;

        for (Country c : activeCountries) {
            dead += c.dead;
            sick += c.sick;
            healthy += c.healthy;
        }

        gui.currentStatus.setText("Points: " + points + " Healthy:" + healthy + " Sick:" + sick + " Dead:" + dead);
    }

    public void hasGameEnded() throws IOException {
        boolean noInfection = true;
        boolean allDead = true;

        if (points == 0) {
            return;
        }


        for (Country c : activeCountries) {
            if (c.sick != 0) ;
            noInfection = false;

            if (c.dead != c.population) {
                allDead = false;
            }
        }
        if (noInfection) {
            this.interrupt();
            savePoints();

            try {
                gui.generateWinWindow().setVisible(true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (allDead) {
            savePoints();
            this.interrupt();
            gui.generateLooseWindow().setVisible(true);
        }
    }

    public void savePoints() {
        try {
            FileWriter highScoreWriter = new FileWriter("highscore");
            highScoreWriter.write("\n" + points);
            highScoreWriter.flush(); //by na pewno wszystko zostalo zapisane do pliku
            highScoreWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
