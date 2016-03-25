/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015, 2016 IceDragon200
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package growthcraft.nether.common.block;

import java.util.Random;

import growthcraft.core.common.block.ICropDataProvider;
import growthcraft.core.client.renderer.RenderBlockFruit;
import growthcraft.nether.Netherloid;
import growthcraft.api.core.util.BlockFlags;
import growthcraft.core.integration.AppleCore;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockNetherMaliceFruit extends Block implements IGrowable, ICropDataProvider
{
	public static class MaliceFruitStage
	{
		public static final int YOUNG = 0;
		public static final int MID = 1;
		public static final int MATURE = 2;
		public static final int COUNT = 3;

		private MaliceFruitStage() {}
	}

	@SideOnly(Side.CLIENT)
	private IIcon[] icons;

	private final int growth = Netherloid.getConfig().maliceFruitGrowthRate;
	private final boolean dropRipeMaliceFruit = Netherloid.getConfig().dropRipeMaliceFruit;
	private final int dropChance = Netherloid.getConfig().maliceFruitDropChance;

	public BlockNetherMaliceFruit()
	{
		super(Material.plants);
		setTickRandomly(true);
		setHardness(0.2F);
		setResistance(5.0F);
		setStepSound(soundTypeWood);
		setBlockName("grcnether.netherMaliceFruit");
		setCreativeTab(null);
	}

	public float getGrowthProgress(IBlockAccess world, int x, int y, int z, int meta)
	{
		return (float)meta / (float)MaliceFruitStage.MATURE;
	}

	void incrementGrowth(World world, int x, int y, int z, int meta)
	{
		world.setBlockMetadataWithNotify(x, y, z, meta + 1, BlockFlags.SYNC);
		AppleCore.announceGrowthTick(this, world, x, y, z, meta);
	}

	/* Can this accept bonemeal? */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient)
	{
		return world.getBlockMetadata(x, y, z) < MaliceFruitStage.MATURE;
	}

	/* SideOnly(Side.SERVER) Can this apply bonemeal effect? */
	@Override
	public boolean func_149852_a(World world, Random random, int x, int y, int z)
	{
		return true;
	}

	/* Apply bonemeal effect */
	@Override
	public void func_149853_b(World world, Random random, int x, int y, int z)
	{
		incrementGrowth(world, x, y, z, world.getBlockMetadata(x, y, z));
	}

	/**
	 * Drops the block as an item and replaces it with air
	 * @param world - world to drop in
	 * @param x - x Coord
	 * @param y - y Coord
	 * @param z - z Coord
	 */
	public void fellBlockAsItem(World world, int x, int y, int z)
	{
		this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
		world.setBlockToAir(x, y, z);
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (!this.canBlockStay(world, x, y, z))
		{
			this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
			world.setBlock(x, y, z, Blocks.air, 0, BlockFlags.SYNC);
		}
		else
		{
			final Event.Result allowGrowthResult = AppleCore.validateGrowthTick(this, world, x, y, z, random);
			if (allowGrowthResult == Event.Result.DENY)
				return;

			final boolean continueGrowth = random.nextInt(this.growth) == 0;
			if (allowGrowthResult == Event.Result.ALLOW || continueGrowth)
			{
				final int meta = world.getBlockMetadata(x, y, z);
				if (meta < MaliceFruitStage.MATURE)
				{
					incrementGrowth(world, x, y, z, meta);
				}
				else if (dropRipeMaliceFruit && world.rand.nextInt(this.dropChance) == 0)
				{
					fellBlockAsItem(world, x, y, z);
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int dir, float par7, float par8, float par9)
	{
		if (world.getBlockMetadata(x, y, z) >= MaliceFruitStage.MATURE)
		{
			if (!world.isRemote)
			{
				fellBlockAsItem(world, x, y, z);
			}
			return true;
		}
		return false;
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block)
	{
		if (!this.canBlockStay(world, x, y, z))
		{
			fellBlockAsItem(world, x, y, z);
		}
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return Netherloid.blocks.netherMaliceLeaves.getBlock() == world.getBlock(x, y + 1, z) &&
			(world.getBlockMetadata(x, y + 1, z) & 3) == 0;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getItem(World world, int x, int y, int z)
	{
		return Netherloid.items.netherMaliceFruit.getItem();
	}

	@Override
	public Item getItemDropped(int meta, Random random, int par3)
	{
		return meta >= MaliceFruitStage.MATURE ? Netherloid.items.netherMaliceFruit.getItem() : null;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderType()
	{
		return RenderBlockFruit.id;
	}

	@Override
	public boolean renderAsNormalBlock()
	{
		return false;
	}

	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		icons = new IIcon[MaliceFruitStage.COUNT];

		for (int i = 0; i < icons.length; ++i )
		{
			icons[i] = reg.registerIcon("grcnether:malice_fruit_" + i);
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return this.icons[meta];
	}

	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
	{
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getCollisionBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z)
	{
		this.setBlockBoundsBasedOnState(world, x, y, z);
		return super.getSelectedBoundingBoxFromPool(world, x, y, z);
	}

	@Override
	public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z)
	{
		final int meta = world.getBlockMetadata(x, y, z);
		final float f = 0.0625F;

		if (meta == MaliceFruitStage.YOUNG)
		{
			this.setBlockBounds(6*f, 11*f, 6*f, 10*f, 15*f, 10*f);
		}
		else if (meta == MaliceFruitStage.MID)
		{
			this.setBlockBounds((float)(5.5*f), 10*f, (float)(5.5*f), (float)(10.5*f), 15*f, (float)(10.5*f));
		}
		else
		{
			this.setBlockBounds(5*f, 9*f, 5*f, 11*f, 15*f, 11*f);
		}
	}
}
