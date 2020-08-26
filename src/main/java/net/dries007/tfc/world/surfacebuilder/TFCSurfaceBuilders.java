/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCSurfaceBuilders
{
    public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, MOD_ID);

    public static final RegistryObject<NormalSurfaceBuilder> NORMAL = register("normal", NormalSurfaceBuilder::new);
    public static final RegistryObject<ThinSurfaceBuilder> THIN = register("thin", ThinSurfaceBuilder::new);
    public static final RegistryObject<DeepSurfaceBuilder> DEEP = register("deep", DeepSurfaceBuilder::new);

    public static final RegistryObject<BadlandsSurfaceBuilder> BADLANDS = register("badlands", BadlandsSurfaceBuilder::new);
    public static final RegistryObject<MountainSurfaceBuilder> MOUNTAINS = register("mountains", MountainSurfaceBuilder::new);
    public static final RegistryObject<ShoreSurfaceBuilder> SHORE = register("shore", ShoreSurfaceBuilder::new);
    public static final RegistryObject<UnderwaterSurfaceBuilder> UNDERWATER = register("underwater", UnderwaterSurfaceBuilder::new);

    // Used for shores - red sand = normal beach sand, sandstone = variant beach sand (pink / black)
    public static final SurfaceBuilderConfig RED_SAND_CONFIG = register(Blocks.RED_SAND);
    public static final SurfaceBuilderConfig RED_SANDSTONE_CONFIG = register(Blocks.RED_SANDSTONE);

    public static final SurfaceBuilderConfig COBBLE_COBBLE_GRAVEL_CONFIG = register(Blocks.COBBLESTONE, Blocks.COBBLESTONE, Blocks.GRAVEL);

    public static final DeepSurfaceBuilderConfig GRASS_DIRT_GRAVEL_GRAVEL_CONFIG = register(Blocks.GRASS_BLOCK, Blocks.DIRT, Blocks.GRAVEL, Blocks.GRAVEL);

    private static <C extends ISurfaceBuilderConfig, S extends SurfaceBuilder<C>> RegistryObject<S> register(String name, Supplier<S> factory)
    {
        return SURFACE_BUILDERS.register(name, factory);
    }

    private static SurfaceBuilderConfig register(Block all)
    {
        return register(all, all, all);
    }

    private static SurfaceBuilderConfig register(Block top, Block under, Block underwater)
    {
        return new SurfaceBuilderConfig(top.getDefaultState(), under.getDefaultState(), underwater.getDefaultState());
    }

    private static DeepSurfaceBuilderConfig register(Block top, Block under, Block deepUnder, Block underwater)
    {
        return new DeepSurfaceBuilderConfig(top.getDefaultState(), under.getDefaultState(), deepUnder.getDefaultState(), underwater.getDefaultState());
    }
}
