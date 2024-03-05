package ky.someone.mods.framingtemplates.data;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.FramedDrawerBlock;
import com.google.gson.JsonObject;
import com.jaquadro.minecraft.storagedrawers.core.ModItems;
import eutros.framedcompactdrawers.recipe.ModTags;
import ky.someone.mods.framingtemplates.item.FramingItems;
import ky.someone.mods.framingtemplates.item.FramingTemplateItem;
import ky.someone.mods.framingtemplates.recipe.FramingRecipes;
import ky.someone.mods.framingtemplates.util.FramingUtil;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FramingRecipesProvider extends RecipeProvider {
	public FramingRecipesProvider(PackOutput output) {
		super(output);
	}

	@Override
	protected void buildRecipes(Consumer<FinishedRecipe> consumer) {
		// in the context of datagen, all dependencies are loaded
		assert FramingItems.STORAGE_DRAWERS_TEMPLATE != null;
		assert FramingItems.FUNCTIONAL_STORAGE_TEMPLATE != null;

		var sdTemplate = FramingItems.STORAGE_DRAWERS_TEMPLATE.get();
		var fsTemplate = FramingItems.FUNCTIONAL_STORAGE_TEMPLATE.get();

		// template copying
		consumer.accept(new FinishedCopyRecipe(sdTemplate, Ingredient.of(ModItems.UPGRADE_TEMPLATE.get()), Ingredient.of(Tags.Items.RODS_WOODEN)));
		consumer.accept(new FinishedCopyRecipe(fsTemplate, Ingredient.of(Items.CHEST), Ingredient.of(Items.PAPER)));

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, sdTemplate, 3)
				.pattern("SCS")
				.pattern("S#S")
				.pattern("SCS")
				.define('#', sdFramed())
				.define('S', Tags.Items.RODS_WOODEN)
				.define('C', Tags.Items.INGOTS_COPPER)
				.unlockedBy("has_copper", has(Items.COPPER_INGOT))
				.group("framing_templates")
				.save(consumer);

		ShapedRecipeBuilder.shaped(RecipeCategory.MISC, fsTemplate, 3)
				.pattern("IPI")
				.pattern("P#P")
				.pattern("IPI")
				.define('#', fsFramed())
				.define('P', Items.PAPER)
				.define('I', Tags.Items.NUGGETS_IRON)
				.unlockedBy("has_iron", has(Items.IRON_INGOT))
				.group("framing_templates")
				.save(consumer);

		// special recipes
		consumer.accept(new FinishedSpecialRecipe(FramingRecipes.TEMPLATE_DECORATION.get(), FramingUtil.id("template_decoration")));
	}

	private static Ingredient sdFramed() {
		return Ingredient.fromValues(Stream.of(ModTags.Items.FRAME_DOUBLE, ModTags.Items.FRAME_TRIPLE).map(Ingredient.TagValue::new));
	}

	private static Ingredient fsFramed() {
		return Ingredient.fromValues(
				FunctionalStorage.DRAWER_TYPES.values()
						.stream()
						.flatMap(List::stream)
						.map(Pair::getLeft)
						.flatMap(RegistryObject::stream)
						.filter(block -> block instanceof FramedDrawerBlock)
						.map(ItemLike::asItem)
						.map(Item::getDefaultInstance)
						.map(Ingredient.ItemValue::new)
		);
	}

	public static class FinishedCopyRecipe implements FinishedRecipe {
		private final FramingTemplateItem template;
		private final Ingredient center;
		private final Ingredient surrounding;
		private final ResourceLocation templateId;

		public FinishedCopyRecipe(FramingTemplateItem template, Ingredient center, Ingredient surrounding) {
			this.template = template;
			this.center = center;
			this.surrounding = surrounding;
			this.templateId = BuiltInRegistries.ITEM.getKey(template);
		}


		public void serializeRecipeData(JsonObject json) {
			json.addProperty("template", templateId.toString());
			json.add("center", center.toJson());
			json.add("surrounding", surrounding.toJson());
		}

		@Override
		public ResourceLocation getId() {
			return FramingUtil.id("copy/" + templateId.getPath());
		}

		@Override
		public RecipeSerializer<?> getType() {
			return FramingRecipes.TEMPLATE_COPY.get();
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}

	public record FinishedSpecialRecipe(RecipeSerializer<? extends CustomRecipe> serializer, ResourceLocation id) implements FinishedRecipe {
		@Override
		public void serializeRecipeData(JsonObject json) {
		}

		@Override
		public ResourceLocation getId() {
			return id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return serializer;
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}
