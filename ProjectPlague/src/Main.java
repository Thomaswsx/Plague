public class Main {
    public static void main(String[] args) throws Exception {

        GameEngine ge = new GameEngine();
        ge.startGame();

    }

    public static void log(String value, Boolean isInfo) {

        if (isInfo) {
            System.out.println("INFO: " + value);
        } else if(isInfo ){
            System.out.println("DEBUG: " + value);
        }
    }
}
