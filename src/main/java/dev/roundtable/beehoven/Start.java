package dev.roundtable.beehoven;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Start {

    public static void main(String[] args) {

        Beehoven beehoven = Beehoven.getInstance();

        Runtime.getRuntime().addShutdownHook(new Thread(beehoven::shutdown));

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String line;

        try {

            while ((line = br.readLine()) != null) {

                String[] input = line.split(" ");

                switch (input[0].toLowerCase()) {

                    case "stop":
                    case "quit":
                    case "end":
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Unknown command: \"" + input[0] + "\"");
                        break;

                }

            }

            br.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Goodbye.");

    }

}
