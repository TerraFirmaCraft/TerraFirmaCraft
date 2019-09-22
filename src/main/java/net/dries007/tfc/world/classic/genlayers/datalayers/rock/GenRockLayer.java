/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.classic.genlayers.datalayers.rock;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.world.classic.genlayers.*;

public abstract class GenRockLayer extends GenLayerTFC
{
    public static GenLayerTFC initialize(long seed, RockCategory.Layer level)
    {
        GenLayerTFC layer = new GenLayerRockInit(1L, TFCRegistries.ROCKS.getValuesCollection().stream().filter(level).toArray(Rock[]::new));
//        drawImage(512, layer, "Rock 0");
        layer = new GenLayerFuzzyZoomTFC(2000L, layer);
//        drawImage(512, layer, "Rock 1");
        //layer = new GenLayerAddRock(1L, layer);
//        drawImage(512, layer, "Rock 2");
        layer = new GenLayerZoomTFC(2001L, layer);
        //layer = new GenLayerAddRock(2L, layer);
//        drawImage(512, layer, "Rock 3");
        layer = new GenLayerZoomTFC(2002L, layer);
        //layer = new GenLayerAddRock(3L, layer);
//        drawImage(512, layer, "Rock 4");
        layer = new GenLayerZoomTFC(2003L, layer);
        //layer = new GenLayerAddRock(4L, layer);
//        drawImage(512, layer, "Rock 5");
        layer = new GenLayerSmoothTFC(1000L, layer);
//        drawImage(512, layer, "Rock 6");
        for (int zoomLevel = 0; zoomLevel < 5; ++zoomLevel)
        {
            layer = new GenLayerZoomTFC(1000 + zoomLevel, layer);
//            drawImage(512, layer, "Rock " + (7 + zoomLevel));
        }

        GenLayerSmoothTFC smoothedLayer = new GenLayerSmoothTFC(1000L, layer);
        GenLayerVoronoiZoomTFC voronoiLayer = new GenLayerVoronoiZoomTFC(10L, smoothedLayer);
//        drawImage(512, layer, "Rock Final");
        smoothedLayer.initWorldGenSeed(seed);
        voronoiLayer.initWorldGenSeed(seed);
        return voronoiLayer;
    }

    public GenRockLayer(long par1)
    {
        super(par1);
    }
}
