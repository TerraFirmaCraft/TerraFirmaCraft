/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockChestTFC;
import net.dries007.tfc.objects.te.TEChestTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
public class TESRChestTFC extends TileEntitySpecialRenderer<TEChestTFC>
{
    private static final Map<Tree, ResourceLocation> SINGLE_TEXTURES = new HashMap<>();
    private static final Map<Tree, ResourceLocation> DOUBLE_TEXTURES = new HashMap<>();
    private static final Map<Tree, ResourceLocation> TRAP_SINGLE_TEXTURES = new HashMap<>();
    private static final Map<Tree, ResourceLocation> TRAP_DOUBLE_TEXTURES = new HashMap<>();

    static
    {
        for (Tree wood : TFCRegistries.TREES.getValuesCollection())
        {
            SINGLE_TEXTURES.put(wood, new ResourceLocation(MOD_ID, "textures/entity/chests/chest/" + wood.getRegistryName().getPath() + ".png"));
            DOUBLE_TEXTURES.put(wood, new ResourceLocation(MOD_ID, "textures/entity/chests/chest_double/" + wood.getRegistryName().getPath() + ".png"));
            TRAP_SINGLE_TEXTURES.put(wood, new ResourceLocation(MOD_ID, "textures/entity/chests/chest_trap/" + wood.getRegistryName().getPath() + ".png"));
            TRAP_DOUBLE_TEXTURES.put(wood, new ResourceLocation(MOD_ID, "textures/entity/chests/chest_trap_double/" + wood.getRegistryName().getPath() + ".png"));
        }
    }

    private final ModelChest simpleChest = new ModelChest();
    private final ModelChest largeChest = new ModelLargeChest();

    @Override
    public void render(TEChestTFC te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        Tree wood = te.getWood();
        if (!te.hasWorld()) return;
        if (te.getPriorityTE() != te) return;
        IBlockState chestState = te.getWorld().getBlockState(te.getPos());
        EnumFacing connectionFacing = te.getConnection();

        ModelChest modelchest;
        if (connectionFacing == null)
        {
            modelchest = simpleChest;

            if (destroyStage >= 0)
            {
                bindTexture(DESTROY_STAGES[destroyStage]);
                GlStateManager.matrixMode(5890);
                GlStateManager.pushMatrix();
                GlStateManager.scale(4.0F, 4.0F, 1.0F);
                GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                GlStateManager.matrixMode(5888);
            }
            else if (te.isTrapChest() && wood != null)
            {
                bindTexture(TRAP_SINGLE_TEXTURES.get(wood));
            }
            else if (wood != null)
            {
                bindTexture(SINGLE_TEXTURES.get(wood));
            }
        }
        else
        {
            modelchest = largeChest;

            if (destroyStage >= 0)
            {
                bindTexture(DESTROY_STAGES[destroyStage]);
                GlStateManager.matrixMode(5890);
                GlStateManager.pushMatrix();
                GlStateManager.scale(8.0F, 4.0F, 1.0F);
                GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
                GlStateManager.matrixMode(5888);
            }
            else if (te.isTrapChest() && wood != null)
            {
                bindTexture(TRAP_DOUBLE_TEXTURES.get(wood));
            }
            else if (wood != null)
            {
                bindTexture(DOUBLE_TEXTURES.get(wood));
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        if (destroyStage < 0) GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        float rotation = 0;
        //This is to stop the tearing effect on client while the packet for connection(what determines if TE is a double chest) hasn't arrived
        boolean flag = chestState.getValue(BlockChestTFC.FACING).rotateY() == te.getConnection();

        switch (chestState.getValue(BlockChestTFC.FACING))
        {
            case NORTH:
                rotation = 180;
                if (flag)
                {
                    GlStateManager.translate(1F, 0F, 0F);
                }
                break;
            case SOUTH:
                rotation = 0;
                if (flag)
                {
                    GlStateManager.translate(-1F, 0F, 0F);
                }
                break;
            case WEST:
                rotation = 90;
                if (flag)
                {
                    GlStateManager.translate(0.0F, 0F, 1F);
                }
                break;
            case EAST:
                rotation = -90;
                if (flag)
                {
                    GlStateManager.translate(0.0F, 0F, -1F);
                }
                break;
        }

        GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);

        float f = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;
        f = 1.0F - f;
        f = 1.0F - f * f * f;
        modelchest.chestLid.rotateAngleX = -(f * ((float) Math.PI / 2F));
        modelchest.renderAll();
        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        if (destroyStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }
}
