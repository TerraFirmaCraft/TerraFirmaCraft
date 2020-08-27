/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.compat.patchouli;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.state.IBlockProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreIngredient;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.BlockCharcoalPile;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.blocks.devices.BlockBloomery;
import net.dries007.tfc.objects.blocks.devices.BlockCharcoalForge;
import net.dries007.tfc.objects.blocks.devices.BlockPitKiln;
import net.dries007.tfc.objects.blocks.metal.BlockMetalSheet;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.blocks.wood.BlockLogPile;
import net.dries007.tfc.objects.inventory.ingredient.IIngredient;
import net.dries007.tfc.objects.inventory.ingredient.IngredientItemStack;
import net.dries007.tfc.objects.inventory.ingredient.IngredientOreDict;
import vazkii.patchouli.api.PatchouliAPI;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class TFCPatchouliPlugin
{
    public static final ResourceLocation BOOK_UTIL_TEXTURES = new ResourceLocation(MOD_ID, "textures/gui/book.png");

    private static PatchouliAPI.IPatchouliAPI API = null;

    public static Ingredient getIngredient(IIngredient<ItemStack> ingredient)
    {
        if (ingredient instanceof IngredientItemStack)
        {
            return Ingredient.fromStacks(ingredient.getValidIngredients().get(0));
        }
        else if (ingredient instanceof IngredientOreDict)
        {
            return new OreIngredient(((IngredientOreDict) ingredient).getOreName());
        }
        else
        {
            return Ingredient.EMPTY;
        }
    }

    public static Ingredient getIngredient(List<IIngredient<ItemStack>> ingredients)
    {
        List<ItemStack> stacks = new ArrayList<>();
        for (IIngredient<ItemStack> ingredient : ingredients)
        {
            stacks.addAll(ingredient.getValidIngredients());
        }
        return Ingredient.fromStacks(stacks.toArray(new ItemStack[0]));
    }

    public static void init()
    {
        // Register multiblocks
        // '0' is the center, aka where the block would be placed
        getAPI().registerMultiblock(new ResourceLocation(MOD_ID, "forge"), getAPI().makeMultiblock(new String[][] {
                {"   ", " 0 ", "   "},
                {" S ", "SFS", " S "},
                {"   ", " S ", "   "}
            },
            'S', getAPI().predicateMatcher(BlockRockVariant.get(Rock.GRANITE, Rock.Type.RAW), BlockCharcoalForge::isValidSide),
            'F', getAPI().looseBlockMatcher(BlocksTFC.CHARCOAL_FORGE),
            ' ', getAPI().anyMatcher(),
            '0', getAPI().airMatcher()
        ).setSymmetrical(true));

        getAPI().registerMultiblock(new ResourceLocation(MOD_ID, "charcoal_pit_3x3"), getAPI().makeMultiblock(new String[][] {
                {"     ", " DDD ", " DDD ", " DDD ", "     "},
                {" DDD ", "DLLLD", "DL0LD", "DLLLD", " DDD "},
                {"SSSSS", "SDDDS", "SDDDS", "SDDDS", "SSSSS"}
            },
            'D', getAPI().predicateMatcher(BlockRockVariant.get(Rock.GRANITE, Rock.Type.DIRT), BlockLogPile::isValidCoverBlock),
            'S', getAPI().displayOnlyMatcher(BlockRockVariant.get(Rock.GRANITE, Rock.Type.DIRT)), // Since these technically don't have to be here, they're just for display
            'L', getAPI().looseBlockMatcher(BlocksTFC.LOG_PILE),
            '0', getAPI().looseBlockMatcher(BlocksTFC.LOG_PILE),
            ' ', getAPI().anyMatcher()
        ).setSymmetrical(true));

        getAPI().registerMultiblock(new ResourceLocation(MOD_ID, "charcoal_pit_1x1"), getAPI().makeMultiblock(new String[][] {
                {"   ", " D ", "   "},
                {" D ", "D0D", " D "},
                {"SSS", "SDS", "SSS"}
            },
            'D', getAPI().predicateMatcher(BlockRockVariant.get(Rock.GRANITE, Rock.Type.DIRT), BlockLogPile::isValidCoverBlock),
            'S', getAPI().displayOnlyMatcher(BlockRockVariant.get(Rock.GRANITE, Rock.Type.DIRT)), // Since these technically don't have to be here, they're just for display
            '0', getAPI().looseBlockMatcher(BlocksTFC.LOG_PILE),
            ' ', getAPI().anyMatcher()
        ).setSymmetrical(true));

        getAPI().registerMultiblock(new ResourceLocation(MOD_ID, "pit_kiln"), getAPI().makeMultiblock(new String[][] {
                {"   ", " 0 ", "   "},
                {" S ", "SPS", " S "},
                {"   ", " S ", "   "}
            },
            '0', getAPI().displayOnlyMatcher(Blocks.FIRE),
            'S', getAPI().predicateMatcher(BlockRockVariant.get(Rock.GRANITE, Rock.Type.DIRT), IBlockProperties::isNormalCube),
            'P', getAPI().stateMatcher(BlocksTFC.PIT_KILN.getDefaultState().withProperty(BlockPitKiln.FULL, true)),
            ' ', getAPI().anyMatcher()
        ).setSymmetrical(true));

        getAPI().registerMultiblock(new ResourceLocation(MOD_ID, "bloomery"), getAPI().makeMultiblock(new String[][] {
                {" S ", "S S", " S "},
                {"SS ", "BCS", "SS "},
                {"   ", "S0 ", "   "}
            },
            'S', getAPI().predicateMatcher(BlockRockVariant.get(Rock.GRANITE, Rock.Type.SMOOTH), BlockBloomery::isValidSideBlock),
            '0', getAPI().predicateMatcher(BlockRockVariant.get(Rock.GRANITE, Rock.Type.SMOOTH), BlockBloomery::isValidSideBlock),
            'B', getAPI().looseBlockMatcher(BlocksTFC.BLOOMERY),
            'C', getAPI().stateMatcher(BlocksTFC.CHARCOAL_PILE.getDefaultState().withProperty(BlockCharcoalPile.LAYERS, 8)),
            ' ', getAPI().anyMatcher()
        ));

        getAPI().registerMultiblock(new ResourceLocation(MOD_ID, "blast_furnace"), getAPI().makeMultiblock(new String[][] {
                {"  S  ", " SFS ", "SF FS", " SFS ", "  S  "},
                {"     ", "     ", "  B  ", "     ", "     "},
                {"     ", "     ", "  0  ", "     ", "     "}
            },
            'S', getAPI().predicateMatcher(BlockMetalSheet.get(Metal.WROUGHT_IRON), state -> state.getBlock() instanceof BlockMetalSheet),
            'F', getAPI().looseBlockMatcher(BlocksTFC.FIRE_BRICKS),
            'B', getAPI().looseBlockMatcher(BlocksTFC.BLAST_FURNACE),
            '0', getAPI().looseBlockMatcher(BlocksTFC.CRUCIBLE)
        ).setSymmetrical(true));
    }

    public static void giveBookToPlayer(EntityPlayer player)
    {
        ItemStack bookStack = getAPI().getBookStack(new ResourceLocation(MOD_ID, "book").toString());
        ItemHandlerHelper.giveItemToPlayer(player, bookStack);
    }

    private static PatchouliAPI.IPatchouliAPI getAPI()
    {
        if (API == null)
        {
            API = PatchouliAPI.instance;
            if (API.isStub())
            {
                TerraFirmaCraft.getLog().warn("Failed to intercept Patchouli API. Problems may occur");
            }
        }
        return API;
    }
}
