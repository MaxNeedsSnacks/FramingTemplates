package ky.someone.mods.framingtemplates.item;

import com.jaquadro.minecraft.storagedrawers.block.*;
import com.jaquadro.minecraft.storagedrawers.block.tile.BaseBlockEntity;
import eutros.framedcompactdrawers.block.ModBlocks;
import eutros.framedcompactdrawers.block.tile.IFramingHolder;
import eutros.framedcompactdrawers.item.ItemDrawersCustom;
import eutros.framedcompactdrawers.item.ItemOtherCustom;
import ky.someone.mods.framingtemplates.util.DrawerSide;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

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

		if (be instanceof BaseBlockEntity base) {
			if (!(be instanceof IFramingHolder frameable)) {
				// Make it so. (this is disgusting)
				var tag = be.saveWithoutMetadata();

				var block = tryConvertDrawer(state.getBlock());
				if (block != null && block != state.getBlock()) {
					// aesthetics(tm)
					level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
					level.setBlockAndUpdate(pos, block.withPropertiesOf(state));
					be = level.getBlockEntity(pos);
					be.load(tag);
				}
			}
		}

		if (be instanceof IFramingHolder frameable) {
			frameable.setFront(getDecoration(FRONT, stack));
			frameable.setSide(getDecoration(SIDE, stack));
			frameable.setTrim(getDecoration(TRIM, stack));

			be.requestModelDataUpdate();

			return true;
		}

		return false;
	}

	@Override
	public ItemStack applyTemplateToItem(ItemStack template, ItemStack target) {
		var item = target.getItem();
		if (item instanceof BlockItem blockItem) {
			if (!(item instanceof ItemDrawersCustom || item instanceof ItemOtherCustom)) {
				var converted = tryConvertDrawer(blockItem.getBlock());
				if (converted == null) return target;

				if (!target.is(converted.asItem())) {
					target = new ItemStack(converted, target.getCount(), target.getTag());
				}
			}

			var tag = target.getOrCreateTag();

			tag.put("MatF", getDecoration(FRONT, template).serializeNBT());
			tag.put("MatS", getDecoration(SIDE, template).serializeNBT());
			tag.put("MatT", getDecoration(TRIM, template).serializeNBT());
		}

		return target;
	}

	@Nullable
	private Block tryConvertDrawer(Block block) {
		if (block instanceof BlockDrawers drawers) {
			return switch (drawers.getDrawerCount()) {
				case 1 -> drawers.isHalfDepth() ? ModBlocks.framedHalfOne : ModBlocks.framedFullOne;
				case 2 -> drawers.isHalfDepth() ? ModBlocks.framedHalfTwo : ModBlocks.framedFullTwo;
				case 4 -> drawers.isHalfDepth() ? ModBlocks.framedHalfFour : ModBlocks.framedFullFour;
				default -> block instanceof BlockCompDrawers ? ModBlocks.framedCompactDrawer : null;
			};
		} else if (block instanceof BlockTrim) {
			return ModBlocks.framedTrim;
		} else if (block instanceof BlockController) {
			return ModBlocks.framedDrawerController;
		} else if (block instanceof BlockSlave) {
			return ModBlocks.framedSlave;
		}

		return null;
	}

	@Override
	public DrawerSide[] getAllSides() {
		return new DrawerSide[]{SIDE, FRONT, TRIM};
	}
}
