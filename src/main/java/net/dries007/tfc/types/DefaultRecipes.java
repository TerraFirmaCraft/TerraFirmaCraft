/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.types;

import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import net.dries007.tfc.api.types.KnappingRecipe;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemUnfiredMold;
import net.dries007.tfc.objects.items.rock.ItemRockToolHead;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@Mod.EventBusSubscriber(modid = MOD_ID)
public class DefaultRecipes
{
    @SubscribeEvent
    @SuppressWarnings("ConstantConditions")
    public static void onRegisterKnappingRecipeEvent(RegistryEvent.Register<KnappingRecipe> event)
    {
        /* STONE TOOL HEADS */

        for (Rock.ToolType type : Rock.ToolType.values())
        {
            KnappingRecipe r = new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, type)), type.getPattern());
            event.getRegistry().register(r.setRegistryName(MOD_ID, type.name().toLowerCase() + "_head"));
        }
        // these recipes cover all cases where multiple stone items can be made
        // recipes are already mirror checked
        event.getRegistry().registerAll(
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), "X  X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "knife_head_1"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), "X   X", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "knife_head_2"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.KNIFE), 2), " X X ", "XX XX", "XX XX", "XX XX", "XX XX").setRegistryName(MOD_ID, "knife_head_3"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.HOE), 2), "XXXXX", "XX   ", "     ", "XXXXX", "XX   ").setRegistryName(MOD_ID, "hoe_head_1"),
            new KnappingRecipe.Stone(KnappingRecipe.Type.STONE, c -> new ItemStack(ItemRockToolHead.get(c, Rock.ToolType.HOE), 2), "XXXXX", "XX   ", "     ", "XXXXX", "   XX").setRegistryName(MOD_ID, "hoe_head_2")
        );

        /* CLAY ITEMS */

        for (Metal.ItemType type : Metal.ItemType.values())
        {
            if (type.hasMold(null))
            {
                event.getRegistry().register(new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemUnfiredMold.get(type)), type.getPattern()).setRegistryName(MOD_ID, type.name().toLowerCase() + "_mold"));
            }
        }

        // todo: uncomment these as more items / blocks are added
        event.getRegistry().registerAll(
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_VESSEL), " XXX ", "XXXXX", "XXXXX", "XXXXX", " XXX ").setRegistryName(MOD_ID, "clay_small_vessel"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_JUG), " X   ", "XXXX ", "XXX X", "XXXX ", "XXX  ").setRegistryName(MOD_ID, "clay_jug"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_POT), "X   X", "X   X", "X   X", "XXXXX", " XXX ").setRegistryName(MOD_ID, "clay_pot"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_BOWL, 2), "X   X", " XXX ").setRegistryName(MOD_ID, "clay_bowl"),
            new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(ItemsTFC.CERAMICS_UNFIRED_BOWL, 4), "X   X", " XXX ", "     ", "X   X", " XXX ").setRegistryName(MOD_ID, "clay_bowl_2")
            //new KnappingRecipe.Simple(KnappingRecipe.Type.CLAY, true, new ItemStack(BlocksTFC.CERAMICS_LARGE_VESSEL), "XXXX", "X  X", "X  X", "X  X", "XXXX").setRegistryName(MOD_ID, "clay_large_vessel"),
        );

        /* LEATHER ITEMS */

        // todo: leather recipes

        /* FIRE CLAY ITEMS */

        // todo: fire clay recipes
    }
}
