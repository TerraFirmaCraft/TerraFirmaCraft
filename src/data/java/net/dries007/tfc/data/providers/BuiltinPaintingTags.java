package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.PaintingVariantTags;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;

public class BuiltinPaintingTags extends TagsProvider<PaintingVariant>
{
    public BuiltinPaintingTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> lookupProvider)
    {
        super(event.getGenerator().getPackOutput(), Registries.PAINTING_VARIANT, lookupProvider, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        provider.lookupOrThrow(Registries.PAINTING_VARIANT)
            .listElementIds()
            .filter(e -> e.location().getNamespace().equals(TerraFirmaCraft.MOD_ID))
            .forEach(tag(PaintingVariantTags.PLACEABLE)::add);
    }
}
