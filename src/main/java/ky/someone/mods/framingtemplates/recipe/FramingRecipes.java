package ky.someone.mods.framingtemplates.recipe;

import ky.someone.mods.framingtemplates.util.FramingUtil;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public interface FramingRecipes {
	DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister.create(Registries.RECIPE_SERIALIZER, FramingUtil.MOD_ID);

	RegistryObject<RecipeSerializer<TemplateCopyRecipe>> TEMPLATE_COPY = REGISTRY.register("template_copy", TemplateCopyRecipe.Serializer::new);

	RegistryObject<RecipeSerializer<TemplateDecorationRecipe>> TEMPLATE_DECORATION = REGISTRY.register("template_decoration", () -> new SimpleCraftingRecipeSerializer<>(TemplateDecorationRecipe::new));
}
