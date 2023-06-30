package it.polimi.ingsw;

public class Main {

    public static void main(String[] args) {
        if (args.length==5) AppClientOne.main(args);
        else if (args.length==3) AppServerOne.main(args);
        else {
            System.out.println("Usage: java -jar MyShelfie.jar [G/T] [R/S] [ipAddress] [socketPort] [rmiPort]");
            System.out.println("Usage: java -jar MyShelfie.jar [ipAddress] [socketPort] [rmiPort]");
        }
    }

}
