package net.dries007.tfc.data.providers;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
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
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.data.KnappingType;
import net.dries007.tfc.util.registry.HolderHolder;

public class BuiltinKnappingTypes extends DataManagerProvider<KnappingType>
{
    public static final ResourceLocation ROCK = Helpers.identifier("rock");
    public static final ResourceLocation CLAY = Helpers.identifier("clay");
    public static final ResourceLocation FIRE_CLAY = Helpers.identifier("fire_clay");
    public static final ResourceLocation LEATHER = Helpers.identifier("leather");
    public static final ResourceLocation GOAT_HORN = Helpers.identifier("goat_horn");

    public BuiltinKnappingTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> lookup)
    {
        super(KnappingType.MANAGER, output, lookup);
    }

    @Override
    protected void addData(HolderLookup.Provider provider)
    {
        add(ROCK,
            TFCTags.Items.ROCK_KNAPPING, 2, 1,
            TFCSounds.KNAP_STONE,
            false, false, true,
            TFCBlocks.ROCK_BLOCKS.get(Rock.GRANITE).get(Rock.BlockType.LOOSE));
        add(CLAY,
            TFCTags.Items.CLAY_KNAPPING, 5, 5,
            TFCSounds.KNAP_CLAY,
            true, true, false,
            Items.CLAY_BALL);
        add(FIRE_CLAY,
            TFCTags.Items.FIRE_CLAY_KNAPPING, 5, 5,
            TFCSounds.KNAP_CLAY,
            true, true, false,
            TFCItems.FIRE_CLAY);
        add(LEATHER,
            TFCTags.Items.LEATHER_KNAPPING, 1, 1,
            TFCSounds.KNAP_LEATHER,
            false, false, false,
            Items.LEATHER);
        add(GOAT_HORN,
            TFCTags.Items.GOAT_HORN_KNAPPING, 1, 1,
            TFCSounds.KNAP_STONE,
            false, false, false,
            TFCItems.GOAT_HORN);
    }

    private void add(ResourceLocation name, TagKey<Item> item, int amount, int consumeAmount, HolderHolder<SoundEvent> sound, boolean consumeAfterComplete, boolean useDisabledTexture, boolean spawnsParticles, ItemLike jeiIcon)
    {
        add(name, new KnappingType(new SizedIngredient(Ingredient.of(item), amount), amount == consumeAmount ? Optional.empty() : Optional.of(consumeAmount), sound.holder(), consumeAfterComplete, useDisabledTexture, spawnsParticles, new ItemStack(jeiIcon)));
    }
}
