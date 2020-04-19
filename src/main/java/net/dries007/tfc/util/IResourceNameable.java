package net.dries007.tfc.util;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

public interface IResourceNameable
{
    @Nonnull
    ResourceLocation getId();

    void setId(@Nonnull ResourceLocation id);
}
