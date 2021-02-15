/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
        ConfiguredSurfaceBuilder.field_244393_b_.fieldOf("parent").forGetter(c -> c.parent)//CODEC
    ).apply(instance, ParentedSurfaceBuilderConfig::new));

    private final Supplier<ConfiguredSurfaceBuilder<?>> parent;

    public ParentedSurfaceBuilderConfig(Supplier<ConfiguredSurfaceBuilder<?>> parent)
    {
        super(Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState(), Blocks.AIR.getDefaultState());
        this.parent = parent;
    }

    public ConfiguredSurfaceBuilder<?> getParent()
    {
        return parent.get();
    }
}
