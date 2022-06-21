package it.polimi.ingsw.client.view.gui;

import it.polimi.ingsw.enums.*;
import it.polimi.ingsw.functional.Tuple;
import javafx.scene.image.Image;

import java.util.EnumMap;
import java.util.Map;

import static it.polimi.ingsw.client.view.gui.GUIUtils.forEachEnumValueLoadImage;
import static it.polimi.ingsw.client.view.gui.GUIUtils.loadImageFromDisk;

/**
 * Static repository of various resources. All resources are lazy-loaded on first usage.
 */
public class AssetManager {
    /**
     * All the student image assets associated with the corresponding {@link PieceColor}
     */
    private static EnumMap<PieceColor, Image> studentPngs = null;
    /**
     * All the professor image assets associated with the corresponding {@link PieceColor}
     */
    private static EnumMap<PieceColor, Image> professorPngs = null;
    /**
     * All the tower image assets associated with the corresponding {@link TowerColor}
     */
    private static EnumMap<TowerColor, Image> towerPngs = null;
    /**
     * All the assistant image assets associated with the corresponding {@link AssistantType}
     */
    private static EnumMap<AssistantType, Image> assistantPngs = null;
    /**
     * All the mage image assets associated with the corresponding {@link Mage}
     */
    private static EnumMap<Mage, Image> magePngs = null;
    /**
     * All the character image assets associated with the corresponding {@link CharacterType}
     */
    private static EnumMap<CharacterType, Image> characterPngs = null;
    /**
     * The two sack state image assets (empty and full in order)
     */
    private static Tuple<Image, Image> sackPngs = null;
    /**
     * The board image asset
     */
    private static Image boardPng = null;
    /**
     * The island image asset
     */
    private static Image islandPng = null;
    /**
     * The island block image asset
     */
    private static Image blockPng = null;
    /**
     * The mother nature image asset
     */
    private static Image mnPng = null;
    /**
     * The coin image asset
     */
    private static Image coinPng = null;
    /**
     * The cloud image asset
     */
    private static Image cloudPng = null;

    /**
     * Returns a {@link Map} linking each student asset with its {@link PieceColor}
     *
     * @return a {@link Map} linking each student asset with its {@link PieceColor}
     */
    public static Map<PieceColor, Image> getStudentPngs() {
        if (studentPngs == null) {
            studentPngs = new EnumMap<>(PieceColor.class);
            forEachEnumValueLoadImage(PieceColor.values(), "/img/students/", studentPngs::put);
        }
        return studentPngs;
    }

    /**
     * Returns a {@link Map} linking each tower asset with its {@link TowerColor}
     *
     * @return a {@link Map} linking each tower asset with its {@link TowerColor}
     */
    public static Map<TowerColor, Image> getTowerPngs() {
        if (towerPngs == null) {
            towerPngs = new EnumMap<>(TowerColor.class);
            forEachEnumValueLoadImage(TowerColor.values(), "/img/towers/", towerPngs::put);
        }
        return towerPngs;
    }

    /**
     * Returns the island image asset
     *
     * @return the island image asset
     */
    public static Image getIslandPng() {
        if (islandPng == null) islandPng = loadImageFromDisk("/img/island.png");
        return islandPng;
    }

    /**
     * Returns the island block image asset
     *
     * @return the island block image asset
     */
    public static Image getBlockPng() {
        if (blockPng == null) blockPng = loadImageFromDisk("/img/block.png");
        return blockPng;
    }

    /**
     * Returns the mother nature image asset
     *
     * @return the mother nature image asset
     */
    public static Image getMnPng() {
        if (mnPng == null) mnPng = loadImageFromDisk("/img/motherNature.png");
        return mnPng;
    }

    /**
     * Returns a {@link Map} linking each assistant asset with its {@link AssistantType}
     *
     * @return a {@link Map} linking each assistant asset with its {@link AssistantType}
     */
    public static EnumMap<AssistantType, Image> getAssistantPngs() {
        if (assistantPngs == null) {
            assistantPngs = new EnumMap<>(AssistantType.class);
            forEachEnumValueLoadImage(AssistantType.values(), "/img/assistants/", assistantPngs::put);
        }
        return assistantPngs;
    }

    public static Image getBoardPng() {
        if (boardPng == null) boardPng = loadImageFromDisk("/img/board.png");
        return boardPng;
    }

    /**
     * Returns a {@link Map} linking each professor asset with its {@link PieceColor}
     *
     * @return a {@link Map} linking each professor asset with its {@link PieceColor}
     */
    public static EnumMap<PieceColor, Image> getProfessorPngs() {
        if (professorPngs == null) {
            professorPngs = new EnumMap<>(PieceColor.class);
            forEachEnumValueLoadImage(PieceColor.values(), "/img/professors/", professorPngs::put);
        }
        return professorPngs;
    }

    /**
     * Returns a {@link Map} linking each mage asset with its {@link Mage}
     *
     * @return a {@link Map} linking each mage asset with its {@link Mage}
     */
    public static EnumMap<Mage, Image> getMagePngs() {
        if (magePngs == null) {
            magePngs = new EnumMap<>(Mage.class);
            forEachEnumValueLoadImage(Mage.values(), "/img/mages/", magePngs::put);
        }
        return magePngs;
    }

    /**
     * Returns a {@link Map} linking each character asset with its {@link CharacterType}
     *
     * @return a {@link Map} linking each character asset with its {@link CharacterType}
     */
    public static Map<CharacterType, Image> getCharacterPngs() {
        if (characterPngs == null) {
            characterPngs = new EnumMap<>(CharacterType.class);
            forEachEnumValueLoadImage(CharacterType.values(), "/img/characters/", characterPngs::put);
        }
        return characterPngs;
    }

    /**
     * Returns the two sack state image assets (empty and full in order)
     *
     * @return the two sack state image assets (empty and full in order)
     */
    public static Tuple<Image, Image> getSackPngs() {
        if (sackPngs == null) {
            sackPngs = new Tuple<>(
                    loadImageFromDisk("/img/sack/empty.png"),
                    loadImageFromDisk("/img/sack/full.png"));
        }
        return sackPngs;
    }

    /**
     * Returns the coin image asset
     *
     * @return the coin image asset
     */
    public static Image getCoinPng() {
        if (coinPng == null) coinPng = loadImageFromDisk("/img/coin.png");
        return coinPng;
    }

    /**
     * Returns the cloud image asset
     *
     * @return the cloud image asset
     */
    public static Image getCloudPng() {
        if (cloudPng == null) cloudPng = loadImageFromDisk("/img/cloud.png");
        return cloudPng;
    }
}
