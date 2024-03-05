package ky.someone.mods.framingtemplates.recipe;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import ky.someone.mods.framingtemplates.item.FramingTemplateItem;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TemplateDecorationRecipe extends CustomRecipe {

	public TemplateDecorationRecipe(ResourceLocation id, CraftingBookCategory category) {
		super(id, category);
	}

	@Override
	public boolean matches(CraftingContainer container, Level level) {
		FramingTemplateItem template = null;
		var templateIdx = -1;
		var hasDecoration = false;

		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);

			if (stack.getItem() instanceof FramingTemplateItem templateItem) {
				if (templateIdx != -1) {
					return false;
				}

				templateIdx = i;
				template = templateItem;
			}
		}

		if (template == null) return false;

		var validIndices = new IntOpenHashSet();
		for (var side : template.getAllSides()) {
			var sideIdx = templateIdx + side.offsetY() * container.getHeight() + side.offsetX() % container.getWidth();

			if (sideIdx < 0 || sideIdx >= container.getContainerSize()) continue;

			validIndices.add(sideIdx);
		}

		for (int i = 0; i < container.getContainerSize(); i++) {
			if (!container.getItem(i).isEmpty()) {
				if (validIndices.contains(i)) {
					hasDecoration = true;
				} else if (i != templateIdx) {
					return false;
				}
			}
		}

		return hasDecoration;
	}

	@Override
	public ItemStack assemble(CraftingContainer container, RegistryAccess reg) {
		FramingTemplateItem template = null;
		var templateIdx = -1;

		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);

			if (stack.getItem() instanceof FramingTemplateItem templateItem) {
				templateIdx = i;
				template = templateItem;
				break;
			}
		}

		// sanity check: template != null
		if (template == null) return ItemStack.EMPTY;

		var stack = template.getDefaultInstance().copy();
		for (var side : template.getAllSides()) {
			var sideIdx = templateIdx + side.offsetY() * container.getHeight() + side.offsetX() % container.getWidth();

			if (sideIdx < 0 || sideIdx >= container.getContainerSize()) continue;

			var decoration = container.getItem(sideIdx);
			if (!decoration.isEmpty()) {
				template.setDecoration(side, stack, decoration.copyWithCount(1));
			}
		}

		return stack;
	}

	@Override
	public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
		// use up the template, keep everything else as is
		var stacks = NonNullList.withSize(container.getContainerSize(), ItemStack.EMPTY);

		var templateUsed = false;

		for (int i = 0; i < container.getContainerSize(); i++) {
			ItemStack stack = container.getItem(i);

			if (!templateUsed && stack.getItem() instanceof FramingTemplateItem templateItem) {
				templateUsed = true;
			} else {
				stacks.set(i, stack.copyWithCount(1));
			}
		}

		return stacks;
	}

	@Override
	public boolean canCraftInDimensions(int i, int j) {
		return i >= 2 && j >= 2;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return FramingRecipes.TEMPLATE_DECORATION.get();
	}
}
