package net.dries007.tfc.data.providers;

import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;

import static net.dries007.tfc.common.TFCTags.Entities.*;

public class BuiltinEntityTags extends EntityTypeTagsProvider
{
    public BuiltinEntityTags(GatherDataEvent event, CompletableFuture<HolderLookup.Provider> provider)
    {
        super(event.getGenerator().getPackOutput(), provider, TerraFirmaCraft.MOD_ID, event.getExistingFileHelper());
    }

    @Override
    protected void addTags(HolderLookup.Provider provider)
    {
        // ===== Vanilla Tags ===== //

        // ===== TFC Tags ===== //

        tag(MONSTERS)
            .addTag(EntityTypeTags.UNDEAD)
            .add(
                EntityType.CREEPER,
                EntityType.SPIDER,
                EntityType.WITCH,
                EntityType.SLIME
            );
    }
}
