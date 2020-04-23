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
        long seed = 1234;

        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);

        IAreaFactory<LazyArea> mainLayer;
        int layerCount = 0;

        IMAGES.color(LayerDrawingUtil::landColor).size(40);

        // Ocean / Continents

        mainLayer = VoronoiIslandLayer.INSTANCE.apply(contextFactory.apply(1000L));
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -20, -20, 20, 20);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1002L), mainLayer);
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -20, -20, 20, 20);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1004L), mainLayer);
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);

        mainLayer = ZoomLayer.FUZZY.apply(contextFactory.apply(1001L), mainLayer);
        IMAGES.size(40).draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -20, -20, 20, 20);

        mainLayer = AddIslandLayer.NORMAL.apply(contextFactory.apply(1004L), mainLayer);
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);

        mainLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1006L), mainLayer);
        IMAGES.draw("layer_land_" + ++layerCount, mainLayer, 0, 0, -40, -40, 40, 40);
    }
}
