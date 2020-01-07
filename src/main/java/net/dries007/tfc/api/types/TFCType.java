package net.dries007.tfc.api.types;

import javax.annotation.Nonnull;

import net.minecraft.util.ResourceLocation;

public class TFCType
{
    private ResourceLocation id;

    @Nonnull
    public ResourceLocation getId()
    {
        return id;
    }

    public void setId(ResourceLocation id)
    {
        this.id = id;
    }
}
