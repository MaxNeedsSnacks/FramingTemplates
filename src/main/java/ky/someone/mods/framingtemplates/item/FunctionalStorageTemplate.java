package ky.someone.mods.framingtemplates.item;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.*;
import com.buuz135.functionalstorage.block.tile.*;
import com.buuz135.functionalstorage.client.model.FramedDrawerModelData;
import com.buuz135.functionalstorage.util.DrawerWoodType;
import ky.someone.mods.framingtemplates.util.DrawerSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
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
		var block = convertToFramed(state.getBlock());

		if (block == null) return false;

		var modelData = makeModelData(stack);

		if (block != state.getBlock()) {
			// this is... just as disgusting as the storage drawers one >.>
			var tag = be.saveWithoutMetadata();

			level.levelEvent(LevelEvent.PARTICLES_DESTROY_BLOCK, pos, Block.getId(state));
			level.setBlockAndUpdate(pos, block.withPropertiesOf(state));
			be = level.getBlockEntity(pos);
			be.load(tag);
		}

		// buuz why isn't this an interface?
		if (be instanceof FramedDrawerTile framed) {
			framed.setFramedDrawerModelData(modelData);
		} else if (be instanceof FramedDrawerControllerTile framed) {
			framed.setFramedDrawerModelData(modelData);
		} else if (be instanceof CompactingFramedDrawerTile framed) {
			framed.setFramedDrawerModelData(modelData);
		} else if (be instanceof FramedSimpleCompactingDrawerTile framed) {
			framed.setFramedDrawerModelData(modelData);
		} else if (be instanceof FramedControllerExtensionTile framed) {
			framed.setFramedDrawerModelData(modelData);
		} else {
			return false;
		}

		be.requestModelDataUpdate();
		return true;
	}

	@Override
	public ItemStack applyTemplateToItem(ItemStack template, ItemStack target) {
		var item = target.getItem();
		if (item instanceof BlockItem blockItem) {
			var converted = convertToFramed(blockItem.getBlock());
			if(converted != null) {
				if(!target.is(converted.asItem())) {
					target = new ItemStack(converted, target.getCount(), target.getTag());
				}
				target.getOrCreateTag().put("Style", makeModelData(template).serializeNBT());
			}
		}
		return target;
	}

	@Nullable
	private Block convertToFramed(Block input) {
		if (input instanceof FramedDrawerBlock framed) {
			return framed;
		}

		// the framed variants all implement these classes
		if (input instanceof DrawerControllerBlock) {
			return FunctionalStorage.FRAMED_DRAWER_CONTROLLER.getKey().get();
		} else if (input instanceof ControllerExtensionBlock) {
			return FunctionalStorage.FRAMED_CONTROLLER_EXTENSION.getKey().get();
		} else if (input instanceof CompactingDrawerBlock) {
			return FunctionalStorage.FRAMED_COMPACTING_DRAWER.getKey().get();
		} else if (input instanceof SimpleCompactingDrawerBlock) {
			return FunctionalStorage.FRAMED_SIMPLE_COMPACTING_DRAWER.getKey().get();
		} else if (input instanceof DrawerBlock drawer) {
			var id = new ResourceLocation(FunctionalStorage.MOD_ID,
					DrawerWoodType.FRAMED.getName() + "_" + drawer.getType().getSlots());
			return BuiltInRegistries.BLOCK.get(id);
		}

		return null;
	}

	private FramedDrawerModelData makeModelData(ItemStack template) {
		var side = getDecoration(SIDE, template);
		var front = getDecoration(FRONT, template);
		var trim = getDecoration(FRONT_DIVIDER, template);

		Map<String, Item> items = new HashMap<>();
		if (!side.isEmpty()) {
			var sideItem = side.getItem();
			items.put("particle", sideItem);
			items.put("side", sideItem);
			items.put("front_divider", sideItem);
		}
		if (!front.isEmpty()) {
			items.put("front", front.getItem());
		}
		if (!trim.isEmpty()) {
			items.put("trim", trim.getItem());
		}
		return new FramedDrawerModelData(items);
	}

	@Override
	public DrawerSide[] getAllSides() {
		return new DrawerSide[]{SIDE, FRONT, FRONT_DIVIDER};
	}
}
