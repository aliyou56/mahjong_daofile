/*
 * 
AbstractTile (abstract)
 	|_ComplexTile (abstract)
 		|_CommonTile
 	|_HonorTile (abstract)
 		|_SuperiorHonor
 		|_WindHonor
 			|_SimpleHonor
 			|_SupremeHonor (abstract)
 				|_FlowerTile
 				|_SeasonTile
        
 public enum Season { 
        SPRING(1,WindHonor.Wind.EAST), SUMMER(2,WindHonor.Wind.SOUTH), AUTUMN(3,WindHonor.Wind.WEST), WINTER(4,WindHonor.Wind.NORTH);
        private final int value;
        private final WindHonor.Wind wind;

        private Season(int value,WindHonor.Wind wind){
            this.value = value;
            this.wind = wind;
        }
        
        public WindHonor.Wind getWind(){
            return this.wind;
        }
        
        public int getValue(){ 
            return this.value;
        }
}
 */
package fr.univubs.inf1603.mahjong;

import fr.univubs.inf1603.mahjong.engine.rule.AbstractTile;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile.Family;
import fr.univubs.inf1603.mahjong.engine.rule.CommonTile.Number;

/**
 *
 * @author aliyou
 */
public class AbstractTileFactory {
    
    public AbstractTile createCommonTile(Family f, Number n) {
        return new CommonTile(f, n);
    }
    
    public AbstractTile createHonorTile() {
        return null;
    }
}
