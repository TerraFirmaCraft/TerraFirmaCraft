/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.outputs;

import net.minecraft.core.DefaultedRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegistryBuilder;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.recipes.JamPotRecipe;
import net.dries007.tfc.common.recipes.PotRecipe;
import net.dries007.tfc.common.recipes.SoupPotRecipe;
import net.dries007.tfc.util.tooltip.BlockEntityTooltip;
import net.dries007.tfc.util.Helpers;

/**
 * The output of a pot recipe. This output can be fairly complex, but follows a specific contract:
 * <ol>
 *     <li>The output is created, with access to the inventory, populated with the ingredient items (in {@link PotRecipe#getOutput(PotBlockEntity.PotInventory)})</li>
 *     <li>{@link PotOutput#onFinish(PotBlockEntity.PotInventory)} is called, with a completely empty inventory. The output can then add fluids or items back into the pot as necessary</li>
 *     <li>THEN, if {@link PotOutput#isEmpty()} returns true, the output is discarded. Otherwise...</li>
 *     <li>The output is saved to the tile entity. On a right click, {@link PotOutput#onInteract(PotBlockEntity, Player, ItemStack)} is called, and after each call, {@link PotOutput#isEmpty()} will be queried to see if the output is empty. The pot will not resume functionality until the output is empty</li>
 * </ol>
 *
 * @see PotBlockEntity#handleCooking()
 */
public interface PotOutput
{
    ResourceKey<Registry<OutputType>> KEY = ResourceKey.createRegistryKey(Helpers.identifier("pot_output"));
    ResourceKey<OutputType> DEFAULT = ResourceKey.create(KEY, Helpers.identifier("empty"));
    DefaultedRegistry<OutputType> REGISTRY = (DefaultedRegistry<OutputType>) new RegistryBuilder<>(KEY).sync(true).defaultKey(DEFAULT).create();

    DeferredRegister<OutputType> TYPES = DeferredRegister.create(KEY, TerraFirmaCraft.MOD_ID);

    PotOutput EMPTY_INSTANCE = new PotOutput() {};

    DeferredHolder<OutputType, OutputType> EMPTY = register("empty", (provider, nbt) -> EMPTY_INSTANCE);
    DeferredHolder<OutputType, OutputType> SOUP = register("soup", SoupPotRecipe.OUTPUT_TYPE);
    DeferredHolder<OutputType, OutputType> JAM = register("jam", JamPotRecipe.OUTPUT_TYPE);

    private static DeferredHolder<OutputType, OutputType> register(String name, OutputType output)
    {
        return TYPES.register(name, () -> output);
    }

    /**
     * Read an output from an NBT tag.
     */
    static PotOutput read(HolderLookup.Provider provider, CompoundTag nbt)
    {
        return REGISTRY.get(Helpers.resourceLocation(nbt.getString("type"))).read(provider, nbt);
    }

    /**
     * Write an output to a NBT tag.
     */
    static CompoundTag write(HolderLookup.Provider provider, PotOutput output)
    {
        final CompoundTag nbt = new CompoundTag();
        nbt.putString("type", REGISTRY.getKey(output.getType()).toString());
        output.write(provider, nbt);
        return nbt;
    }

    /**
     * If there is still something to be extracted from this output. If this returns false at any time the output must be serializable
     */
    default boolean isEmpty()
    {
        return true;
    }

    /**
     * The color of the fluid the pot, while storing this output, should render as inside the pot, despite the pot itself not necessarily being filled with any fluid
     *
     * @return an {@code int} color, or -1 for no fluid to be displayed.
     */
    default int getFluidColor()
    {
        return -1;
    }

    /**
     * An alternative to {@link PotOutput#getFluidColor()} that renders a solid texture.
     *
     * @return A {@linkplain ResourceLocation} matching a texture.
     */
    @Nullable
    default ResourceLocation getRenderTexture()
    {
        return null;
    }

    /**
     * @return The y level [0, 1] that the fluid face renders at. The inside of the pot's model extends from 6 to 11 pixels vertically.
     */
    default float getFluidYLevel()
    {
        return 0.625f;
    }

    /**
     * Called with an empty pot inventory immediately after completion, and after clearing the inventory of the pot, but, before
     * checking {@link #isEmpty()}. Fills the inventory with immediate outputs from the output. Note that any outputs that depend
     * on the inventory must be computed <strong>before</strong> this method.
     */
    default void onFinish(PotBlockEntity.PotInventory inventory) {}

    /**
     * Called when a player interacts with the pot inventory, using the specific item unsealedStack, to try and extract output.
     */
    default ItemInteractionResult onInteract(PotBlockEntity entity, Player player, ItemStack clickedWith)
    {
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    /**
     * Gets the output type of this output, used for serializing the output.
     * If the output always returns true to {@link #isEmpty()}, then this can be left as {@link PotOutput#EMPTY}.
     */
    default OutputType getType()
    {
        return PotOutput.EMPTY.get();
    }

    /**
     * Writes implementation specific output data to disk.
     */
    default void write(HolderLookup.Provider provider, CompoundTag nbt) {}

    @Nullable
    default BlockEntityTooltip getTooltip()
    {
        return null;
    }

    @FunctionalInterface
    interface OutputType
    {
        /**
         * Read the output from the given tag. The tag should contain the key "type", which will equal the registered ID of this output type.
         */
        PotOutput read(HolderLookup.Provider provider, CompoundTag nbt);
    }
}
