/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.BlockWorkbench;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.stats.StatList;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.container.ContainerWorkbenchTFC;
import net.dries007.tfc.util.OreDictionaryHelper;

public class BlockWorkbenchTFC extends BlockWorkbench
{
    private static final Map<Tree, BlockWorkbenchTFC> MAP = new HashMap<>();

    public static BlockWorkbenchTFC get(Tree wood)
    {
        return MAP.get(wood);
    }

    public final Tree wood;

    public BlockWorkbenchTFC(Tree wood)
    {
        if (MAP.put(wood, this) != null)
        {
            throw new IllegalStateException("There can only be one.");
        }
        this.wood = wood;

        setSoundType(SoundType.WOOD);
        setHardness(2.0F).setResistance(5.0F);
        setHarvestLevel("axe", 0);
        OreDictionaryHelper.register(this, "workbench");
        OreDictionaryHelper.register(this, "crafting", "table", "wood");
        Blocks.FIRE.setFireInfo(this, 5, 20);
    }

    @SideOnly(Side.CLIENT)
    @Nonnull
    @Override
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, @Nonnull BlockPos pos, IBlockState state, @Nullable EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote || playerIn == null)
        {
            return true;
        }
        else
        {
            playerIn.displayGui(new InterfaceCraftingTable(this, worldIn, pos));
            playerIn.addStat(StatList.CRAFTING_TABLE_INTERACTION);
            return true;
        }
    }

    @SuppressWarnings("WeakerAccess")
    @ParametersAreNonnullByDefault
    @MethodsReturnNonnullByDefault
    public static class InterfaceCraftingTable implements IInteractionObject
    {
        //todo: replace with proper workbench mechanics + normal forge gui code
        private final BlockWorkbenchTFC workbenchTFC;
        private final World world;
        private final BlockPos position;

        public InterfaceCraftingTable(BlockWorkbenchTFC workbenchTFC, World worldIn, BlockPos pos)
        {
            this.workbenchTFC = workbenchTFC;
            this.world = worldIn;
            this.position = pos;
        }

        /**
         * Get the name of this object. For players this returns their username
         */
        @Override
        public String getName()
        {
            return "crafting_table";
        }

        /**
         * Returns true if this thing is named
         */
        @Override
        public boolean hasCustomName()
        {
            return false;
        }

        /**
         * Get the formatted ChatComponent that will be used for the sender's username in chat
         */
        @Override
        public ITextComponent getDisplayName()
        {
            return new TextComponentTranslation(workbenchTFC.getTranslationKey() + ".name");
        }

        @Override
        public Container createContainer(InventoryPlayer inv, EntityPlayer player)
        {
            return new ContainerWorkbenchTFC(inv, world, position, workbenchTFC);
        }

        @Override
        public String getGuiID()
        {
            return "minecraft:crafting_table";
        }
    }

}