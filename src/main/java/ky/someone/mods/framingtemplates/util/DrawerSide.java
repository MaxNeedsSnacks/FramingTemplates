package ky.someone.mods.framingtemplates.util;

public record DrawerSide(String key, int offsetX, int offsetY, char symbol) {

	public static DrawerSide of(String key, int dx, int dy) {
		return new DrawerSide(key, dx, dy, '•');
	}

	public static DrawerSide of(String key, int dx, int dy, char symbol) {
		return new DrawerSide(key, dx, dy, symbol);
	}
}
