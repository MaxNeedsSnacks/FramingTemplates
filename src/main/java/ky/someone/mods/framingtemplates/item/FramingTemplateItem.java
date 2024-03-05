package ky.someone.mods.framingtemplates.item;

import ky.someone.mods.framingtemplates.util.DrawerSide;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.ModList;

import java.util.List;

import static ky.someone.mods.framingtemplates.util.FramingUtil.*;

public abstract class FramingTemplateItem extends Item {

	public static final String DECORATIONS_KEY = "decorations";

	private static final Component NO_DECO = Component.literal("✘").withStyle(TOOLTIP_RED);

	protected final String mod;

	public FramingTemplateItem(String modid) {
		super(new Item.Properties().stacksTo(16));
		this.mod = modid;
	}

	public abstract boolean applyTemplateInWorld(ItemStack stack, Level level, BlockPos pos, BlockState blockState);

	public abstract boolean applyTemplateToItem(ItemStack template, ItemStack target);

	public ItemStack getDecoration(DrawerSide side, ItemStack stack) {
		var decorations = stack.getTagElement(DECORATIONS_KEY);

		if (decorations == null || !decorations.contains(side.key())) {
			return ItemStack.EMPTY;
		}

		return ItemStack.of(decorations.getCompound(side.key()));
	}

	public ItemStack setDecoration(DrawerSide side, ItemStack stack, ItemStack decoration) {
		var decorations = stack.getOrCreateTagElement(DECORATIONS_KEY);

		if (!decoration.isEmpty()) {
			var compound = decoration.save(new CompoundTag());
			decorations.put(side.key(), compound);
		} else {
			decorations.remove(side.key());
		}

		if (decorations.isEmpty()) {
			stack.removeTagKey(DECORATIONS_KEY);
		}

		return stack;
	}

	public abstract DrawerSide[] getAllSides();

	@Override
	public InteractionResult useOn(UseOnContext ctx) {
		var stack = ctx.getItemInHand();
		var level = ctx.getLevel();
		var pos = ctx.getClickedPos();
		var result = applyTemplateInWorld(stack, level, pos, level.getBlockState(pos));

		if (result) {
			if (!level.isClientSide) stack.shrink(1);
			return InteractionResult.sidedSuccess(level.isClientSide);
		}

		return InteractionResult.PASS;
	}

	@Override
	public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
		var isShift = Screen.hasShiftDown();

		tooltip.add(Component.translatable("tooltip.framing_templates.applied_decorations")
				.append(Component.literal(" (Shift)").withStyle(isShift ? ChatFormatting.DARK_GRAY : ChatFormatting.GRAY))
				.withStyle(TOOLTIP_BLUE));

		if (isShift) {
			for (var side : getAllSides()) {
				var component = Component.literal(" " + side.symbol() + " ")
						.append(Component.translatable("tooltip.framing_templates.sides.%s".formatted(side.key())).withStyle(TOOLTIP_EXTRA))
						.append(": ").withStyle(TOOLTIP_FLAVOUR);

				var item = getDecoration(side, stack);
				if (!item.isEmpty()) {
					component.append(item.getHoverName().copy().withStyle(TOOLTIP_MAIN));
				} else {
					component.append(NO_DECO);
				}

				tooltip.add(component);
			}
		}

		// safety: we know the mod is loaded
		var mod = ModList.get().getModContainerById(this.mod).orElseThrow().getModInfo().getDisplayName();
		tooltip.add(Component.translatable("tooltip.framing_templates.applicable_to",
				Component.literal(mod).withStyle(TOOLTIP_MAIN)).withStyle(TOOLTIP_GREEN));
	}
}
