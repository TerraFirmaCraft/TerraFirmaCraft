/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;
import net.minecraft.world.gen.layer.SmoothLayer;
import net.minecraft.world.gen.layer.VoroniZoomLayer;
import net.minecraft.world.gen.layer.ZoomLayer;
import net.minecraft.world.gen.layer.traits.IAreaTransformer0;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

import imageutil.Images;
import net.dries007.tfc.world.gen.layer.TFCLayerUtil;

import static test.OverworldLayerTests.IMAGES;

/**
 * Test class for rock layer generation
 */
public class RockLayerTests
{
    private static final Rock[] ROCKS = Rock.values();

    public static void main(String[] args)
    {
        OverworldLayerTests.isTestMode = true;
        testRockLayers(System.currentTimeMillis());
    }

    private static List<IAreaFactory<LazyArea>> testRockLayers(long seed)
    {
        Random seedGenerator = new Random(seed);
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);
        Supplier<LazyAreaLayerContext> contextSupplier = () -> contextFactory.apply(seedGenerator.nextLong());

        IAreaFactory<LazyArea> areaFactory;
        int layerCount = 0;

        IMAGES.enable().color(Images.Colors.DISCRETE_20);

        IAreaTransformer0 rockLayer = (context, x, z) -> ROCKS[context.random(ROCKS.length)].ordinal();
        ICastleTransformer rockMixLayer = (context, north, west, south, east, center) -> {
            if (north == center || west == center || south == center || east == center)
            {
                return ROCKS[context.random(ROCKS.length)].ordinal();
            }
            return center;
        };

        areaFactory = rockLayer.apply(contextSupplier.get());
        IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.length, -30, -30, 30, 30);

        areaFactory = rockMixLayer.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.length, -30, -30, 30, 30);

        areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.length, -30, -30, 30, 30);

        areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.length, -30, -30, 30, 30);

        areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.length, -300, -300, 300, 300);

        areaFactory = ZoomLayer.NORMAL.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.length, -300, -300, 300, 300);

        areaFactory = SmoothLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.length, -300, -300, 300, 300);

        areaFactory = SmoothLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.length, -300, -300, 300, 300);

        areaFactory = TFCLayerUtil.repeat(ZoomLayer.NORMAL, 5, areaFactory, contextSupplier);
        IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.length, -3000, -3000, 3000, 3000);

        areaFactory = VoroniZoomLayer.INSTANCE.apply(contextSupplier.get(), areaFactory);
        IMAGES.draw("layer_rock_" + ++layerCount, areaFactory, 0, ROCKS.length, -3000, -3000, 3000, 3000);

        return Arrays.asList(areaFactory, areaFactory, areaFactory);
    }

    enum Rock
    {
        GRANITE,
        DIORITE,
        GABBRO,
        SHALE,
        CLAYSTONE,
        ROCKSALT,
        LIMESTONE,
        CONGLOMERATE,
        DOLOMITE,
        CHERT,
        CHALK,
        RHYOLITE,
        BASALT,
        ANDESITE,
        DACITE,
        QUARTZITE,
        SLATE,
        PHYLLITE,
        SCHIST,
        GNEISS,
        MARBLE
    }
}
