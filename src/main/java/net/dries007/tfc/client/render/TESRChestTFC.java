/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import java.util.EnumMap;

import net.minecraft.block.BlockChest;
import net.minecraft.client.model.ModelChest;
import net.minecraft.client.model.ModelLargeChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.objects.blocks.wood.BlockChestTFC;
import net.dries007.tfc.objects.te.TEChestTFC;

import static net.dries007.tfc.Constants.MOD_ID;

@SideOnly(Side.CLIENT)
public class TESRChestTFC extends TileEntitySpecialRenderer<TEChestTFC>
{
    private static final EnumMap<Wood, ResourceLocation> SINGLE_TEXTURES = new EnumMap<>(Wood.class);
    private static final EnumMap<Wood, ResourceLocation> DOUBLE_TEXTURES = new EnumMap<>(Wood.class);
    private static final EnumMap<Wood, ResourceLocation> TRAP_SINGLE_TEXTURES = new EnumMap<>(Wood.class);
    private static final EnumMap<Wood, ResourceLocation> TRAP_DOUBLE_TEXTURES = new EnumMap<>(Wood.class);

    static
    {
        for (Wood wood : Wood.values())
        {
            SINGLE_TEXTURES.put(wood, new ResourceLocation(MOD_ID, "textures/blocks/wood/chests/chest/" + wood.name().toLowerCase() + ".png"));
            DOUBLE_TEXTURES.put(wood, new ResourceLocation(MOD_ID, "textures/blocks/wood/chests/chest_double/" + wood.name().toLowerCase() + ".png"));
            TRAP_SINGLE_TEXTURES.put(wood, new ResourceLocation(MOD_ID, "textures/blocks/wood/chests/chest_trap/" + wood.name().toLowerCase() + ".png"));
            TRAP_DOUBLE_TEXTURES.put(wood, new ResourceLocation(MOD_ID, "textures/blocks/wood/chests/chest_trap_double/" + wood.name().toLowerCase() + ".png"));
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
        int meta = 0;

        if (te.hasWorld())
        {
            BlockChestTFC block = te.getBlockType();
            meta = te.getBlockMetadata();

            if (block != null && meta == 0)
            {
                block.checkForSurroundingChests(te.getWorld(), te.getPos(), te.getWorld().getBlockState(te.getPos()));
                meta = te.getBlockMetadata();
            }

            te.checkForAdjacentChests();
        }

        if (te.adjacentChestZNeg != null || te.adjacentChestXNeg != null) return;

        ModelChest modelchest;
        if (te.adjacentChestXPos == null && te.adjacentChestZPos == null)
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
            else if (te.getChestType() == BlockChest.Type.TRAP)
            {
                bindTexture(TRAP_SINGLE_TEXTURES.get(te.getWood()));
            }
            else
            {
                bindTexture(SINGLE_TEXTURES.get(te.getWood()));
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
            else if (te.getChestType() == BlockChest.Type.TRAP)
            {
                bindTexture(TRAP_DOUBLE_TEXTURES.get(te.getWood()));
            }
            else
            {
                bindTexture(DOUBLE_TEXTURES.get(te.getWood()));
            }
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();

        if (destroyStage < 0) GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);

        GlStateManager.translate((float) x, (float) y + 1.0F, (float) z + 1.0F);
        GlStateManager.scale(1.0F, -1.0F, -1.0F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        int rotation = 0;

        switch (meta)
        {
            case 2:
                rotation = 180;
                if (te.adjacentChestXPos != null) GlStateManager.translate(1.0F, 0.0F, 0.0F);
                break;
            case 3:
                rotation = 0;
                break;
            case 4:
                rotation = 90;
                break;
            case 5:
                rotation = -90;
                if (te.adjacentChestZPos != null) GlStateManager.translate(0.0F, 0.0F, -1.0F);
                break;
        }

        GlStateManager.rotate((float) rotation, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        float lidAngle = te.prevLidAngle + (te.lidAngle - te.prevLidAngle) * partialTicks;

        if (te.adjacentChestZNeg != null)
        {
            float f1 = te.adjacentChestZNeg.prevLidAngle + (te.adjacentChestZNeg.lidAngle - te.adjacentChestZNeg.prevLidAngle) * partialTicks;
            if (f1 > lidAngle) lidAngle = f1;
        }

        if (te.adjacentChestXNeg != null)
        {
            float f2 = te.adjacentChestXNeg.prevLidAngle + (te.adjacentChestXNeg.lidAngle - te.adjacentChestXNeg.prevLidAngle) * partialTicks;
            if (f2 > lidAngle) lidAngle = f2;
        }

        lidAngle = 1.0F - lidAngle;
        lidAngle = 1.0F - lidAngle * lidAngle * lidAngle;
        modelchest.chestLid.rotateAngleX = -(lidAngle * ((float) Math.PI / 2F));
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
