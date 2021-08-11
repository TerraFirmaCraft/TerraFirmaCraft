/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.function.Supplier;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.level.levelgen.surfacebuilders.SurfaceBuilderBaseConfiguration;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ParentedSurfaceBuilderConfig extends SurfaceBuilderBaseConfiguration
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
