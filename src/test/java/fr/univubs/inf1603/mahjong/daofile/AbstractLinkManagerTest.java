/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univubs.inf1603.mahjong.daofile;

import java.nio.ByteBuffer;
import java.util.UUID;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author aliyou
 */
public class AbstractLinkManagerTest {
    
    public AbstractLinkManagerTest() {
    }

    /**
     * Test of readRow method, of class AbstractLinkManager.
     */
    @Test
    public void testReadRow() {
        System.out.println("readRow");
        ByteBuffer buffer = null;
        long rowPointer = 0L;
        AbstractLinkManager instance = null;
        LinkRow expResult = null;
        LinkRow result = instance.readRow(buffer, rowPointer);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addLink method, of class AbstractLinkManager.
     */
    @Test
    public void testAddLink() throws Exception {
        System.out.println("addLink");
        LinkRow.Link link = null;
        AbstractLinkManager instance = null;
        instance.addLink(link);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of removeLink method, of class AbstractLinkManager.
     */
    @Test
    public void testRemoveLink() throws Exception {
        System.out.println("removeLink");
        UUID dataID = null;
        AbstractLinkManager instance = null;
        instance.removeLink(dataID);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}
