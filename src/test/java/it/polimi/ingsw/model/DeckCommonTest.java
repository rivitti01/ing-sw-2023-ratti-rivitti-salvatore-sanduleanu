package it.polimi.ingsw.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DeckCommonTest {
    DeckCommon deckCommon;

    @BeforeEach
    void setup(){
        this.deckCommon = new DeckCommon();
    }

    @Test
    void pop(){
        CommonGoalCard card_A = new CommonGoalCard(4, this.deckCommon);
        CommonGoalCard card_B = new CommonGoalCard(4, this.deckCommon);
        CommonGoalCard card_C = new CommonGoalCard(4, this.deckCommon);
        CommonGoalCard card_D = new CommonGoalCard(4, this.deckCommon);
        CommonGoalCard card_E = new CommonGoalCard(4, this.deckCommon);
    }
}
