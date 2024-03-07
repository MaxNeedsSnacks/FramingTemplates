package ky.someone.mods.framingtemplates.util;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

public interface FramingUtil {
	String MOD_ID = "framing_templates";
	String MOD_NAME = "Framing Templates";

	UnaryOperator<Style> TOOLTIP_MAIN = (style) -> style.withColor(0xfcb95b);
	UnaryOperator<Style> TOOLTIP_EXTRA = (style) -> style.withColor(0x0fd1ec).withItalic(true);
	UnaryOperator<Style> TOOLTIP_FLAVOUR = (style) -> style.withColor(ChatFormatting.GRAY);

	UnaryOperator<Style> TOOLTIP_GREEN = (style) -> style.withColor(0x4ecc8d);
	UnaryOperator<Style> TOOLTIP_RED = (style) -> style.withColor(0xfd6d5d);
	UnaryOperator<Style> TOOLTIP_BLUE = (style) -> style.withColor(0x5555ff);

	static ResourceLocation id(String path) {
		return new ResourceLocation(MOD_ID, path);
	}

	static <U> U TODO() {
		throw new UnsupportedOperationException("Not implemented yet");
	}
}
