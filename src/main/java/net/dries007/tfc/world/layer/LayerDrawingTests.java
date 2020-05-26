/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.layer;

import java.util.function.LongFunction;

import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.ZoomLayer;

import imageutil.Images;

import static net.dries007.tfc.world.layer.LayerDrawingUtil.IMAGES;

public class LayerDrawingTests
{
    public static void main(String[] args)
    {
        IMAGES.enable().color(Images.Colors.DISCRETE_20);

        long seed = System.currentTimeMillis();
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);
        IAreaFactory<LazyArea> seedLayer;
        int layerCount = 0;


        seedLayer = new RandomLayer(20).apply(contextFactory.apply(1000L));
        IMAGES.size(32).draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -16, -16, 16, 16);

        // The following results were obtained about the number of applications of this layer. (over 10 M samples each time)
        // None => 95.01% of adjacent pairs were equal (which lines up pretty good with theoretical predictions)
        // 1x => 98.49%
        // 2x => 99.42%
        // 3x => 99.54%
        // 4x => 99.55%
        // And thus we only apply once, as it's the best result to reduce adjacent pairs without too much effort / performance cost
        seedLayer = new RandomizeNeighborsLayer(20).apply(contextFactory.apply(1001L), seedLayer);
        IMAGES.size(32).draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -16, -16, 16, 16);

        IMAGES.size(640);
        for (int i = 0; i < 2; i++)
        {
            seedLayer = ExactZoomLayer.INSTANCE.apply(contextFactory.apply(1001L), seedLayer);
            IMAGES.draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -16, -16, 16, 16);

            seedLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1001L), seedLayer);
            IMAGES.draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -16, -16, 16, 16);

            seedLayer = SmoothLayer.INSTANCE.apply(contextFactory.apply(1001L), seedLayer);
            IMAGES.draw("layer_seed" + ++layerCount, seedLayer, 0, 20, -16, -16, 16, 16);
        }

        for (int i = 0; i < 6; i++)
        {
            seedLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1001L), seedLayer);
            IMAGES.draw("layer_seed" + ++layerCount, seedLayer, 0, 20, 0, 0, 10000, 10000);
        }

        seedLayer = ZoomLayer.NORMAL.apply(contextFactory.apply(1003L), seedLayer);
        IMAGES.size(1280).draw("layer_seed" + ++layerCount, seedLayer, 0, 20, 0, 0, 10000, 10000);
    }
}
