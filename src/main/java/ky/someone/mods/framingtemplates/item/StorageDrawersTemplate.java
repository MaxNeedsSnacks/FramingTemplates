package ky.someone.mods.framingtemplates.item;

import eutros.framedcompactdrawers.block.tile.IFramingHolder;
import eutros.framedcompactdrawers.item.ItemDrawersCustom;
import eutros.framedcompactdrawers.item.ItemOtherCustom;
import ky.someone.mods.framingtemplates.util.DrawerSide;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class StorageDrawersTemplate extends FramingTemplateItem {

	public static final DrawerSide FRONT = DrawerSide.of("front", -1, 0, '⬅');
	public static final DrawerSide SIDE = DrawerSide.of("side", -1, -1, '⬉');
	public static final DrawerSide TRIM = DrawerSide.of("trim", 0, -1, '⬆');

	public StorageDrawersTemplate(String modid) {
		super(modid);
	}

	@Override
	public boolean applyTemplateInWorld(ItemStack stack, Level level, BlockPos pos, BlockState state) {
		var be = level.getBlockEntity(pos);
		if (be instanceof IFramingHolder frameable) {
			frameable.setFront(getDecoration(FRONT, stack));
			frameable.setSide(getDecoration(SIDE, stack));
			frameable.setTrim(getDecoration(TRIM, stack));

			be.requestModelDataUpdate();
			level.sendBlockUpdated(pos, state, state, 11);

			return true;
		}

		return false;
	}

	@Override
	public boolean applyTemplateToItem(ItemStack template, ItemStack target) {
		var item = target.getItem();
		if (item instanceof ItemDrawersCustom || item instanceof ItemOtherCustom) {
			var tag = target.getOrCreateTag();

			tag.put("MatF", getDecoration(FRONT, template).serializeNBT());
			tag.put("MatS", getDecoration(SIDE, template).serializeNBT());
			tag.put("MatT", getDecoration(TRIM, template).serializeNBT());

			return true;
		}
		return false;
	}

	@Override
	public DrawerSide[] getAllSides() {
		return new DrawerSide[]{SIDE, FRONT, TRIM};
	}
}
