/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.ph;

import net.dries007.tfc.world.classic.DataLayer;
import net.dries007.tfc.world.classic.genlayers.GenLayerFuzzyZoomTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerSmoothTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerTFC;
import net.dries007.tfc.world.classic.genlayers.GenLayerZoomTFC;

public abstract class GenPHLayer extends GenLayerTFC
{
    public static final int MIN = DataLayer.PH_ACID_HIGH.layerID;
    public static final int MAX = DataLayer.PH_ALKALINE_HIGH.layerID;

    public static GenLayerTFC initializePH(long seed)
    {
        GenLayerTFC continent = new GenLayerPHInit(1L);
        continent = new GenLayerAddPH(1L, continent);
        continent = new GenLayerFuzzyZoomTFC(2000L, continent);
        continent = new GenLayerAddPH(1L, continent);
        continent = new GenLayerZoomTFC(2001L, continent);
        continent = new GenLayerAddPH(2L, continent);
        continent = new GenLayerPHMix(88L, continent);
        continent = new GenLayerZoomTFC(2002L, continent);
        continent = new GenLayerAddPH(3L, continent);
        continent = new GenLayerZoomTFC(2003L, continent);
        continent = new GenLayerAddPH(4L, continent);
        continent = GenLayerZoomTFC.magnify(1000L, continent, 2);
        continent = new GenLayerSmoothTFC(1000L, continent);
        continent = new GenLayerPHMix(1000, continent);
        continent = new GenLayerZoomTFC(1000, continent);
        continent = new GenLayerZoomTFC(1000 + 1, continent);
        continent = new GenLayerSmoothTFC(1000L, continent);
        continent.initWorldGenSeed(seed);
        return continent;
    }

    public GenPHLayer(long par1)
    {
        super(par1);
    }
}
