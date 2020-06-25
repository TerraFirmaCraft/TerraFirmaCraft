/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.noise;


import java.util.Random;
import java.util.function.LongFunction;
import java.util.function.Supplier;

import net.minecraft.world.gen.LazyAreaLayerContext;
import net.minecraft.world.gen.area.IAreaFactory;
import net.minecraft.world.gen.area.LazyArea;

import net.dries007.tfc.images.Colors;
import net.dries007.tfc.images.ImageUtil;
import net.dries007.tfc.world.layer.Noise2DLayer;
import net.dries007.tfc.world.layer.Plate;
import net.dries007.tfc.world.layer.PlateGenerationLayer;
import net.dries007.tfc.world.layer.traits.ISpecialAreaFactory;
import net.dries007.tfc.world.layer.traits.LazySpecialAreaLayerContext;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.SimplexNoise2D;
import net.dries007.tfc.world.noise.VoronoiNoise2D;

import static net.dries007.tfc.images.LayerImageUtil.LAYERS;


public class LayerDrawingTests
{
    private static final ImageUtil<INoise2D> NOISE2D = ImageUtil.get(target -> (x, y) -> target.noise((float) x, (float) y));
    private static final ImageUtil<ISpecialAreaFactory<Plate>> PLATES = ImageUtil.get(target -> (x, y) -> {
        Plate p = target.make().getValue((int) x, (int) y);
        return p.getElevation();
    });

    public static void main()
    {
        NOISE2D.color(Colors.LINEAR_GRAY);

        long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        LongFunction<LazyAreaLayerContext> contextFactory = seedModifier -> new LazyAreaLayerContext(25, seed, seedModifier);
        Supplier<LazyAreaLayerContext> contextSupplier = () -> contextFactory.apply(random.nextInt());

        INoise2D noise = new VoronoiNoise2D(seed);

        NOISE2D.draw("voronoi", noise);

        IAreaFactory<LazyArea> layer = new Noise2DLayer(new VoronoiNoise2D(seed, 1.8f, 2).spread(0.1f).scaled(0, 20)).apply(contextSupplier.get());
        LAYERS.color(Colors.DISCRETE_20).draw("voronoi_layer", layer);

        /*
        Goal:
         - Plate generation (voronoi -> Plate)
         - Plate improvements (Plate -> Plate)
         - Plate Boundary flags (Plate -> int)
         - Plate elevation base (Plate -> int)
         - Mix boundary + elevation (int, int -> int)
         */

        Supplier<LazySpecialAreaLayerContext<Plate>> plateContext = () -> new LazySpecialAreaLayerContext<>(25, seed, random.nextInt());

        ISpecialAreaFactory<Plate> plateLayer = new PlateGenerationLayer(new VoronoiNoise2D(seed), new SimplexNoise2D(seed).octaves(2)).apply(plateContext.get());
        PLATES.color(Colors.LINEAR_BLUE_RED).draw("plate_generation", plateLayer);
    }
}
