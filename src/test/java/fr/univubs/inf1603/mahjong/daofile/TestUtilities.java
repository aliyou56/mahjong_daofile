
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.Wind;
import fr.univubs.inf1603.mahjong.engine.game.Game;
import fr.univubs.inf1603.mahjong.engine.game.GameException;
import fr.univubs.inf1603.mahjong.engine.game.GameTile;
import fr.univubs.inf1603.mahjong.engine.game.GameTileInterface;
import fr.univubs.inf1603.mahjong.engine.game.TileZone;
import java.util.ArrayList;
import java.util.Comparator;
import org.junit.Assert;

/**
 *
 * @author aliyou
 * @version 1.3
 */
public class TestUtilities {
    
    /**
     * Compare deux tuiles.
     * 
     * @param gti1 Tuile 1
     * @param gti2 Tuile 2
     */
    public static void assertTest(GameTileInterface gti1, GameTileInterface gti2) {
        Assert.assertEquals(gti1.getUUID(), gti2.getUUID());
        Assert.assertEquals(gti1.getGameID(), gti2.getGameID());
        Assert.assertEquals(gti1.getOrientation(), gti2.getOrientation());
        Assert.assertEquals(gti1.getTile().toNormalizedName(), gti2.getTile().toNormalizedName());
        
        if (gti1 instanceof GameTile && gti2 instanceof GameTile) {
            GameTile gt1 = (GameTile) gti1;
            GameTile gt2 = (GameTile) gti2;
            Assert.assertEquals(gt1.isPubliclyVisible(), gt2.isPubliclyVisible());
        }
    }
    
    public static Comparator<GameTileInterface> tileComparator() {
        return (GameTileInterface o1, GameTileInterface o2) -> {
            return o1.getGameID() - o2.getGameID();
        };
    }
    
    /**
     * Compare deux zones.
     * 
     * @param tz1 Zone 1
     * @param tz2 Zone 2
     */
    public static void assertTest(TileZone tz1, TileZone tz2) {
        Assert.assertEquals(tz1.getUUID(), tz2.getUUID());
        Assert.assertEquals(tz1.getIdentifier(), tz2.getIdentifier());
        ArrayList<GameTileInterface> tilesTz1 = tz1.getTiles();
        ArrayList<GameTileInterface> tilesTz2 = tz2.getTiles();
        tilesTz1.sort(tileComparator());
        tilesTz2.sort(tileComparator());
        Assert.assertEquals(tilesTz1.size(), tilesTz2.size());
        for(int i=0; i<tilesTz1.size(); i++) {
            assertTest(tilesTz1.get(i), tilesTz1.get(i));
        }
    }
    
    /**
     * Compare deux parties de Mahjong
     * 
     * @param game1 Game 1
     * @param game2 Game 2
     */
    public static void assertTest(Game game1, Game game2) {
       try {
            Assert.assertEquals(game1.getUUID(), game1.getUUID());
            Assert.assertEquals(game1.getCurrentwind(), game2.getCurrentwind());
            Assert.assertEquals(game1.getPlayingTime(), game2.getPlayingTime());
            Assert.assertEquals(game1.getStealingTime(), game2.getStealingTime());
            Assert.assertEquals(game1.getRule().getName(), game2.getRule().getName());
            StringBuilder sb = new StringBuilder();
            Wind[] w1 = game1.getPlayerWinds();
            Wind[] w2 = game2.getPlayerWinds();
            Assert.assertArrayEquals(game1.getPlayerWinds(), game2.getPlayerWinds());
            Assert.assertArrayEquals(game1.getAllPlayerPoints(), game2.getAllPlayerPoints());
            Assert.assertTrue(game1.getLastPlayedMove().isEqual(game2.getLastPlayedMove()));
            sb.append("\n game1 \t\t game2");
            for (int i=0; i<w1.length; i++) {
                sb.append("\n ").append(w1[i]).append(" \t\t ").append(w2[i]);
            }
            sb.append("\n");
//            if (game1 instanceof MahjongGame && game2 instanceof MahjongGame) {
//                MahjongGame mahjongGame1 = (MahjongGame) game1;
//                MahjongGame mahjongGame2 = (MahjongGame) game2;
//                
//            }
            sb.append("\n");
            System.out.println(sb.toString());
        } catch (GameException ex) {
            ex.printStackTrace(System.err);
        }
    }
}
