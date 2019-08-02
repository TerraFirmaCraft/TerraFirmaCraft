/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.recipes.knapping.KnappingRecipe;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.api.util.IRockObject;
import net.dries007.tfc.client.gui.*;
import net.dries007.tfc.objects.container.*;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.items.ceramics.ItemMold;
import net.dries007.tfc.objects.items.ceramics.ItemSmallVessel;
import net.dries007.tfc.objects.items.rock.ItemRock;
import net.dries007.tfc.objects.te.*;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class TFCGuiHandler implements IGuiHandler
{
    private static final ResourceLocation SMALL_INVENTORY_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/small_inventory.png");
    private static final ResourceLocation CLAY_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/clay_button.png");
    private static final ResourceLocation FIRE_CLAY_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/clay_button_fire.png");
    private static final ResourceLocation LEATHER_TEXTURE = new ResourceLocation(MOD_ID, "textures/gui/knapping/leather_button.png");

    // use this instead of player.openGui() -> avoids magic numbers
    public static void openGui(World world, BlockPos pos, EntityPlayer player, Type type)
    {
        player.openGui(TerraFirmaCraft.getInstance(), type.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
    }

    // Only use this for things that don't need a BlockPos to identify TE's!!!
    public static void openGui(World world, EntityPlayer player, Type type)
    {
        player.openGui(TerraFirmaCraft.getInstance(), type.ordinal(), world, 0, 0, 0);
    }

    @Override
    @Nullable
    public Container getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        BlockPos pos = new BlockPos(x, y, z);
        ItemStack stack = player.getHeldItemMainhand();
        Type type = Type.valueOf(ID);
        switch (type)
        {
            case NEST_BOX:
                TENestBox teNestBox = Helpers.getTE(world, pos, TENestBox.class);
                return teNestBox == null ? null : new ContainerNestBox(player.inventory, teNestBox);
            case LOG_PILE:
                TELogPile teLogPile = Helpers.getTE(world, pos, TELogPile.class);
                return teLogPile == null ? null : new ContainerLogPile(player.inventory, teLogPile);
            case SMALL_VESSEL:
                return new ContainerSmallVessel(player.inventory, stack.getItem() instanceof ItemSmallVessel ? stack : player.getHeldItemOffhand());
            case SMALL_VESSEL_LIQUID:
                return new ContainerLiquidTransfer(player.inventory, stack.getItem() instanceof ItemSmallVessel ? stack : player.getHeldItemOffhand());
            case MOLD:
                return new ContainerLiquidTransfer(player.inventory, stack.getItem() instanceof ItemMold ? stack : player.getHeldItemOffhand());
            case FIRE_PIT:
                return new ContainerFirePit(player.inventory, Helpers.getTE(world, pos, TEFirePit.class));
            case BARREL:
                return new ContainerBarrel(player.inventory, Helpers.getTE(world, pos, TEBarrel.class));
            case CHARCOAL_FORGE:
                return new ContainerCharcoalForge(player.inventory, Helpers.getTE(world, pos, TECharcoalForge.class));
            case ANVIL:
                return new ContainerAnvilTFC(player.inventory, Helpers.getTE(world, pos, TEAnvilTFC.class));
            case ANVIL_PLAN:
                return new ContainerAnvilPlan(player.inventory, Helpers.getTE(world, pos, TEAnvilTFC.class));
            case KNAPPING_STONE:
                return new ContainerKnapping(KnappingRecipe.Type.STONE, player.inventory, stack.getItem() instanceof ItemRock ? stack : player.getHeldItemOffhand());
            case KNAPPING_CLAY:
                return new ContainerKnapping(KnappingRecipe.Type.CLAY, player.inventory, stack.getItem() == Items.CLAY_BALL ? stack : player.getHeldItemOffhand());
            case KNAPPING_LEATHER:
                return new ContainerKnapping(KnappingRecipe.Type.LEATHER, player.inventory, stack.getItem() == Items.LEATHER ? stack : player.getHeldItemOffhand());
            case KNAPPING_FIRE_CLAY:
                return new ContainerKnapping(KnappingRecipe.Type.FIRE_CLAY, player.inventory, stack.getItem() == ItemsTFC.FIRE_CLAY ? stack : player.getHeldItemOffhand());
            case QUERN:
                return new ContainerQuern(player.inventory, Helpers.getTE(world, pos, TEQuern.class));
            case CRUCIBLE:
                return new ContainerCrucible(player.inventory, Helpers.getTE(world, pos, TECrucible.class));
            case LARGE_VESSEL:
                return new ContainerLargeVessel(player.inventory, Helpers.getTE(world, pos, TELargeVessel.class));
            case CALENDAR:
            case SKILLS:
            case NUTRITION:
                return new ContainerSimple(player.inventory);
            case BLAST_FURNACE:
                return new ContainerBlastFurnace(player.inventory, Helpers.getTE(world, pos, TEBlastFurnace.class));
            case CRAFTING:
                return new ContainerInventoryCrafting(player.inventory, player.world);
            default:
                return null;
        }
    }

    @Override
    @Nullable
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        Container container = getServerGuiElement(ID, player, world, x, y, z);
        Type type = Type.valueOf(ID);
        BlockPos pos = new BlockPos(x, y, z);
        switch (type)
        {
            case NEST_BOX:
                return new GuiContainerTFC(container, player.inventory, SMALL_INVENTORY_BACKGROUND);
            case LOG_PILE:
                return new GuiContainerTFC(container, player.inventory, SMALL_INVENTORY_BACKGROUND);
            case SMALL_VESSEL:
                return new GuiContainerTFC(container, player.inventory, SMALL_INVENTORY_BACKGROUND);
            case SMALL_VESSEL_LIQUID:
                return new GuiLiquidTransfer(container, player, player.getHeldItemMainhand().getItem() instanceof ItemSmallVessel);
            case MOLD:
                return new GuiLiquidTransfer(container, player, player.getHeldItemMainhand().getItem() instanceof ItemMold);
            case FIRE_PIT:
                return new GuiFirePit(container, player.inventory, Helpers.getTE(world, pos, TEFirePit.class));
            case BARREL:
                return new GuiBarrel(container, player.inventory, Helpers.getTE(world, pos, TEBarrel.class), world.getBlockState(new BlockPos(x, y, z)).getBlock().getTranslationKey());
            case CHARCOAL_FORGE:
                return new GuiCharcoalForge(container, player.inventory, Helpers.getTE(world, pos, TECharcoalForge.class));
            case ANVIL:
                return new GuiAnvilTFC(container, player.inventory, Helpers.getTE(world, pos, TEAnvilTFC.class));
            case ANVIL_PLAN:
                return new GuiAnvilPlan(container, player.inventory, Helpers.getTE(world, pos, TEAnvilTFC.class));
            case KNAPPING_STONE:
                ItemStack stack = player.getHeldItemMainhand();
                Rock rock = stack.getItem() instanceof IRockObject ? ((IRockObject) stack.getItem()).getRock(stack) :
                    ((IRockObject) player.getHeldItemOffhand().getItem()).getRock(player.getHeldItemOffhand());
                //noinspection ConstantConditions
                return new GuiKnapping(container, player, KnappingRecipe.Type.STONE, rock.getTexture());
            case KNAPPING_CLAY:
                return new GuiKnapping(container, player, KnappingRecipe.Type.CLAY, CLAY_TEXTURE);
            case KNAPPING_LEATHER:
                return new GuiKnapping(container, player, KnappingRecipe.Type.LEATHER, LEATHER_TEXTURE);
            case KNAPPING_FIRE_CLAY:
                return new GuiKnapping(container, player, KnappingRecipe.Type.FIRE_CLAY, FIRE_CLAY_TEXTURE);
            case QUERN:
                return new GuiQuern(container, player.inventory, Helpers.getTE(world, pos, TEQuern.class), world.getBlockState(new BlockPos(x, y, z)).getBlock().getTranslationKey());
            case CRUCIBLE:
                return new GuiCrucible(container, player.inventory, Helpers.getTE(world, pos, TECrucible.class));
            case LARGE_VESSEL:
                return new GuiLargeVessel(container, player.inventory, Helpers.getTE(world, pos, TELargeVessel.class), world.getBlockState(new BlockPos(x, y, z)).getBlock().getTranslationKey());
            case CALENDAR:
                return new GuiCalendar(container, player.inventory);
            case NUTRITION:
                return new GuiNutrition(container, player.inventory);
            case SKILLS:
                return new GuiSkills(container, player.inventory);
            case BLAST_FURNACE:
                return new GuiBlastFurnace(container, player.inventory, Helpers.getTE(world, pos, TEBlastFurnace.class));
            case CRAFTING:
                return new GuiInventoryCrafting(container);
            default:
                return null;
        }
    }

    public enum Type
    {
        NEST_BOX,
        LOG_PILE,
        SMALL_VESSEL,
        SMALL_VESSEL_LIQUID,
        MOLD,
        FIRE_PIT,
        BARREL,
        QUERN,
        KNAPPING_STONE,
        KNAPPING_CLAY,
        KNAPPING_FIRE_CLAY,
        KNAPPING_LEATHER,
        CHARCOAL_FORGE,
        ANVIL,
        ANVIL_PLAN,
        CRUCIBLE,
        BLAST_FURNACE,
        LARGE_VESSEL,
        CALENDAR,
        NUTRITION,
        SKILLS,
        INVENTORY, // This is special, it is used by GuiButtonPlayerInventoryTab to signal to open the vanilla inventory
        CRAFTING, // In-inventory 3x3 crafting grid
        NULL; // This is special, it is a non-null null.

        private static Type[] values = values();

        @Nonnull
        public static Type valueOf(int id)
        {
            return id < 0 || id >= values.length ? NULL : values[id];
        }
    }
}
