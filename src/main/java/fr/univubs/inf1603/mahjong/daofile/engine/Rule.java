/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.univubs.inf1603.mahjong.daofile.engine;

import fr.univubs.inf1603.mahjong.engine.rule.BoardRule;
import fr.univubs.inf1603.mahjong.engine.rule.GameRule;
import fr.univubs.inf1603.mahjong.engine.rule.ScoringSystem;

/**
 *
 * @author aliyou
 */
public class Rule implements GameRule {

    @Override
    public String getName() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String getDescription() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public ScoringSystem getScoringSystem() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BoardRule getBoardRule() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
