package ky.someone.mods.framingtemplates.data;

import ky.someone.mods.framingtemplates.item.FramingItems;
import ky.someone.mods.framingtemplates.util.FramingUtil;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class FramingLangProvider extends LanguageProvider {
	public FramingLangProvider(PackOutput output) {
		super(output, FramingUtil.MOD_ID, "en_us");
	}

	@Override
	protected void addTranslations() {
		// once again, datagen is safe
		assert FramingItems.STORAGE_DRAWERS_TEMPLATE != null;
		assert FramingItems.FUNCTIONAL_STORAGE_TEMPLATE != null;

		addItem(FramingItems.STORAGE_DRAWERS_TEMPLATE, "Drawer Framing Template");
		addItem(FramingItems.FUNCTIONAL_STORAGE_TEMPLATE, "Functional Storage Framing Template");

		add("tooltip.framing_templates.applied_decorations", "Decorated with");
		add("tooltip.framing_templates.applicable_to", "Applicable to framed drawers from %s");
		add("tooltip.framing_templates.makes_copy", "(Creates a copy of the input template)");
		// Storage Drawers sides
		add("tooltip.framing_templates.sides.side", "Side");
		add("tooltip.framing_templates.sides.front", "Front");
		add("tooltip.framing_templates.sides.trim", "Trim");
		add("tooltip.framing_templates.sides.front_divider", "Divider");
	}
}
