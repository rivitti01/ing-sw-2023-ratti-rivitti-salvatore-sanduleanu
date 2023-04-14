package it.polimi.ingsw.model;
import static it.polimi.ingsw.Costants.*;
import static it.polimi.ingsw.model.Colors.*;
import static it.polimi.ingsw.model.Colors.ANSI_RESET;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class PersonalGoalCard {
    final Tile[][] goalsShelf;
    final String cardName;

    public PersonalGoalCard(String personalGoalCard){
        cardName = personalGoalCard;
        goalsShelf = new Tile[SHELF_ROWS][SHELF_COLUMN];
        String filePath = "src/main/resources/PersonalGoalStrategy.json";
        File input = new File(filePath);
        try {
            JsonElement fileElement = JsonParser.parseReader(new FileReader(input));
            JsonObject fileObject = fileElement.getAsJsonObject();
            JsonArray jsonArrayOfPersonalGoal = fileObject.get(personalGoalCard).getAsJsonArray();
            for (JsonElement personalElement : jsonArrayOfPersonalGoal){
                JsonObject personalObject = personalElement.getAsJsonObject();
                String color = personalObject.get("color").getAsString();
                int x = personalObject.get("x").getAsInt();
                int y = personalObject.get("y").getAsInt();
                goalsShelf[x][y] = new Tile(Color.valueOf(color));
            }
        } catch (FileNotFoundException e) {
            System.err.println("File not found!");
            e.printStackTrace();
        } catch (Exception e){
            System.err.println("File read error!");
            e.printStackTrace();
        }
    }

    public void printPersonal(){
        for (int i = 0; i < SHELF_ROWS; i++) {
            System.out.println("  ");
            for (int j = 0; j < SHELF_COLUMN; j++) {
                if(j==0)
                    System.out.print("|");
                if (this.goalsShelf[i][j] == null)
                    System.out.print("  " + "|");
                else {
                    if (this.goalsShelf[i][j].getColor() == Color.WHITE)
                        System.out.print(ANSI_WHITE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (this.goalsShelf[i][j].getColor() == Color.YELLOW)
                        System.out.print(ANSI_YELLOW_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (this.goalsShelf[i][j].getColor() == Color.BLUE)
                        System.out.print(ANSI_BLUE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (this.goalsShelf[i][j].getColor() == Color.GREEN)
                        System.out.print(ANSI_GREEN_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (this.goalsShelf[i][j].getColor() == Color.PINK)
                        System.out.print(ANSI_PURPLE_BACKGROUND + "  " + ANSI_RESET + "|");
                    else if (this.goalsShelf[i][j].getColor() == Color.CYAN)
                        System.out.print(ANSI_CYAN_BACKGROUND + "  " + ANSI_RESET + "|");
                }
            }
        }
        System.out.println("");
    }
}
