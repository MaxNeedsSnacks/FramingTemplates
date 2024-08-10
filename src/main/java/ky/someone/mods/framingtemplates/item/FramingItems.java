package ky.someone.mods.framingtemplates.item;

import ky.someone.mods.framingtemplates.util.FramingUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

// oh you're kidding, THAT is the issue? sometimes, you hurt me, java...
@SuppressWarnings("Convert2MethodRef")
public interface FramingItems {
	DeferredRegister<Item> REGISTRY = DeferredRegister.create(Registries.ITEM, FramingUtil.MOD_ID);

	RegistryObject<FramingTemplateItem> FUNCTIONAL_STORAGE_TEMPLATE = forMod("functional_storage_template", "functionalstorage", modid -> new FunctionalStorageTemplate(modid));
	RegistryObject<FramingTemplateItem> STORAGE_DRAWERS_TEMPLATE = forMod("storage_drawers_template", "framedcompactdrawers", modid -> new StorageDrawersTemplate(modid));

	static <T extends FramingTemplateItem> RegistryObject<T> forMod(String id, String modid, Function<String, T> factory) {
		if (ModList.get().isLoaded(modid)) {
			return REGISTRY.register(id, () -> factory.apply(modid));
		}

		return null;
	}
}
