/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

public class VeinWrapper implements IRecipeWrapper
{
    private final VeinType veinType;
    private final List<ItemStack> rockBlocks;
    private final List<ItemStack> oreItems;

    public VeinWrapper(VeinType vein)
    {
        this.veinType = vein;
        rockBlocks = TFCRegistries.ROCKS.getValuesCollection().stream()
            .filter(vein::canSpawnIn).map(rock -> new ItemStack(BlockRockVariant.get(rock, Rock.Type.RAW)))
            .collect(Collectors.toList());
        if (veinType.getOre() != null)
        {
            Ore ore = veinType.getOre();
            // Add every permutation of BlockOreTFC for better readability (this means that it's gonna work for any ore block clicked for recipes)
            oreItems = TFCRegistries.ROCKS.getValuesCollection().stream()
                .map(r -> new ItemStack(BlockOreTFC.get(ore, r))).collect(Collectors.toList());
            // Add ore drops
            if (ore.isGraded())
            {
                for (Ore.Grade grade : Ore.Grade.values())
                {
                    oreItems.add(ItemOreTFC.get(ore, grade, 1));
                }
                oreItems.add(ItemSmallOre.get(ore, 1));
            }
            else
            {
                oreItems.add(ItemOreTFC.get(ore, 1));
            }
        }
        else
        {
            // Add custom ores
            IBlockState state = veinType.getOreState(Rock.GRANITE, Ore.Grade.NORMAL);
            oreItems = Collections.singletonList(new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)));
        }
    }


    @Override
    public void getIngredients(IIngredients recipeIngredients)
    {
        recipeIngredients.setInputs(VanillaTypes.ITEM, rockBlocks); // Inputs are "reversed" compared to RockLayerWrapper

        List<List<ItemStack>> outputList = new ArrayList<>();
        outputList.add(oreItems);
        if (veinType.hasLooseRocks())
        {
            outputList.add(Collections.singletonList(veinType.getLooseRockItem()));
        }
        recipeIngredients.setOutputLists(VanillaTypes.ITEM, outputList);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        final int newLine = 11;
        float x = 16f;
        float y = 19f;
        // Draw Min / Max Y
        String text = I18n.format("jei.tooltips.tfc.vein.min_y");
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0x00C300, false);

        x = 16f;
        y += newLine;
        text = String.valueOf(veinType.getMinY());
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);

        x = 49f;
        y = 19f;
        text = I18n.format("jei.tooltips.tfc.vein.max_y");
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0x00C300, false);

        x = 49f;
        y += newLine;
        text = String.valueOf(veinType.getMaxY());
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);

        // Draw Rarity
        x = 33f;
        y += newLine;
        text = I18n.format("jei.tooltips.tfc.vein.rarity");
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0x00C300, false);

        x = 33f;
        y += newLine;
        String rarityValue = String.format("%.1f", 100.0f / veinType.getRarity()); // Let's not forget that we can't format using I18n (since MC convert any %d and %f in lang entries to %s)
        text = I18n.format("jei.tooltips.tfc.vein.rarity_value", rarityValue);
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);

        // Draw Loose rock title
        text = I18n.format("jei.tooltips.tfc.vein.loose_rock");
        List<String> listString = minecraft.fontRenderer.listFormattedStringToWidth(text, 52); // To fit
        y = 33f - newLine * listString.size();
        for (String str : listString)
        {
            x = 127f;
            y += newLine;
            x = x - minecraft.fontRenderer.getStringWidth(str) / 2.0f;
            minecraft.fontRenderer.drawString(str, x, y, 0xFFFFFF, false);
        }

        // Draw vein name
        x = 81f;
        y = 3f;
        text = I18n.format("vein." + veinType.getRegistryName() + ".name");
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);
    }
}
