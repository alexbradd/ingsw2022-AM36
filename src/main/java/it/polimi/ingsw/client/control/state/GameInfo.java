package it.polimi.ingsw.client.control.state;

/**
 * Client's representation of the game's general information.
 *
 * @author Mattia Busso
 */
public class GameInfo {

    /**
     * The id of the game.
     */
    private Long id;

    /**
     * The username of the player.
     */
    private String username;

    /**
     * The number of players of the game.
     */
    private Integer numPlayers;

    /**
     * If the game is on expert mode or not.
     */
    private Boolean isExpert;

    // getters

    /**
     * Username getter.
     * @return the username of the player
     */
    public String getUsername() {
        return username;
    }

    /**
     * NumPlayers getter.
     * @return the number of players of the game
     */
    public Integer getNumPlayers() {
        return numPlayers;
    }

    /**
     * Expert mode getter.
     *
     * @return the expert mode of the game
     */
    public Boolean isExpert() {
        return isExpert;
    }

    /**
     * Id getter.
     *
     * @return the id of the game
     */
    public Long getId() {
        return id;
    }

    //setters

    /**
     * Sets the game's id.
     *
     * @param id the id of the game
     */
    protected void setId(long id) {
        this.id = id;
    }

    /**
     * Sets the player's username.
     *
     * @param username the username of the player
     */
    protected void setUsername(String username) { this.username = username; }

    /**
     * Sets the number of players in the game.
     *
     * @param n the number of players in the game
     */
    protected void setNumPlayers(int n) {
        numPlayers = n;
    }

    /**
     * Sets the game's expert mode.
     *
     * @param isExpert the expert mode of the game
     */
    protected void setExpertMode(boolean isExpert) {
        this.isExpert = isExpert;
    }

}
