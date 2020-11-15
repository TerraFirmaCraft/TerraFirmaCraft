/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.function.Function;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.Codecs;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SuppressWarnings("unused")
public class TFCSurfaceBuilders
{
    public static final DeferredRegister<SurfaceBuilder<?>> SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, MOD_ID);

    // Alternate codecs for surface builder configs
    public static final Codec<SurfaceBuilderConfig> NOOP_CODEC = Codec.unit(SurfaceBuilder.CONFIG_STONE);
    public static final Codec<SurfaceBuilderConfig> LENIENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codecs.LENIENT_BLOCKSTATE.fieldOf("top_material").forGetter(SurfaceBuilderConfig::getTopMaterial),
        Codecs.LENIENT_BLOCKSTATE.fieldOf("under_material").forGetter(SurfaceBuilderConfig::getUnderMaterial)
    ).apply(instance, (topMaterial, underMaterial) -> new SurfaceBuilderConfig(topMaterial, underMaterial, Blocks.AIR.defaultBlockState())));

    public static final RegistryObject<NormalSurfaceBuilder> NORMAL = register("normal", NormalSurfaceBuilder::new, LENIENT_CODEC);
    public static final RegistryObject<ThinSurfaceBuilder> THIN = register("thin", ThinSurfaceBuilder::new, LENIENT_CODEC);

    public static final RegistryObject<BadlandsSurfaceBuilder> BADLANDS = register("badlands", BadlandsSurfaceBuilder::new, LENIENT_CODEC);
    public static final RegistryObject<MountainSurfaceBuilder> MOUNTAINS = register("mountains", MountainSurfaceBuilder::new, NOOP_CODEC);
    public static final RegistryObject<ShoreSurfaceBuilder> SHORE = register("shore", ShoreSurfaceBuilder::new, NOOP_CODEC);
    public static final RegistryObject<UnderwaterSurfaceBuilder> UNDERWATER = register("underwater", UnderwaterSurfaceBuilder::new, NOOP_CODEC);

    public static final RegistryObject<GlacierSurfaceBuilder> WITH_GLACIERS = register("with_glaciers", GlacierSurfaceBuilder::new, ParentedSurfaceBuilderConfig.CODEC);

    // Used for shores - red sand = normal beach sand, sandstone = variant beach sand (pink / black)
    public static final SurfaceBuilderConfig RED_SAND_CONFIG = config(Blocks.RED_SAND);
    public static final SurfaceBuilderConfig RED_SANDSTONE_CONFIG = config(Blocks.RED_SANDSTONE);
    public static final SurfaceBuilderConfig COBBLE_COBBLE_RED_SAND_CONFIG = config(Blocks.COBBLESTONE, Blocks.COBBLESTONE, Blocks.RED_SAND);

    private static <C extends ISurfaceBuilderConfig, S extends SurfaceBuilder<C>> RegistryObject<S> register(String name, Function<Codec<C>, S> factory, Codec<C> codec)
    {
        return SURFACE_BUILDERS.register(name, () -> factory.apply(codec));
    }

    private static SurfaceBuilderConfig config(Block all)
    {
        return config(all, all, all);
    }

    private static SurfaceBuilderConfig config(Block top, Block under, Block underwater)
    {
        return new SurfaceBuilderConfig(top.defaultBlockState(), under.defaultBlockState(), underwater.defaultBlockState());
    }
}