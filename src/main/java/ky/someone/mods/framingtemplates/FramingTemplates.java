package ky.someone.mods.framingtemplates;


import ky.someone.mods.framingtemplates.item.FramingItems;
import ky.someone.mods.framingtemplates.item.FramingTemplateItem;
import ky.someone.mods.framingtemplates.recipe.FramingRecipes;
import ky.someone.mods.framingtemplates.util.FramingUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(FramingUtil.MOD_ID)
public class FramingTemplates {

	public static final Logger LOGGER = LoggerFactory.getLogger(FramingUtil.MOD_NAME);

	public FramingTemplates() {
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();

		FramingItems.REGISTRY.register(modBus);
		FramingRecipes.REGISTRY.register(modBus);
		modBus.addListener(this::registerCreativeTab);

		MinecraftForge.EVENT_BUS.addListener(this::onRightClick);
	}

	private void registerCreativeTab(RegisterEvent event) {
		if (event.getRegistryKey() != Registries.CREATIVE_MODE_TAB) return;

		event.register(Registries.CREATIVE_MODE_TAB, (helper) -> {
			var entries = FramingItems.REGISTRY.getEntries();
			if (entries.isEmpty()) return;

			var allItems = entries.stream()
					.flatMap(RegistryObject::stream)
					.map(Item::getDefaultInstance)
					.toList();

			helper.register("tab", CreativeModeTab.builder()
					.title(Component.literal(FramingUtil.MOD_NAME))
					.displayItems((params, output) -> output.acceptAll(allItems))
					.icon(() -> allItems.get(0))
					.build());
		});
	}

	private void onRightClick(PlayerInteractEvent.RightClickItem event) {
		if (event.getHand() != InteractionHand.OFF_HAND) return;

		var player = event.getEntity();
		var mainHandItem = player.getMainHandItem();
		var offHandItem = player.getOffhandItem();

		if (offHandItem.getItem() instanceof FramingTemplateItem template) {
			var applied = template.applyTemplateToItem(offHandItem, mainHandItem);
			if (!applied.equals(mainHandItem, false)) {
				if (!player.getAbilities().instabuild) {
					if (offHandItem.getCount() < mainHandItem.getCount()) {
						return;
					}
					offHandItem.shrink(mainHandItem.getCount());
				}
				player.setItemInHand(InteractionHand.MAIN_HAND, applied);
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}
}
