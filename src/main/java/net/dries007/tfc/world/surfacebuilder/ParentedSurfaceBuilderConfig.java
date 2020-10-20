package net.dries007.tfc.world.surfacebuilder;

import java.util.function.Supplier;

import net.minecraft.block.Blocks;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ParentedSurfaceBuilderConfig extends SurfaceBuilderConfig
{
    public static final Codec<ParentedSurfaceBuilderConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ConfiguredSurfaceBuilder.CODEC.fieldOf("parent").forGetter(c -> c.parent)
    ).apply(instance, ParentedSurfaceBuilderConfig::new));

    private final Supplier<ConfiguredSurfaceBuilder<?>> parent;

    public ParentedSurfaceBuilderConfig(Supplier<ConfiguredSurfaceBuilder<?>> parent)
    {
        super(Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState(), Blocks.AIR.defaultBlockState());
        this.parent = parent;
    }

    public ConfiguredSurfaceBuilder<?> getParent()
    {
        return parent.get();
    }
}
