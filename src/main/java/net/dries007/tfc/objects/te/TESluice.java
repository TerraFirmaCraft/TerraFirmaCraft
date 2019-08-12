/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;

import net.dries007.tfc.ConfigTFC;
import net.dries007.tfc.Constants;
import net.dries007.tfc.api.types.Ore;
import net.dries007.tfc.api.types.Rock;
import net.dries007.tfc.objects.blocks.stone.BlockRockVariant;
import net.dries007.tfc.objects.items.metal.ItemSmallOre;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.world.classic.chunkdata.ChunkDataTFC;

public class TESluice extends TEBase implements ITickable
{
    private int soil;
    private int ticksRemaining, delayTimer;

    //todo add checks for water

    public TESluice()
    {
        super();
    }

    @Override
    public void update()
    {
        if (!world.isRemote)
        {
            if (ticksRemaining > 0)
            {
                if (--ticksRemaining <= 0)
                {
                    ChunkPos myPos = world.getChunk(pos).getPos();
                    int radius = ConfigTFC.GENERAL.sluiceRadius;
                    //Copy from Helper method, but only look for workable chunks
                    List<Chunk> chunks = new ArrayList<>();
                    for (int x = myPos.x - radius; x <= myPos.x + radius; x++)
                    {
                        for (int z = myPos.z - radius; z <= myPos.z + radius; z++)
                        {
                            Chunk chunk = world.getChunk(x, z);
                            ChunkDataTFC chunkData = ChunkDataTFC.get(chunk);
                            if (chunkData.canWork(true) && chunkData.getChunkOres().size() > 0)
                            {
                                chunks.add(chunk);
                            }
                        }
                    }
                    if (chunks.size() > 0)
                    {
                        Chunk workingChunk = chunks.get(Constants.RNG.nextInt(chunks.size()));
                        ChunkDataTFC chunkData = ChunkDataTFC.get(workingChunk);
                        chunkData.addWork();
                        List<Ore> oreList = chunkData.getChunkOres();
                        Ore drop = oreList.get(Constants.RNG.nextInt(oreList.size()));
                        ItemStack output = new ItemStack(ItemSmallOre.get(drop));
                        Helpers.spawnItemStack(world, pos.up(), output);
                        consumeSoil();
                    }
                    else
                    {
                        soil = 0;
                        ticksRemaining = 0;
                    }
                }
            }
            if (--delayTimer <= 0)
            {
                delayTimer = 20;
                for (EntityItem entityItem : world.getEntitiesWithinAABB(EntityItem.class, new AxisAlignedBB(pos).grow(1), EntitySelectors.IS_ALIVE))
                {
                    ItemStack stack = entityItem.getItem();
                    if (stack.getItem() instanceof ItemBlock && ((ItemBlock) stack.getItem()).getBlock() instanceof BlockRockVariant)
                    {
                        BlockRockVariant rockBlock = (BlockRockVariant) ((ItemBlock) stack.getItem()).getBlock();
                        if (rockBlock.getType() == Rock.Type.SAND || rockBlock.getType() == Rock.Type.GRAVEL)
                        {
                            soil += 20;
                            stack.shrink(1);
                            if (stack.getCount() <= 0)
                            {
                                entityItem.setDead();
                                break;
                            }
                        }
                    }
                }
                if (ticksRemaining <= 0)
                {
                    consumeSoil();
                }
            }
        }
    }

    @Override
    public void readFromNBT(@Nonnull NBTTagCompound nbt)
    {
        super.readFromNBT(nbt);
        soil = nbt.getInteger("soil");
        ticksRemaining = nbt.getInteger("ticksRemaining");
    }

    @Nonnull
    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("soil", soil);
        nbt.setInteger("ticksRemaining", ticksRemaining);
        return super.writeToNBT(nbt);
    }

    private void consumeSoil()
    {
        if (soil > 0)
        {
            soil--;
            ticksRemaining = ConfigTFC.GENERAL.sluiceTicks;
        }
        else
        {
            soil = 0;
            ticksRemaining = 0;
        }
    }
}
