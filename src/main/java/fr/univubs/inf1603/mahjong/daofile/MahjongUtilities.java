
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.FlowerTile;
import fr.univubs.inf1603.mahjong.engine.rule.SeasonTile;
import fr.univubs.inf1603.mahjong.engine.rule.SuperiorHonor;
import fr.univubs.inf1603.mahjong.Wind;

/**
 *
 * @author aliyou
 * @version 1.1.0
 */
public class MahjongUtilities {

    /**
     * Rétourne le vent <code>Wind</code> correspondant au carctère passé en
     * paramètre <code>c</code>.
     *
     * @param c Caractère.
     * @return Vent <code>Wind</code> si le caractère correspond à un symbole de
     * vent sinon <code>null</code>.
     */
    public static Wind getWind(char c) {
//        System.out.println("Wind -> c : " + c);
        for (Wind wind : Wind.values()) {
            if (wind.getSymbol() == c) {
                return wind;
            }
        }
        return null;
    }

    /**
     * Rétourne le dragon  <code>SuperiorHonor.Dragon</code>
     * {@link SuperiorHonor.Dragon} correspondant au carctère passé en paramètre
     * <code>c</code>.
     *
     * @param c Caractère.
     * @return Dargon <code>SuperiorHonor.Dragon</code> si le caractère
     * correspond à un symbole de dragon sinon <code>null</code>.
     */
    public static SuperiorHonor.Dragon getDragon(char c) {
        for (SuperiorHonor.Dragon dragon : SuperiorHonor.Dragon.values()) {
            if (dragon.getSymbol() == c) {
                return dragon;
            }
        }
        return null;
    }

    /**
     * Rétourne la saison <code>SeasonTile.Season</code>
     * {@link SeasonTile.Season} correspondant au vent passé en paramètre
     * <code>wind</code>.
     *
     * @param wind Vent.
     * @return Saison  <code>SeasonTile.Season</code> si le vent
     * <code>wind</code> correspond à un vent <code>Wind</code> de saison sinon
     * <code>null</code>.
     */
    public static SeasonTile.Season getSeason(Wind wind) {
        for (SeasonTile.Season season : SeasonTile.Season.values()) {
            if (season.getWind() == wind) {
                return season;
            }
        }
        return null;
    }

    /**
     * Rétourne la fleur <code>FlowerTile.Flower</code>
     * {@link FlowerTile.Flower} correspondant au vent passé en paramètre
     * <code>wind</code>.
     *
     * @param wind Vent.
     * @return Fleur <code>FlowerTile.Flower</code> si le vent <code>wind</code>
     * correspond à un vent <code>Wind</code> de fleur
     * <code>FlowerTile.Flower</code> sinon <code>null</code>.
     */
    public static FlowerTile.Flower getFlower(Wind wind) {
        for (FlowerTile.Flower flower : FlowerTile.Flower.values()) {
            if (flower.getWind().equals(wind)) {
                return flower;
            }
        }
        return null;
    }

    /**
     * Rétourne la famille <code>CommontTile.Family</code>
     * {@link CommonTile.Family} correspondant au caractère passé en paramètre
     * <code>c</code>.
     *
     * @param c Caractère.
     * @return Famille <code>CommontTile.Family</code> si le caractère
     * correspond à un symbole de famille <code>CommontTile.Family</code> sinon
     * <code>null</code>.
     */
    public static CommonTile.Family getFamily(char c) {
        for (CommonTile.Family family : CommonTile.Family.values()) {
            if (family.getSymbol() == c) {
                return family;
            }
        }
        return null;
    }

    /**
     * Rétourne le Numéro <code>CommontTile.Number</code>
     * {@link CommonTile.Number} correspondant à la valeur passé en paramètre
     * <code>value</code>.
     *
     * @param value Valeur.
     * @return Numéro <code>CommontTile.Number</code> si la valeur correspond à
     * une valeur de numéro <code>CommontTile.Number</code> sinon
     * <code>null</code>.
     */
    public static CommonTile.Number getNumber(int value) {
        for (CommonTile.Number number : CommonTile.Number.values()) {
            if (number.getValue() == value) {
                return number;
            }
        }
        return null;
    }
}
