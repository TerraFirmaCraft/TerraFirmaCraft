/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.items.ItemHandlerHelper;

import net.dries007.tfc.common.blockentities.PotBlockEntity;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.outputs.PotOutput;
import net.dries007.tfc.util.tooltip.BlockEntityTooltip;
import net.dries007.tfc.util.tooltip.BlockEntityTooltips;
import net.dries007.tfc.util.Helpers;

public class JamPotRecipe extends PotRecipe
{
    public static final MapCodec<JamPotRecipe> CODEC = RecordCodecBuilder.mapCodec(i -> i.group(
        PotRecipe.CODEC.forGetter(c -> c),
        ItemStack.CODEC.fieldOf("unsealed_result").forGetter(c -> c.jarredStack),
        ItemStack.CODEC.fieldOf("sealed_result").forGetter(c -> c.jarredStackWithLid),
        ResourceLocation.CODEC.fieldOf("texture").forGetter(c -> c.texture)
    ).apply(i, JamPotRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, JamPotRecipe> STREAM_CODEC = StreamCodec.composite(
        PotRecipe.STREAM_CODEC, c -> c,
        ItemStack.STREAM_CODEC, c -> c.jarredStack,
        ItemStack.STREAM_CODEC, c -> c.jarredStackWithLid,
        ResourceLocation.STREAM_CODEC, c -> c.texture,
        JamPotRecipe::new
    );

    public static final PotOutput.OutputType OUTPUT_TYPE = (provider, nbt) -> {
        ItemStack stack = ItemStack.parseOptional(provider, nbt.getCompound("unsealed_result"));
        ItemStack stack2 = ItemStack.parseOptional(provider, nbt.getCompound("sealed_result"));
        ResourceLocation texture = Helpers.resourceLocation(nbt.getString("texture"));
        return new JamPotRecipe.JamOutput(stack, stack2, texture);
    };

    private final ItemStack jarredStack;
    private final ItemStack jarredStackWithLid;
    private final ResourceLocation texture;

    public JamPotRecipe(PotRecipe base, ItemStack jarredStack, ItemStack jarredStackWithLid, ResourceLocation texture)
    {
        super(base);
        this.jarredStack = jarredStack;
        this.jarredStackWithLid = jarredStackWithLid;
        this.texture = texture;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries)
    {
        return jarredStackWithLid;
    }

    public ResourceLocation getTexture()
    {
        return texture;
    }

    @Override
    public PotOutput getOutput(PotBlockEntity.PotInventory inventory)
    {
        return new JamOutput(jarredStack.copy(), jarredStackWithLid.copy(), texture);
    }

    @Override
    public RecipeSerializer<?> getSerializer()
    {
        return TFCRecipeSerializers.POT_JAM.get();
    }

    public record JamOutput(ItemStack unsealedStack, ItemStack sealedStack, ResourceLocation texture) implements PotOutput
    {
        @Override
        public boolean isEmpty()
        {
            return unsealedStack.isEmpty() || sealedStack.isEmpty();
        }

        @Override
        public ItemInteractionResult onInteract(PotBlockEntity entity, Player player, ItemStack clickedWith)
        {
            if (Helpers.isItem(clickedWith, TFCItems.EMPTY_JAR) && !unsealedStack.isEmpty())
            {
                // take the player's empty jar
                clickedWith.shrink(1);
                sealedStack.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, unsealedStack.split(1));
                return ItemInteractionResult.SUCCESS;
            }
            if (Helpers.isItem(clickedWith, TFCItems.EMPTY_JAR_WITH_LID) && !sealedStack.isEmpty())
            {
                // take the player's empty jar
                clickedWith.shrink(1);
                unsealedStack.shrink(1);
                ItemHandlerHelper.giveItemToPlayer(player, sealedStack.split(1));
                return ItemInteractionResult.SUCCESS;
            }
            return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
        }

        @Override
        public ResourceLocation getRenderTexture()
        {
            return texture;
        }

        @Override
        public float getFluidYLevel()
        {
            return Mth.clampedMap(unsealedStack.getCount(), 0, 4, 7f / 16, 10f / 16);
        }

        @Override
        public void write(HolderLookup.Provider provider, CompoundTag nbt)
        {
            nbt.put("unsealed_result", unsealedStack.save(provider));
            nbt.put("sealed_result", sealedStack.save(provider));
            nbt.putString("texture", texture.toString());
        }

        @Override
        public OutputType getType()
        {
            return JamPotRecipe.OUTPUT_TYPE;
        }

        @Override
        public BlockEntityTooltip getTooltip()
        {
            return ((level, state, pos, entity, tooltip) -> {
                BlockEntityTooltips.itemWithCount(tooltip, sealedStack);
                FoodCapability.addTooltipInfo(sealedStack, tooltip);
            });
        }
    }
}
