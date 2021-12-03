package net.dries007.tfc.compat.jei.category;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.helpers.IGuiHelper;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.items.TFCItems;
import net.dries007.tfc.common.recipes.ScrapingRecipe;
import net.dries007.tfc.util.Metal;

public class ScrapingRecipeCategory extends SimpleItemRecipeCategory<ScrapingRecipe>
{
    public ScrapingRecipeCategory(ResourceLocation uId, IGuiHelper helper)
    {
        super(uId, helper, new ItemStack(TFCItems.METAL_ITEMS.get(Metal.Default.BLACK_BRONZE).get(Metal.ItemType.KNIFE).get()), ScrapingRecipe.class);
    }

    @Override
    protected Tag<Item> getToolTag()
    {
        return TFCTags.Items.KNIVES;
    }
}
