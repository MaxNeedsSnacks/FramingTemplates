package ky.someone.mods.framingtemplates.recipe;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import ky.someone.mods.framingtemplates.item.FramingTemplateItem;
import ky.someone.mods.framingtemplates.util.FramingUtil;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

public class TemplateCopyRecipe extends ShapedRecipe {

	private final Item template;
	private final Ingredient center;
	private final Ingredient surrounding;
	private final int count;

	public TemplateCopyRecipe(ResourceLocation id, Item template, Ingredient top, Ingredient surrounding, int count) {
		super(id, "", CraftingBookCategory.BUILDING,
				3, 3,
				NonNullList.of(
						Ingredient.EMPTY,
						surrounding,
						Ingredient.of(template),
						surrounding,
						surrounding,
						top,
						surrounding,
						surrounding,
						surrounding,
						surrounding
				),
				withCopyHint(template, count));

		this.template = template;
		this.center = top;
		this.surrounding = surrounding;
		this.count = count;
	}

	private static ItemStack withCopyHint(Item item, int count) {
		var stack = item.getDefaultInstance().copyWithCount(count);

		var hint = Component.translatable("tooltip.framing_templates.makes_copy").withStyle(FramingUtil.TOOLTIP_EXTRA);
		var lore = new ListTag();
		lore.add(StringTag.valueOf(Component.Serializer.toJson(hint)));

		stack.getOrCreateTagElement("display").put("Lore", lore);
		return stack;
	}

	@Override
	public boolean matches(CraftingContainer container, Level level) {
		return super.matches(container, level)
				&& container.getItem(1).getTagElement(FramingTemplateItem.DECORATIONS_KEY) != null;
	}

	@Override
	public ItemStack assemble(CraftingContainer container, RegistryAccess reg) {
		return container.getItem(1).copyWithCount(count);
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return FramingRecipes.TEMPLATE_COPY.get();
	}

	public static class Serializer implements RecipeSerializer<TemplateCopyRecipe> {
		@Override
		public TemplateCopyRecipe fromJson(ResourceLocation id, JsonObject json) {
			var templateId = json.getAsJsonPrimitive("template").getAsString();
			var item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(templateId));
			if (!(item instanceof FramingTemplateItem template)) {
				throw new JsonParseException("Invalid template item %s!".formatted(templateId));
			}

			var surrounding = Ingredient.fromJson(json.get("surrounding"));
			var center = json.has("center") ? Ingredient.fromJson(json.get("center")) : surrounding;
			var count = GsonHelper.getAsInt(json, "count", 2);

			return new TemplateCopyRecipe(id, template, center, surrounding, count);
		}

		@Override
		public TemplateCopyRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buf) {
			var item = buf.readRegistryIdUnsafe(ForgeRegistries.ITEMS);
			if (!(item instanceof FramingTemplateItem template)) {
				throw new JsonParseException("Invalid template item!");
			}

			var surrounding = Ingredient.fromNetwork(buf);
			var center = Ingredient.fromNetwork(buf);
			var count = buf.readVarInt();

			return new TemplateCopyRecipe(id, template, center, surrounding, count);
		}

		@Override
		public void toNetwork(FriendlyByteBuf buf, TemplateCopyRecipe recipe) {
			buf.writeRegistryIdUnsafe(ForgeRegistries.ITEMS, recipe.template);
			recipe.surrounding.toNetwork(buf);
			recipe.center.toNetwork(buf);
			buf.writeVarInt(recipe.count);
		}
	}
}
