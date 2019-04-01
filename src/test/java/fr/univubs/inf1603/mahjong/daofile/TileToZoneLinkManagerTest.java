/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univubs.inf1603.mahjong.daofile;

import fr.univubs.inf1603.mahjong.dao.fake_engine.GameTile;
import java.util.ArrayList;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class TileToZoneLinkManagerTest {
    
    public TileToZoneLinkManagerTest() {
    }

    /**
     * Test of addTiles method, of class TileToZoneLinkManager.
     */
    @Test
    public void testAddTiles() throws Exception {
        System.out.println("addTiles");
        UUID zoneID = null;
        ArrayList<GameTile> tiles = null;
        TileToZoneLinkManager instance = null;
        instance.addTiles(zoneID, tiles);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeTiles method, of class TileToZoneLinkManager.
     */
    @Test
    public void testRemoveTiles() throws Exception {
        System.out.println("removeTiles");
        UUID zoneID = null;
        TileToZoneLinkManager instance = null;
        //instance.removeTiles(zoneID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of loadTilesCollection method, of class TileToZoneLinkManager.
     */
    @Test
    public void testLoadTilesCollection() throws Exception {
        System.out.println("loadTilesCollection");
        UUID zoneID = null;
        TileToZoneLinkManager instance = null;
        ArrayList<GameTile> expResult = null;
        ArrayList<GameTile> result = instance.loadTilesCollection(zoneID);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
