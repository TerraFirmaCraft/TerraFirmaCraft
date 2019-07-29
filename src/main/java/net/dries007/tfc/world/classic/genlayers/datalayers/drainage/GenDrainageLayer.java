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
        GenLayerTFC continent = genContinent(0);
//        drawImage(512, continent, "Drainage 0");
        continent = GenLayerZoomTFC.magnify(1000L, continent, 2);
        continent = new GenLayerSmoothTFC(1000L, continent);
//        drawImage(512, continent, "Drainage 1");
        for (int zoomLevel = 0; zoomLevel < 2; ++zoomLevel)
        {
            if (zoomLevel == 0)
            {
                continent = new GenLayerDrainageMix(1000 + zoomLevel, continent);
//                drawImage(512, continent, "Drainage 2-" + zoomLevel + " Mix");
            }
            continent = new GenLayerZoomTFC(1000 + zoomLevel, continent);
//            drawImage(512, continent, "Drainage 2-" + zoomLevel + " Smoothed");
        }

        GenLayerSmoothTFC finalCont = new GenLayerSmoothTFC(1000L, continent);
        //GenLayerTFC voronoiZoom = new GenLayerVoronoiZoomTFC(10L, finalCont);
//        drawImage(512, finalCont, "Drainage Final");
        finalCont.initWorldGenSeed(seed);
        return finalCont;
    }

    public static GenLayerTFC genContinent(long seed)
    {
        GenLayerTFC continent = new GenLayerDrainageInit(1L + seed);
//        drawImage(512, continent, "Drainage Init 0");
        continent = new GenLayerAddDrainage(1L, continent);
//        drawImage(512, continent, "Drainage Init 0b Add Drainage");
        continent = new GenLayerFuzzyZoomTFC(2000L, continent);
//        drawImage(512, continent, "Drainage Init 1");
        continent = new GenLayerAddDrainage(1L, continent);
//        drawImage(512, continent, "Drainage Init 2 Add Drainage");
        continent = new GenLayerZoomTFC(2001L, continent);
//        drawImage(512, continent, "Drainage Init 3 Zoom");
        continent = new GenLayerAddDrainage(2L, continent);
//        drawImage(512, continent, "Drainage Init 4 Add Drainage");
        continent = new GenLayerDrainageMix(88L, continent);
//        drawImage(512, continent, "Drainage Init 4b Mix");
        continent = new GenLayerZoomTFC(2002L, continent);
//        drawImage(512, continent, "Drainage Init 5 Zoom");
        continent = new GenLayerAddDrainage(3L, continent);
//        drawImage(512, continent, "Drainage Init 6 Add Drainage");
        continent = new GenLayerZoomTFC(2003L, continent);
//        drawImage(512, continent, "Drainage Init 7 Zoom");
        continent = new GenLayerAddDrainage(4L, continent);
//        drawImage(512, continent, "Drainage Init 8 Add Drainage");
        return continent;
    }

    public GenDrainageLayer(long par1)
    {
        super(par1);
    }
}
