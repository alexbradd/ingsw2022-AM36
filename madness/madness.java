class Phase {
    private final PlayerList players;
    private final ProfessorList professors
    private final Sack sack;
    private final CloudList clouds;
    private final IslandList islands;
    private final MotherNature motherNature;
    private final CharacterList characters;
    
    Phase(Phase old) {
        players = players.shallowClone();
        professors = old.professors.clone(players);
        players.rebindStores(professors);
        
        sack = old.sack.clone(professors);      
        clouds = old.clouds.clone(professors);      
        islands = old.islands.clone(professors, players);      
        motherNature = old.motherNature.clone(islands);
        
        characterList = old.characterList; // ???
    }
}

class PlayerList {
    ...
    void PlayerList shallowClone() { // they will share entrance/hall
        PlayerList ret = PlayerList();
        list.forEach(p -> ret.add(p.shallowClone()));
    }
    void rebindStores(ProfessorList newProfessors) {
        list.forEach(p -> {
            p.cloneEntrance(newProfessors);
            p.cloneHall(newProfessors);
        });
    }
}

class Player {
    ...
    private final StudentStore entrance, hall;
    ...
    Player shallowClone() {
        Player ret = new Player(username, entranceSize, towerColor);
        ret.entrance = this.entrance.shallowClone();
        ret.entrance = this.hall.shallowClone();
        ret.setNumOfTowers(this.getNumOfTowers());
        return ret;
    }
    void cloneEntrance(ProfessorList professors) {
        entrance = entrance.clone(professors);
    }
    void cloneHall(ProfessorList professors) {
        hall = hall.clone(professors);
    }
}

class StudentStore {
    ...
    StudentStore shallowClone() {
        Store ret = new Store();
        getStudents().forEach(ret::add);
        return ret;
    }
    StudentStore clone(ProfessorList newProfessors) {
        Store ret = new Store();
        getStudents().forEach(s -> {
            Professor newProfessor = newProfessors.get(s.getColor());
            ret.add(new Student(newProfessor));
        });
        return ret;
    }
}

class Sack { // CloudList è la stessa cosa
    private final StudentStore store;
    ...
    Sack clone(ProfessorList newProfessors) {
        Sack ret = new Sack();
        ret.store = store.clone(professors);
        return ret;
    }
}

class IslandList { // TODO adottare l'idea del conti
    ...
    void scrub(Consumer<Island> mergeCallback) {
        for (int i = 0; i < pieces.length; i++) {
            Island p1 = pieces[i];
            Island p2 = i >= pieces.length - 1 ? pieces[0] : pieces[i + 1];
            if (p1.canBeMergedWith(p2)) p1.merge(p2, mergeCallback);
        }

    }
    private void replace(Island oldIsland, Island newIsland) {...}
    IslandList clone(ProfessorList newProfessors, PlayerList newPlayers) {
        IslandList ret = new IslandList();
        list.forEach(i -> ret.replace(i, i.clone(newProfessors, newPlayers);
        return ret;
    }
}

class IslandListIterator {
    IslandListIterator(IslandList list, Island start) {
        ...
    }
}

class Island { // TODO adottare l'idea del conti
    private final StudentStore store;
    ...
    void conquer(Player conqueror, Consumer<Island> mergeCallback) { // The callback is called only in case of a merge
        ...
        bound.scrub(mergeCallback)
    }
    Island merge(Island other, Consumer<Island> mergeCallback) {
        Island ret = ...;
        ...
        mergeCallback.accept(ret);
        return ret;
    }
    Island clone(ProfessorList newProfessors, PlayerList newPlayers) {
        // TODO
    }
}

class MotherNature {
    private final IslandList list;
    private IslandListIterator iterator;
    ...
    void move(int steps) {
        ...
    }
    void assignTower(Island i) {
        calculator
            .calculateInfluences(island)
            .flatMap(extractor)
            .ifPresent(p -> {
                island.conquer(p, i -> {
                    iterator = new IslandListIterator(list, i);
                })
            });
    }
    MotherNature clone(IslandList newList) {
        MotherNature ret = new MotherNature(newList);
        ret.iterator = new IslandListIterator(newList, current);
    }
}
