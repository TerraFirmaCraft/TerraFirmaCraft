package net.dries007.tfc.compat.jei.category;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import mezz.jei.api.helpers.IGuiHelper;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.recipes.QuernRecipe;

public class QuernRecipeCategory extends SimpleItemRecipeCategory<QuernRecipe>
{
    public QuernRecipeCategory(ResourceLocation uId, IGuiHelper helper)
    {
        super(uId, helper, new ItemStack(TFCBlocks.QUERN.get()), QuernRecipe.class);
    }

    @Override
    protected Tag<Item> getToolTag()
    {
        return TFCTags.Items.HANDSTONE;
    }
}
