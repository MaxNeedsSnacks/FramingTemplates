package ky.someone.mods.framingtemplates;


import ky.someone.mods.framingtemplates.item.FramingItems;
import ky.someone.mods.framingtemplates.item.FramingTemplateItem;
import ky.someone.mods.framingtemplates.recipe.FramingRecipes;
import ky.someone.mods.framingtemplates.util.FramingUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(FramingUtil.MOD_ID)
public class FramingTemplates {
	public FramingTemplates() {
		var modBus = FMLJavaModLoadingContext.get().getModEventBus();

		FramingItems.REGISTRY.register(modBus);
		FramingRecipes.REGISTRY.register(modBus);

		MinecraftForge.EVENT_BUS.addListener(this::onRightClick);
	}

	private void onRightClick(PlayerInteractEvent.RightClickItem event) {
		if (event.getHand() != InteractionHand.OFF_HAND) return;

		var player = event.getEntity();
		var mainHandItem = player.getMainHandItem();
		var offHandItem = player.getOffhandItem();

		if (offHandItem.getItem() instanceof FramingTemplateItem template) {
			if (template.applyTemplateToItem(offHandItem, mainHandItem)) {
				if (!player.getAbilities().instabuild) {
					offHandItem.shrink(1);
				}
				event.setCancellationResult(InteractionResult.SUCCESS);
				event.setCanceled(true);
			}
		}
	}
}
