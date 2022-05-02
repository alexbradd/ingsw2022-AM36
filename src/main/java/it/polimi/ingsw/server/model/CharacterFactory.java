package it.polimi.ingsw.server.model;

// STUB
class CharacterFactory {
    static Character[] pickNRandom(int n) {
        Character[] characters = new Character[n];
        for (int i = 0; i < n; i++) {
            characters[i] = new Herbalist();
        }

        return characters;
    }
}
