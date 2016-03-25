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
package id2h.netherloid.common.block;

import java.util.Random;

import growthcraft.api.core.util.BlockFlags;
import growthcraft.api.core.util.RenderType;
import growthcraft.core.common.block.ICropDataProvider;
import growthcraft.core.integration.AppleCore;
import id2h.netherloid.Netherloid;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IPlantable;

public class BlockNetherSquashStem extends BlockBush implements ICropDataProvider, IGrowable, IPlantable
{
	public static class StemStage
	{
		public static final int MATURE = 7;

		private StemStage() {}
	}

	private final Block fruitBlock;

	@SideOnly(Side.CLIENT)
	private IIcon stemConnectedIcon;

	public BlockNetherSquashStem(Block block)
	{
		super();
		this.fruitBlock = block;
		setTickRandomly(true);
		setCreativeTab(null);
		setBlockTextureName("grcnether:soulsquash_stem");
	}

	@Override
	public float getGrowthProgress(IBlockAccess world, int x, int y, int z, int meta)
	{
		return (float)meta / (float)StemStage.MATURE;
	}

	protected boolean func_149854_a(Block block)
	{
		return Blocks.soul_sand == block;
	}

	@Override
	public boolean canPlaceBlockAt(World world, int x, int y, int z)
	{
		return super.canPlaceBlockAt(world, x, y, z) && func_149854_a(world.getBlock(x, y + 1, z));
	}

	@Override
	public boolean canBlockStay(World world, int x, int y, int z)
	{
		return func_149854_a(world.getBlock(x, y + 1, z));
	}

	public boolean hasGrownFruit(World world, int x, int y, int z)
	{
		return fruitBlock == world.getBlock(x, y - 1, z);
	}

	public boolean canGrowFruit(World world, int x, int y, int z)
	{
		return world.isAirBlock(x, y - 1, z);
	}

	public void incrementGrowth(World world, int x, int y, int z, int previousMeta)
	{
		final int meta = MathHelper.clamp_int(previousMeta + MathHelper.getRandomIntegerInRange(world.rand, 2, 5), 0, StemStage.MATURE);
		world.setBlockMetadataWithNotify(x, y, z, meta + 1, BlockFlags.SYNC);
		AppleCore.announceGrowthTick(this, world, x, y, z, previousMeta);
	}

	private void growStem(World world, int x, int y, int z, int meta)
	{
		if (meta < StemStage.MATURE)
		{
			incrementGrowth(world, x, y, z, meta);
		}
		else if (canGrowFruit(world, x, y, z))
		{
			world.setBlock(x, y - 1, z, fruitBlock, world.rand.nextInt(4), BlockFlags.SYNC);
		}
	}

	@Override
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		final Event.Result allowGrowthResult = AppleCore.validateGrowthTick(this, world, x, y, z, random);
		if (allowGrowthResult == Event.Result.DENY)
			return;

		if (allowGrowthResult == Event.Result.ALLOW || random.nextInt(10) == 0)
		{
			final int meta = world.getBlockMetadata(x, y, z);
			growStem(world, x, y, z, meta);
		}
	}

	/* Client Side: can bonemeal */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient)
	{
		return world.getBlockMetadata(x, y, z) < StemStage.MATURE || canGrowFruit(world, x, y, z);
	}

	/* SideOnly(Side.SERVER) Can this apply bonemeal effect? */
	@Override
	public boolean func_149852_a(World world, Random random, int x, int y, int z)
	{
		return true;
	}

	/* IGrowable: Apply bonemeal effect */
	@Override
	public void func_149853_b(World world, Random random, int x, int y, int z)
	{
		growStem(world, x, y, z, world.getBlockMetadata(x, y, z));
	}

	@Override
	public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int weight)
	{
		super.dropBlockAsItemWithChance(world, x, y, z, meta, f, weight);
		if (!world.isRemote)
		{
			final ItemStack item = Netherloid.items.netherSquashSeeds.asStack();
			for (int i = 0; i < 3; ++i)
			{
				if (world.rand.nextInt(15) <= meta)
				{
					//dropBlockAsItem_do(world, x, y, z, item);
				}
			}
		}
	}

	@Override
	public Item getItemDropped(int meta, Random random, int par3)
	{
		return null;
	}

	@Override
	public int quantityDropped(Random random)
	{
		return 1;
	}

	@Override
	public Item getItem(World world, int x, int y, int z)
	{
		return Netherloid.items.netherSquashSeeds.getItem();
	}

	@Override
	public int getRenderType()
	{
		return RenderType.BUSH;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		this.blockIcon = reg.registerIcon(this.getTextureName() + "_disconnected");
		this.stemConnectedIcon = reg.registerIcon(this.getTextureName() + "_connected");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		if ((meta & 8) != 0)
		{
			return stemConnectedIcon;
		}
		return blockIcon;
	}
}
