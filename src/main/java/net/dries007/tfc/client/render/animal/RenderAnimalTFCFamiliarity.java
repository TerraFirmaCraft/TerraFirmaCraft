/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

import org.lwjgl.opengl.GL11;

import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class RenderAnimalTFCFamiliarity
{
	private static final ResourceLocation ICONS = new ResourceLocation(MOD_ID, "textures/gui/overlay/icons.png");
	private static final RenderAnimalTFCFamiliarity INSTANCE = new RenderAnimalTFCFamiliarity();

	public static RenderAnimalTFCFamiliarity getInstance() { return INSTANCE; }

	@SuppressWarnings("PointlessArithmeticExpression")
	public void drawTexturedModalRect(float xCoord, float yCoord, int minU, int minV, int maxU, int maxV)
	{
		float textureScaleU = 0.00390625F;
		float textureScaleV = 0.00390625F;
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder vb = tessellator.getBuffer();
		vb.begin(7, DefaultVertexFormats.POSITION_TEX);
		vb.pos(xCoord + 0.0F, yCoord + maxV, 0).tex((minU + 0) * textureScaleU, (minV + maxV) * textureScaleV)
				.endVertex();
		vb.pos(xCoord + maxU, yCoord + maxV, 0).tex((minU + maxU) * textureScaleU, (minV + maxV) * textureScaleV)
				.endVertex();
		vb.pos(xCoord + maxU, yCoord + 0.0F, 0).tex((minU + maxU) * textureScaleU, (minV + 0) * textureScaleV)
				.endVertex();
		vb.pos(xCoord + 0.0F, yCoord + 0.0F, 0).tex((minU + 0) * textureScaleU, (minV + 0) * textureScaleV).endVertex();
		tessellator.draw();
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public void renderAnimalFamiliarity(RenderLivingEvent.Post<EntityAnimalTFC> event)
	{
		Minecraft mc = Minecraft.getMinecraft();
		EntityPlayer player = mc.player.inventory.player;

		if (player.isSneaking())
		{
			EntityLivingBase entity = event.getEntity();
			if (entity instanceof EntityAnimalTFC)
			{
				double x, y, z;
				x = event.getX();
				y = event.getY();
				z = event.getZ();

				float f = 1.6F;
				float f1 = 0.016666668F * f;
				double d3 = entity.getDistance(player);
				float f2 = 5.0F;

				if (d3 < f2)
				{
					EntityAnimalTFC animal = (EntityAnimalTFC) entity;
					RenderManager rendermanager = mc.getRenderManager();

					GL11.glPushMatrix();
					GL11.glTranslatef((float) x + 0.0F, (float) y + entity.height + 0.75F, (float) z);
					GL11.glRotatef(-rendermanager.playerViewY, 0.0F, 1.0F, 0.0F);
					GL11.glRotatef(rendermanager.playerViewX, 1.0F, 0.0F, 0.0F);
					GL11.glScalef(-f1, -f1, f1);
					GL11.glDisable(GL11.GL_LIGHTING);
					GL11.glTranslatef(0.0F, 0.25F / f1, 0.0F);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					mc.renderEngine.bindTexture(ICONS);
					GL11.glScalef(0.33F, 0.33F, 0.33F);

					float familiarity = Math.max(0.0F, Math.min(1.0F, animal.getFamiliarity()));
					if (familiarity >= 0.3F) {
						this.drawTexturedModalRect(-8, 0, 112, 40, 16, 16);
					} else {
						this.drawTexturedModalRect(-8, 0, 92, 40, 16, 16);
					}

					GL11.glTranslatef(0, 0, -0.001F);

					if (familiarity == 1.0F) {
						this.drawTexturedModalRect(-6, 14 - (int) (12 * familiarity), 114, 74 - (int) (12 * familiarity), 12, (int) (12 * familiarity));
					} else {
						this.drawTexturedModalRect(-6, 14 - (int) (12 * familiarity), 94, 74 - (int) (12 * familiarity), 12, (int) (12 * familiarity));
					}

					GL11.glDepthMask(true);
					GL11.glEnable(GL11.GL_LIGHTING);
					GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
					GL11.glPopMatrix();
				}
			}
		}
	}
}
