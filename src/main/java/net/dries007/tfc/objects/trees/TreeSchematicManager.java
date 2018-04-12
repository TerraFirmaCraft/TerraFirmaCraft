package net.dries007.tfc.objects.trees;

import net.dries007.tfc.objects.Wood;
import net.dries007.tfc.util.Schematic;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * Ported and modified version of TFC2 code by Bioxx
 */
public class TreeSchematicManager
{
	/**
	 * Vector of Vectors. List of schematics is sorted first by growth stage, then schem ID
	 */
	private Vector<Vector<TreeSchematic>> treeList;

	public TreeSchematicManager()
	{
		this.treeList = new Vector<Vector<TreeSchematic>>();
	}

	public void addSchematic(TreeSchematic treeSchematic)
	{
		int growth = treeSchematic.getGrowthStage();

		if(treeList.size() <= growth)
			treeList.setSize(growth + 1);

		if(treeList.get(growth) == null)
			treeList.set(growth, new Vector<TreeSchematic>());

		treeList.get(growth).add(treeSchematic);
	}

	/**
	 * @return Returns a treeschem of any growth stage
	 */
	public TreeSchematic getRandomSchematic(Random random)
	{
		Vector<TreeSchematic> v = treeList.get(random.nextInt(treeList.size()));
		if(v != null)
		{
			return v.get(random.nextInt(v.size()));
		}
		return null;
	}

	/**
	 * @return Returns a treeschem of specified growth stage
	 */
	public TreeSchematic getRandomSchematic(Random random, int growthStage)
	{
		//Gets the list of schems for the specified growth stages
		if(growthStage > treeList.size() - 1) return null;
		Vector<TreeSchematic> v = treeList.get(growthStage);
		if(v != null)
		{
			return v.get(random.nextInt(v.size()));
		}
		return null;
	}

	public TreeSchematic getSchematic(int schem, int growthStage)
	{
		//Gets the list of schems for the specified growth stages
		if(growthStage > treeList.size() - 1) return null;
		Vector<TreeSchematic> v = treeList.get(growthStage);
		if(v != null)
		{
			return v.get(schem);
		}
		return null;
	}

	public static class TreeSchematic extends Schematic {
		private int size;
		private Wood wood;
		private int baseCount = 0;
		private int logCount = 0;

		public TreeSchematic(String path, String filename, Wood wood)
		{
			super(path, filename);
			this.wood = wood;
		}

		@Override
		public void PostProcess()
		{
			ArrayList<SchematicBlock> map = new ArrayList<SchematicBlock>();
			for(SchematicBlock b : blockMap)
			{
				if(b.state.getBlock() != Blocks.AIR)
				{
					if(b.blockPos.getY() == 0)
						baseCount++;
					map.add(b);
					if(b.state.getBlock().getMaterial(b.state) == Material.WOOD)
						logCount++;
				}

			}
			blockMap = map;

			int num = filename.indexOf('_');
			String s = filename.substring(0, num);
			if(s.equals("large"))
				size = 2;
			else if(s.equals("normal"))
				size = 1;
			else
				size = 0;

			aabb = new AxisAlignedBB(0, 0, 0, width, height, length);
		}

		@Override
		public AxisAlignedBB getBoundingBox(BlockPos blockPos)
		{
			return aabb.grow(blockPos.getX()-getCenterX()+1, blockPos.getY(), blockPos.getZ()-getCenterZ()+1);
		}

		public int getBaseCount()
		{
			return this.baseCount;
		}

		public int getLogCount()
		{
			return this.logCount;
		}

		public int getGrowthStage()
		{
			return size;
		}

		public Wood getWoodType()
		{
			return wood;
		}
	}
}
