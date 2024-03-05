package ky.someone.mods.framingtemplates.data;

import ky.someone.mods.framingtemplates.item.FramingItems;
import ky.someone.mods.framingtemplates.util.FramingUtil;
import net.minecraft.data.PackOutput;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class FramingItemModelProvider extends ItemModelProvider {
	public FramingItemModelProvider(PackOutput output, ExistingFileHelper efh) {
		super(output, FramingUtil.MOD_ID, efh);
	}

	@Override
	protected void registerModels() {
		basicItem(FramingItems.STORAGE_DRAWERS_TEMPLATE.get());
		basicItem(FramingItems.FUNCTIONAL_STORAGE_TEMPLATE.get());
	}
}
