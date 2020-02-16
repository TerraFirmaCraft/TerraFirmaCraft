package net.dries007.tfc.api.types;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

public class TFCType
{
    private ResourceLocation name;

    @Nonnull
    public ResourceLocation getName()
    {
        return name;
    }

    public void setName(@Nonnull ResourceLocation name)
    {
        this.name = name;
    }
}
