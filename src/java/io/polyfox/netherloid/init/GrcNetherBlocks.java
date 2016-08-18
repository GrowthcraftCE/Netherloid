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
package io.polyfox.netherloid.init;

import growthcraft.core.common.definition.BlockDefinition;
import growthcraft.core.common.GrcModuleBlocks;
import growthcraft.core.eventhandler.PlayerInteractEventPaddy;
import growthcraft.core.integration.NEI;
import io.polyfox.netherloid.common.block.BlockNetherBaalsRot;
import io.polyfox.netherloid.common.block.BlockNetherCinderrot;
import io.polyfox.netherloid.common.block.BlockNetherFireLily;
import io.polyfox.netherloid.common.block.BlockNetherKnifeBush;
import io.polyfox.netherloid.common.block.BlockNetherMaliceFruit;
import io.polyfox.netherloid.common.block.BlockNetherMaliceLeaves;
import io.polyfox.netherloid.common.block.BlockNetherMaliceLog;
import io.polyfox.netherloid.common.block.BlockNetherMalicePlanks;
import io.polyfox.netherloid.common.block.BlockNetherMaliceSapling;
import io.polyfox.netherloid.common.block.BlockNetherMaraLotus;
import io.polyfox.netherloid.common.block.BlockNetherMuertecap;
import io.polyfox.netherloid.common.block.BlockNetherPaddy;
import io.polyfox.netherloid.common.block.BlockNetherPepper;
import io.polyfox.netherloid.common.block.BlockNetherSquash;
import io.polyfox.netherloid.common.block.BlockNetherSquashStem;
import io.polyfox.netherloid.common.item.ItemNetherLilyPad;

import net.minecraft.init.Blocks;
import net.minecraftforge.oredict.OreDictionary;

public class GrcNetherBlocks extends GrcModuleBlocks
{
	public BlockDefinition netherBaalsRot;
	public BlockDefinition netherCinderrot;
	public BlockDefinition netherFireLily;
	public BlockDefinition netherKnifeBush;
	public BlockDefinition netherMaliceFruit;
	public BlockDefinition netherMaliceLeaves;
	public BlockDefinition netherMaliceLog;
	public BlockDefinition netherMalicePlanks;
	public BlockDefinition netherMaliceSapling;
	public BlockDefinition netherMaraLotus;
	public BlockDefinition netherMuertecap;
	public BlockDefinition netherPaddyField;
	public BlockDefinition netherPaddyFieldFilled;
	public BlockDefinition netherPepper;
	public BlockDefinition netherSquash;
	public BlockDefinition netherSquashStem;

	public GrcNetherBlocks() {}

	@Override
	public void preInit()
	{
		this.netherBaalsRot = newDefinition(new BlockNetherBaalsRot());
		this.netherCinderrot = newDefinition(new BlockNetherCinderrot());
		this.netherFireLily = newDefinition(new BlockNetherFireLily());
		this.netherKnifeBush = newDefinition(new BlockNetherKnifeBush());
		this.netherMaliceFruit = newDefinition(new BlockNetherMaliceFruit());
		this.netherMaliceLeaves = newDefinition(new BlockNetherMaliceLeaves());
		this.netherMaliceLog = newDefinition(new BlockNetherMaliceLog());
		this.netherMalicePlanks = newDefinition(new BlockNetherMalicePlanks());
		this.netherMaliceSapling = newDefinition(new BlockNetherMaliceSapling());
		this.netherMaraLotus = newDefinition(new BlockNetherMaraLotus());
		this.netherMuertecap = newDefinition(new BlockNetherMuertecap());
		this.netherPaddyField = newDefinition(new BlockNetherPaddy(false));
		this.netherPaddyFieldFilled = newDefinition(new BlockNetherPaddy(true));
		this.netherPepper = newDefinition(new BlockNetherPepper());
		this.netherSquash = newDefinition(new BlockNetherSquash());
		this.netherSquashStem = newDefinition(new BlockNetherSquashStem(netherSquash.getBlock()));
	}

	private void hideItems()
	{
		NEI.hideItem(netherMuertecap.asStack());
		NEI.hideItem(netherMaliceFruit.asStack());
		NEI.hideItem(netherPaddyField.asStack());
		NEI.hideItem(netherPaddyFieldFilled.asStack());
		NEI.hideItem(netherPepper.asStack());
		NEI.hideItem(netherSquashStem.asStack());
	}

	@Override
	public void register()
	{
		netherBaalsRot.register("grcnether.netherBaalsRot");
		netherCinderrot.register("grcnether.netherCinderrot");
		netherFireLily.register("grcnether.netherFireLily", ItemNetherLilyPad.class);
		netherKnifeBush.register("grcnether.netherKnifeBush");
		netherMaliceFruit.register("grcnether.netherMaliceFruit");
		netherMaliceLeaves.register("grcnether.netherMaliceLeaves");
		netherMaliceLog.register("grcnether.netherMaliceLog");
		netherMalicePlanks.register("grcnether.netherMalicePlanks");
		netherMaliceSapling.register("grcnether.netherMaliceSapling");
		netherMaraLotus.register("grcnether.netherMaraLotus", ItemNetherLilyPad.class);
		netherMuertecap.register("grcnether.netherMuertecap");
		netherPaddyField.register("grcnether.netherPaddyField");
		netherPaddyFieldFilled.register("grcnether.netherPaddyFieldFilled");
		netherPepper.register("grcnether.netherPepperBlock");
		netherSquash.register("grcnether.netherSquash");
		netherSquashStem.register("grcnether.netherSquashStem");

		OreDictionary.registerOre("plankMaliceWood", netherMalicePlanks.getBlock());

		PlayerInteractEventPaddy.paddyBlocks.put(Blocks.soul_sand, netherPaddyField.getBlock());

		hideItems();
	}

}
