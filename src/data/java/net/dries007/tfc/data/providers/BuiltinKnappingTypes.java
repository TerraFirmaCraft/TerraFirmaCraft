package net.dries007.tfc.data.providers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.crafting.SizedIngredient;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.blocks.rock.Rock;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.util.data.KnappingType;
import net.dries007.tfc.util.registry.HolderHolder;

public class BuiltinKnappingTypes extends DataManagerProvider<KnappingType>
{
    public BuiltinKnappingTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(KnappingType.MANAGER, output, lookup);
    }

    @Override
    protected void addData()
    {
        add("rock",
            Ingredient.of(TFCTags.Items.ROCK_KNAPPING), 2, 1,
            TFCSounds.KNAP_STONE,
            false, false, true,
            TFCBlocks.ROCK_BLOCKS.get(Rock.GRANITE).get(Rock.BlockType.LOOSE));
        add("clay",
            Ingredient.of(Items.CLAY_BALL), 5, 5,
            TFCSounds.KNAP_CLAY,
            true, true, false,
            Items.CLAY_BALL);
        add("fire_clay",
            Ingredient.of(TFCItems.FIRE_CLAY), 5, 5,
            TFCSounds.KNAP_CLAY,
            true, true, false,
            TFCItems.FIRE_CLAY);
        add("leather",
            Ingredient.of(Items.LEATHER), 1, 1,
            TFCSounds.KNAP_LEATHER,
            false, false, false,
            Items.LEATHER);
        add("goat_horn",
            Ingredient.of(TFCItems.GOAT_HORN), 1, 1,
            TFCSounds.KNAP_STONE,
            false, false, false,
            TFCItems.GOAT_HORN);
    }

    private void add(String name, Ingredient item, int amount, int consumeAmount, HolderHolder<SoundEvent> sound, boolean consumeAfterComplete, boolean useDisabledTexture, boolean spawnsParticles, ItemLike jeiIcon)
    {
        add(name, new KnappingType(new SizedIngredient(item, amount), amount == consumeAmount ? Optional.empty() : Optional.of(consumeAmount), sound.holder(), consumeAfterComplete, useDisabledTexture, spawnsParticles, new ItemStack(jeiIcon)));
    }
}
