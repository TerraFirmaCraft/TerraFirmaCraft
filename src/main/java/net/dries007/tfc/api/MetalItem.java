/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.crafting.CraftingHelper;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.objects.types.MetalItemManager;
import net.dries007.tfc.objects.types.MetalManager;

public class MetalItem
{
    public static Optional<MetalItem> get(ItemStack stack)
    {
        return MetalItemManager.CACHE.getAll(stack.getItem())
            .stream()
            .filter(metalItem -> metalItem.isValid(stack))
            .findFirst();
    }

    public static void addTooltipInfo(ItemStack stack, List<ITextComponent> text)
    {
        get(stack).ifPresent(metalItem -> {
            text.add(new TranslationTextComponent(TerraFirmaCraft.MOD_ID + ".tooltip.metal", metalItem.getMetal().getDisplayName()));
            text.add(new TranslationTextComponent(TerraFirmaCraft.MOD_ID + ".tooltip.units", metalItem.getAmount()));
            text.add(metalItem.getMetal().getTier().getDisplayName());
        });
    }

    private final ResourceLocation id;
    private final Ingredient ingredient;
    private final Metal metal;
    private final int amount;

    public MetalItem(ResourceLocation id, JsonObject json)
    {
        this.id = id;
        ingredient = CraftingHelper.getIngredient(JSONUtils.getJsonObject(json, "ingredient"));
        ResourceLocation metalId = new ResourceLocation(JSONUtils.getString(json, "metal"));
        metal = MetalManager.INSTANCE.get(metalId);
        if (metal == null)
        {
            throw new JsonSyntaxException("Invalid metal specified: " + metalId.toString());
        }
        amount = JSONUtils.getInt(json, "amount");
    }

    public ResourceLocation getId()
    {
        return id;
    }

    public Metal getMetal()
    {
        return metal;
    }

    public int getAmount()
    {
        return amount;
    }

    private boolean isValid(ItemStack stack)
    {
        return this.ingredient.test(stack);
    }

    public Collection<Item> getValidItems()
    {
        return Arrays.stream(this.ingredient.getMatchingStacks()).map(ItemStack::getItem).collect(Collectors.toSet());
    }
}
