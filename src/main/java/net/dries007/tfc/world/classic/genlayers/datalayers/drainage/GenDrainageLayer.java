/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.drainage;

import net.dries007.tfc.world.classic.DataLayer;
import net.dries007.tfc.world.classic.genlayers.GenLayerFuzzyZoomTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerSmoothTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerZoomTFC;

public abstract class GenDrainageLayer extends GenLayerTFC
{
    public static final int MIN = DataLayer.DRAINAGE_NONE.layerID;
    public static final int MAX = DataLayer.DRAINAGE_VERY_GOOD.layerID;

    public static GenLayerTFC initialize(long seed)
    {
        GenLayerTFC continent = new GenLayerDrainageInit(1L);
        continent = new GenLayerAddDrainage(1L, continent);
        continent = new GenLayerFuzzyZoomTFC(2000L, continent);
        continent = new GenLayerAddDrainage(1L, continent);
        continent = new GenLayerZoomTFC(2001L, continent);
        continent = new GenLayerAddDrainage(2L, continent);
        continent = new GenLayerDrainageMix(88L, continent);
        continent = new GenLayerZoomTFC(2002L, continent);
        continent = new GenLayerAddDrainage(3L, continent);
        continent = new GenLayerZoomTFC(2003L, continent);
        continent = new GenLayerAddDrainage(4L, continent);
        continent = GenLayerZoomTFC.magnify(1000L, continent, 2);
        continent = new GenLayerSmoothTFC(1000L, continent);
        continent = new GenLayerDrainageMix(1000, continent);
        continent = new GenLayerZoomTFC(1000, continent);
        continent = new GenLayerZoomTFC(1001, continent);
        continent = new GenLayerSmoothTFC(1000L, continent);
        continent.initWorldGenSeed(seed);
        drawImage(1024, continent, "drainage");
        return continent;
    }

    public GenDrainageLayer(long par1)
    {
        super(par1);
    }
}
