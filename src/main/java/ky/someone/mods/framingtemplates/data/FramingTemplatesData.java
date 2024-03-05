package ky.someone.mods.framingtemplates.data;

import ky.someone.mods.framingtemplates.util.FramingUtil;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FramingUtil.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FramingTemplatesData {

	@SubscribeEvent
	public static void onGatherData(GatherDataEvent event) {
		var gen = event.getGenerator();
		var output = gen.getPackOutput();
		var efh = event.getExistingFileHelper();

		gen.addProvider(event.includeServer(), new FramingRecipesProvider(output));

		gen.addProvider(event.includeClient(), new FramingItemModelProvider(output, efh));
		gen.addProvider(event.includeClient(), new FramingLangProvider(output));
	}
}
