package ky.someone.mods.framingtemplates.item;

import com.buuz135.functionalstorage.block.DrawerBlock;
import com.buuz135.functionalstorage.block.FramedDrawerBlock;
import com.buuz135.functionalstorage.block.tile.FramedDrawerTile;
import com.buuz135.functionalstorage.client.model.FramedDrawerModelData;
import ky.someone.mods.framingtemplates.util.DrawerSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Map;

public class FunctionalStorageTemplate extends FramingTemplateItem {

	public static final DrawerSide SIDE = DrawerSide.of("side", 0, -1, '⬆');
	public static final DrawerSide FRONT = DrawerSide.of("front", 1, -1, '⬈');
	public static final DrawerSide FRONT_DIVIDER = DrawerSide.of("front_divider", 1, 0, '➡');

	public FunctionalStorageTemplate(String modid) {
		super(modid);
	}

	@Override
	public boolean applyTemplateInWorld(ItemStack stack, Level level, BlockPos pos, BlockState state) {
		var be = level.getBlockEntity(pos);
		if (be instanceof FramedDrawerTile framedDrawer) {
			var modelData = new FramedDrawerModelData(Map.of());
			modelData.deserializeNBT(makeModelData(stack));
			framedDrawer.setFramedDrawerModelData(modelData);

			be.requestModelDataUpdate();
			level.sendBlockUpdated(pos, state, state, 11);

			return true;
		}

		return false;
	}

	@Override
	public boolean applyTemplateToItem(ItemStack template, ItemStack target) {
		var item = target.getItem();
		if (item instanceof DrawerBlock.DrawerItem drawer && drawer.getBlock() instanceof FramedDrawerBlock) {
			target.getOrCreateTag().put("Style", makeModelData(template));
			return true;
		}
		return false;
	}

	private CompoundTag makeModelData(ItemStack template) {
		var side = getDecoration(SIDE, template);
		var front = getDecoration(FRONT, template);
		var trim = getDecoration(FRONT_DIVIDER, template);

		var tag = new CompoundTag();
		if (!side.isEmpty()) {
			var sideItem = BuiltInRegistries.ITEM.getKey(side.getItem());
			tag.putString("particle", sideItem.toString());
			tag.putString("side", sideItem.toString());
			tag.putString("front_divider", sideItem.toString());
		}
		if (!front.isEmpty()) {
			tag.putString("front", BuiltInRegistries.ITEM.getKey(front.getItem()).toString());
		}
		if (!trim.isEmpty()) {
			tag.putString("front_divider", BuiltInRegistries.ITEM.getKey(trim.getItem()).toString());
		}
		return tag;
	}

	@Override
	public DrawerSide[] getAllSides() {
		return new DrawerSide[]{SIDE, FRONT, FRONT_DIVIDER};
	}
}
