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

import growthcraft.api.core.util.BlockFlags;
import growthcraft.nether.common.world.WorldGeneratorMaliceTree;
import growthcraft.nether.Netherloid;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockBush;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraftforge.event.terraingen.TerrainGen;

public class BlockNetherMaliceSapling extends BlockBush implements IGrowable
{
	@SideOnly(Side.CLIENT)
	private IIcon icon;

	private final int growth = 8;

	public BlockNetherMaliceSapling()
	{
		super(Material.plants);
		setHardness(0.0F);
		setStepSound(soundTypeGrass);
		setBlockName("grcnether.netherMaliceSapling");
		setTickRandomly(true);
		setCreativeTab(Netherloid.tab);
		final float f = 0.4F;
		setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
	}

	/************
	 * MAIN
	 ************/
	public void updateTick(World world, int x, int y, int z, Random random)
	{
		if (!world.isRemote)
		{
			super.updateTick(world, x, y, z, random);

			if (world.getBlockLightValue(x, y + 1, z) >= 9 && random.nextInt(this.growth) == 0)
			{
				this.markOrGrowMarked(world, x, y, z, random);
			}
		}
	}

	public void markOrGrowMarked(World world, int x, int y, int z, Random random)
	{
		final int meta = world.getBlockMetadata(x, y, z);

		if ((meta & 8) == 0)
		{
			world.setBlockMetadataWithNotify(x, y, z, meta | 8, 4);
		}
		else
		{
			this.growTree(world, x, y, z, random);
		}
	}

	public void growTree(World world, int x, int y, int z, Random random)
	{
		if (!TerrainGen.saplingGrowTree(world, random, x, y, z)) return;

		final int meta = world.getBlockMetadata(x, y, z) & 3;
		final WorldGenerator generator = new WorldGeneratorMaliceTree(true);

		world.setBlock(x, y, z, Blocks.air, 0, BlockFlags.ALL);

		if (!generator.generate(world, random, x, y, z))
		{
			world.setBlock(x, y, z, this, meta, BlockFlags.ALL);
		}
	}

	/* Both side */
	@Override
	public boolean func_149851_a(World world, int x, int y, int z, boolean isClient)
	{
		return (world.getBlockMetadata(x, y, z) & 8) == 0;
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
		if (random.nextFloat() < 0.45D)
		{
			growTree(world, x, y, z, random);
		}
	}

	/************
	 * TEXTURES
	 ************/
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg)
	{
		icon = reg.registerIcon("grcnether:malicesapling");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta)
	{
		return this.icon;
	}
}
