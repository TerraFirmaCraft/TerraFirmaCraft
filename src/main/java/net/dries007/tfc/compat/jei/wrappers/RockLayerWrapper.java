/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.jei.wrappers;

import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.material.Material;
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
import net.dries007.tfc.api.types.RockCategory;
import net.dries007.tfc.objects.blocks.stone.BlockOreTFC;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.items.metal.ItemOreTFC;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.world.classic.worldgen.vein.VeinRegistry;
import net.dries007.tfc.world.classic.worldgen.vein.VeinType;

@ParametersAreNonnullByDefault
public class RockLayerWrapper implements IRecipeWrapper
{
    private final Rock rock;
    private final List<List<ItemStack>> oreList;

    public RockLayerWrapper(Rock rock)
    {
        this.rock = rock;
        oreList = new ArrayList<>();
        Set<Ore> ores = new HashSet<>();
        List<IBlockState> customOres = new ArrayList<>();
        for (VeinType vein : VeinRegistry.INSTANCE.getVeins().values())
        {
            if (vein.canSpawnIn(rock))
            {
                if (vein.getOre() != null)
                {
                    ores.add(vein.getOre());
                }
                else
                {
                    // Custom ore entry
                    customOres.add(vein.getOreState(rock, Ore.Grade.NORMAL));
                }
            }
        }
        for (Ore ore : ores)
        {
            // Add every permutation of BlockOreTFC for better readability (this means that it's gonna work for any ore block clicked for recipes)
            List<ItemStack> oreItems = TFCRegistries.ROCKS.getValuesCollection().stream()
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
            oreList.add(oreItems);
        }
        if (customOres.size() > 0)
        {
            // Add custom ores
            oreList.addAll(customOres.stream().filter(state -> state.getMaterial() != Material.AIR)
                .map(state -> new ItemStack(state.getBlock(), 1, state.getBlock().getMetaFromState(state)))
                .map(Collections::singletonList)
                .collect(Collectors.toList()));

        }

    }


    @Override
    public void getIngredients(IIngredients recipeIngredients)
    {
        List<ItemStack> input = new ArrayList<>();
        input.add(new ItemStack(BlockRockVariant.get(rock, Rock.Type.RAW)));
        input.add(new ItemStack(ItemRock.get(rock)));
        recipeIngredients.setInputs(VanillaTypes.ITEM, input); // This will only show the raw block, but let use right click stones to open the "recipe"

        recipeIngredients.setOutputLists(VanillaTypes.ITEM, oreList);
    }

    @Override
    public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY)
    {
        final int newLine = 11;
        float x = 33f;
        float y = 6f;
        // Draw Rock Category
        String text = I18n.format("jei.tooltips.tfc.rock_layer.category");
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0x00C300, false);

        text = I18n.format(rock.getRockCategory().getTranslationKey());
        List<String> listString = minecraft.fontRenderer.listFormattedStringToWidth(text, 64); // To fit igneous intrusive/extrusive
        for (String str : listString)
        {
            x = 33f;
            y += newLine;
            x = x - minecraft.fontRenderer.getStringWidth(str) / 2.0f;
            minecraft.fontRenderer.drawString(str, x, y, 0xFFFFFF, false);
        }

        // Draw Layers
        x = 128f;
        y = 6;
        text = I18n.format("jei.tooltips.tfc.rock_layer.layers");
        x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
        minecraft.fontRenderer.drawString(text, x, y, 0x00C300, false);

        if (RockCategory.Layer.TOP.test(rock))
        {
            x = 128f;
            y += newLine;
            text = I18n.format("jei.tooltips.tfc.rock_layer.top");
            x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
            minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);
        }
        if (RockCategory.Layer.MIDDLE.test(rock))
        {
            x = 128f;
            y += newLine;
            text = I18n.format("jei.tooltips.tfc.rock_layer.middle");
            x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
            minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);
        }
        if (RockCategory.Layer.BOTTOM.test(rock))
        {
            x = 128f;
            y += newLine;
            text = I18n.format("jei.tooltips.tfc.rock_layer.bottom");
            x = x - minecraft.fontRenderer.getStringWidth(text) / 2.0f;
            minecraft.fontRenderer.drawString(text, x, y, 0xFFFFFF, false);
        }
    }
}
