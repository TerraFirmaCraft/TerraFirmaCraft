package net.dries007.tfc.world.layer;

import java.util.function.LongFunction;

import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;

import static net.dries007.tfc.world.layer.LayerDrawingUtil.IMAGES;

public class LayerDrawingTests
{
    public static void main(String[] args)
    {
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, 1243, seedModifier);

        IAreaFactory<LazyArea> mainLayer, riverLayer;
        int layerCount = 0;

        IMAGES.color(LayerDrawingUtil::landColor).size(20);

        // Ocean / Continents

        mainLayer = new IslandLayer(8).apply(contextFactory.apply(1000L));
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -10, -10, 10, 10);

        mainLayer = ZoomLayer.FUZZY.apply(contextFactory.apply(1001L), mainLayer);
        IMAGES.size(40).draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -20, -20, 20, 20);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1002L), mainLayer);
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -20, -20, 20, 20);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1003L), mainLayer);
        IMAGES.size(80).draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1004L), mainLayer);
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = AddIslandLayer.HEAVY.apply(contextFactory.apply(1005L + 2 * i), mainLayer);
            IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);

            mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1006L + 2 * i), mainLayer);
            IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);
        }

        // Oceans and Continents => Elevation Mapping

        IMAGES.color(LayerDrawingUtil::elevationColor);
        layerCount = 0;

        mainLayer = ElevationLayer.INSTANCE.apply(contextFactory.apply(1009L), mainLayer);
        IMAGES.draw("layer_elevation_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1010L), mainLayer);
        IMAGES.size(160).draw("layer_elevation_" + ++layerCount, mainLayer, -80, -80, -80, -80, 80, 80);

        // Elevation Mapping => Rivers
        layerCount = 0;

        riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1011L), mainLayer);
        IMAGES.draw("layer_river_" + ++layerCount, riverLayer, 0, 0, -80, -80, 80, 80);

        for (int i = 0; i < 6; i++)
        {
            riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1012L + i), riverLayer);
            IMAGES.draw("layer_river_" + ++layerCount, riverLayer, 0, 0, -80, -80, 80, 80);
        }

        riverLayer = RiverLayer.INSTANCE.apply(contextFactory.apply(1018L), riverLayer);
        IMAGES.size(640).color(LayerDrawingUtil::riverColor).draw("layer_river_" + ++layerCount, riverLayer, 0, 0, -320, -320, 320, 320);

        riverLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1019L), riverLayer);
        IMAGES.draw("layer_river_" + ++layerCount, riverLayer, 0, 0, -80, -80, 80, 80);

        // Elevation Mapping => Biomes

        IMAGES.color(LayerDrawingUtil::biomeColor);
        layerCount = 0;

        mainLayer = BiomeLayer.INSTANCE.apply(contextFactory.apply(1011L), mainLayer);
        IMAGES.size(160).draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -80, -80, 80, 80);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1012L), mainLayer);
        IMAGES.size(320).draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -160, -160, 160, 160);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1013L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -160, -160, 160, 160);

        mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1014L), mainLayer);
        IMAGES.size(640).draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = RemoveOceanLayer.INSTANCE.apply(contextFactory.apply(1015L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = OceanLayer.INSTANCE.apply(contextFactory.apply(1016L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = EdgeBiomeLayer.INSTANCE.apply(contextFactory.apply(1017L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = AddLakeLayer.INSTANCE.apply(contextFactory.apply(1018L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        for (int i = 0; i < 4; i++)
        {
            mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1019L), mainLayer);
            IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);
        }

        mainLayer = ShoreLayer.INSTANCE.apply(contextFactory.apply(1023L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        for (int i = 0; i < 2; i++)
        {
            mainLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1024L), mainLayer);
            IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);
        }

        mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1025L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = MixRiverLayer.INSTANCE.apply(contextFactory.apply(1026L), mainLayer, riverLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = BiomeRiverWidenLayer.MEDIUM.apply(contextFactory.apply(1027L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer = BiomeRiverWidenLayer.LOW.apply(contextFactory.apply(1028L), mainLayer);
        IMAGES.draw("layer_biome_" + ++layerCount, mainLayer, 0, 0, -320, -320, 320, 320);

        mainLayer.make().getValue(0, 0);

    }
}
