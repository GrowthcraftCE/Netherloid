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
package id2h.netherloid;

import growthcraft.api.core.log.ILogger;
import growthcraft.api.core.log.GrcLogger;
import growthcraft.api.core.module.ModuleContainer;
import id2h.netherloid.client.event.TextureStitchEventHandler;
import id2h.netherloid.common.CommonProxy;
import id2h.netherloid.creativetab.CreativeTabsGrowthcraftNether;
import id2h.netherloid.init.GrcNetherBlocks;
import id2h.netherloid.init.GrcNetherFluids;
import id2h.netherloid.init.GrcNetherItems;

import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.common.MinecraftForge;

@Mod(
	modid = Netherloid.MOD_ID,
	name = Netherloid.MOD_NAME,
	version = Netherloid.MOD_VERSION,
	dependencies = Netherloid.MOD_DEPENDENCIES
)
public class Netherloid
{
	public static final String MOD_ID = "Netherloid";
	public static final String MOD_NAME = "Netherloid";
	public static final String MOD_VERSION = "@VERSION@";
	public static final String MOD_DEPENDENCIES = "required-after:Growthcraft;required-after:Growthcraft|Cellar";

	@Instance(MOD_ID)
	public static Netherloid instance;

	public static CreativeTabs tab;
	public static GrcNetherBlocks blocks = new GrcNetherBlocks();
	public static GrcNetherItems items = new GrcNetherItems();
	public static GrcNetherFluids fluids = new GrcNetherFluids();

	private ILogger logger = new GrcLogger(MOD_ID);
	private GrcNetherConfig config = new GrcNetherConfig();
	private ModuleContainer modules = new ModuleContainer();

	public static GrcNetherConfig getConfig()
	{
		return instance.config;
	}

	@EventHandler
	public void preinit(FMLPreInitializationEvent event)
	{
		config.load(event.getModConfigurationDirectory(), "growthcraft/nether.conf");

		tab = new CreativeTabsGrowthcraftNether();

		modules.add(blocks);
		modules.add(items);
		modules.add(fluids);

		if (config.enableThaumcraftIntegration) modules.add(new id2h.netherloid.integration.ThaumcraftModule());

		if (config.debugEnabled) modules.setLogger(logger);

		modules.freeze();

		modules.preInit();
		modules.register();

		MinecraftForge.EVENT_BUS.register(new TextureStitchEventHandler());
	}

	@EventHandler
	public void init(FMLInitializationEvent event)
	{
		CommonProxy.instance.initRenders();

		modules.init();
	}

	@EventHandler
	public void postinit(FMLPostInitializationEvent event)
	{
		modules.postInit();
	}
}
