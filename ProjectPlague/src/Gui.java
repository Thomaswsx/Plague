import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

public class Gui extends JFrame {
    DifficultyLevel difficultyLevel = DifficultyLevel.Easy; //siegamy do klasy, potem do pola enumowego
    GameEngine gameEngine; //by gui moglo siegac do gameEngine
    JFrame statsWindow;
    JLabel statsLabel;
    JLabel currentStatus = new JLabel("Points: 0"); //ilosc zdrowych, chorych, nie zywych i punkty


    public Gui(GameEngine ge) {
        this.gameEngine = ge;
        this.statsWindow = new JFrame("Stats"); // tworzymy okienko
        this.statsWindow.setSize(300, 500);
        this.statsWindow.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE); //schowanie okienka
        statsLabel = new JLabel("No data");
        this.statsWindow.add(new JScrollPane(statsLabel));
    }

    public void onStartDialog() {

        //Dialog
        //New Game
        //High Scores
        //Exit

        JFrame jFrameMenu = new JFrame("Menu");
        jFrameMenu.setSize(150, 200);
        JPanel buttons = new JPanel(); //Panel na przyciski, kolekcja elementow
        buttons.setLayout(new GridLayout(5, 1));
        jFrameMenu.add(buttons); //dodajemy panel przyciski
        jFrameMenu.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //zamkniecie okienka, ale nie zakonczy programu


        JButton newGame = new JButton("New Game");
        JButton highScore = new JButton("High Scores");
        JButton exit = new JButton("Exit");

        newGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { //cos zostalo nacisniete
                selectDifficultLevel();
                jFrameMenu.dispose(); //zamykanie okienka

            }
        });

        highScore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highScoreWindow();
            }
        });

        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jFrameMenu.dispose();
            }
        });

        buttons.add(newGame);
        buttons.add(new JPanel()); // wkladamy nowy panel, ale bedzie to pusta przestrzen
        buttons.add(highScore);
        buttons.add(new JPanel());
        buttons.add(exit);

        jFrameMenu.setVisible(true); //by okienko bylo widac
    }

    public void gameWindow() throws IOException {

        //this - wskazuje na obiekt gui JFrame
        this.setSize(700, 400);// ustalenie rozmiaru
        this.setLayout(new BorderLayout());
        this.setLocation(100, 100); //umieszczenie okna
        this.setResizable(false); // zablokowanie mozliwosci zmiany rozmiaru okna

        //Menu Bar creator
        JMenuBar jMenuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem resumePauseGame = new JMenuItem("Pause");
        resumePauseGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameEngine.isRunning = !gameEngine.isRunning;

                if (gameEngine.isRunning){ //jesli true / odpalona gra
                    Main.log("Game has been resumed", true);
                    resumePauseGame.setText("Pause");
                }else {
                    Main.log("Game has been stopped", true);
                    resumePauseGame.setText("Resume");
                }
            }
        });

        fileMenu.add(resumePauseGame);
        resumePauseGame.setAccelerator(KeyStroke.getKeyStroke('p'));
        jMenuBar.add(fileMenu);




        JMenu upgradesMenu = new JMenu("Upgrades");
        for (Country c : gameEngine.activeCountries) {
            JMenu countryMenu = new JMenu(c.name);
            upgradesMenu.add(countryMenu);
            for (Upgrade u : gameEngine.allUpgrades) {
                JMenuItem singleUpgrade = new JMenuItem(u.name);
                countryMenu.add(singleUpgrade);

                singleUpgrade.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        Main.log(c.name + " just bought: " + u.name, true);
                        c.uprgrades.add(u);
                    }
                });

            }

        }

        jMenuBar.add(upgradesMenu);

        this.setJMenuBar(jMenuBar);

        JLayeredPane map = new JLayeredPane();


        // Umieszczenie znaczków/gwiazdki
        for (Country c : gameEngine.activeCountries) {

            if (c.yCord == 0) {
                continue;
            }
            //koniec

            BufferedImage mapImage2 = ImageIO.read(new File("src/sources/smallgoodstar.png"));
            JLabel mapLabel2 = new JLabel(new ImageIcon(mapImage2));
            mapLabel2.setBounds(c.xCord, c.yCord, 20, 20);
            c.mapElement = mapLabel2;
            mapLabel2.setToolTipText(c.name);

            map.add(mapLabel2, map.highestLayer() + 1);
        }

        map.setLayout(null);
        BufferedImage mapImage = ImageIO.read(new File("src/sources/worldmap.jpg"));
        JLabel mapLabel = new JLabel(new ImageIcon(mapImage));
        mapLabel.setBounds(100, 20, 470, 282);
        map.add(mapLabel, -1);

        this.add(map);


        //Lower bar panel
        JPanel lowerBar = new JPanel();
        lowerBar.setLayout(new GridLayout(1, 2));


        currentStatus = new JLabel("Points: 0");
        lowerBar.add(currentStatus);


        JButton worldButton = new JButton("World");

        worldButton.addActionListener(new ActionListener() { //otworzy sie wtedy kiedy klikniemy
            @Override
            public void actionPerformed(ActionEvent e) {
                statsWindow.setVisible(true);

            }
        });

        lowerBar.add(worldButton);

        this.add(lowerBar, BorderLayout.PAGE_END);


        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // Ustawienie zamykanie okna. Po zamkniecy okna, program zostaje zakonczony
        this.setVisible(true);//by okienko bylo widac
    }

    public void highScoreWindow() {
        JFrame jFrame = new JFrame();
        jFrame.setSize(300, 300);


        jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); // Ustawienie zamykanie okna. Po zamkniecy okna, program zostaje zakonczony

        jFrame.add(new JScrollPane(generateHighScore()));

        jFrame.setVisible(true);//by okienko bylo widac
    }

    public void selectDifficultLevel() {

        JFrame jFrameMenu = new JFrame("Choose difficulty");
        jFrameMenu.setSize(150, 200);
        JPanel buttons = new JPanel(); //Panel na przyciski, kolekcja elementow
        buttons.setLayout(new GridLayout(5, 1));
        jFrameMenu.add(buttons); //dodajemy panel przyciski
        jFrameMenu.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE); //zamkniecie okienka, ale nie zakonczy programu


        JButton easy = new JButton("Easy");
        JButton medium = new JButton("Medium");
        JButton hard = new JButton("Hard");

        easy.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { //cos zostalo nacisniete

                try {
                    GameEngine.newPossibleInfections = 2;

                    difficultyLevel = DifficultyLevel.Easy;

                    gameEngine.start(); //uruchomienie wątku czasu

                    gameWindow();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                jFrameMenu.dispose(); //zamykanie okienka

            }
        });

        medium.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    GameEngine.newPossibleInfections = 5;

                    difficultyLevel = DifficultyLevel.Medium;

                    gameEngine.start(); //uruchomienie wątku czasu

                    gameWindow();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                jFrameMenu.dispose();
            }
        });

        hard.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                try {
                    GameEngine.newPossibleInfections = 10;

                    difficultyLevel = DifficultyLevel.Hard;

                    gameEngine.start(); //uruchomienie wątku czasu

                    gameWindow();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
                jFrameMenu.dispose();
            }
        });

        buttons.add(easy);
        buttons.add(new JPanel()); // wkladamy nowy panel, ale bedzie to pusta przestrzen
        buttons.add(medium);
        buttons.add(new JPanel());
        buttons.add(hard);

        jFrameMenu.setVisible(true); //by okienko bylo widac
    }

    public JFrame generateWinWindow() throws IOException {
        JFrame winWindow = new JFrame("WINNER!");
        winWindow.setSize(300,300);
        winWindow.add(new JLabel("YOU HAVE WON! EARTH IS SAFE, CONGRATULATIONS!"));

        BufferedImage image = ImageIO.read(new File("src/sources/youwinmeme.jpg"));
        JLabel imglabel = new JLabel(new ImageIcon(image));
        winWindow.add(imglabel);
        return winWindow;
    }

    public JFrame generateLooseWindow() throws IOException {
        JFrame winWindow = new JFrame("LOOSER!");
        winWindow.setSize(300,300);
        winWindow.add(new JLabel("YOU HAVE LOST! THE VIRUS HAS ERADICATED ALL HUMANS!"));

        BufferedImage image = ImageIO.read(new File("src/sources/youloosememe.jpg"));
        JLabel imglabel = new JLabel(new ImageIcon(image));
        winWindow.add(imglabel);
        return winWindow;

    }

    public JLabel generateHighScore() {
        ArrayList<Integer> points = new ArrayList<>();
        String text = "<html>";
        try {
            Scanner sc = new Scanner(new File("src/highscore"));
            while (sc.hasNext()) {
                points.add(Integer.parseInt(sc.nextLine()));
            }

            Collections.sort(points);
            for (int i : points) {
                text += i + "<br>";
            }
            text += "</html>"; //zamykamy tag

            return new JLabel(text);


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;

    }

}

