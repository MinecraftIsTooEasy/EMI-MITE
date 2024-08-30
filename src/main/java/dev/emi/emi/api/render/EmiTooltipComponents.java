package dev.emi.emi.api.render;

import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.screen.tooltip.IngredientTooltipComponent;
import dev.emi.emi.screen.tooltip.RecipeCostTooltipComponent;
import dev.emi.emi.screen.tooltip.RecipeTooltipComponent;
import dev.emi.emi.screen.tooltip.RemainderTooltipComponent;
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import shims.java.net.minecraft.text.Text;
import shims.java.net.minecraft.util.Formatting;

import java.util.List;

public class EmiTooltipComponents {
	
	/**
	 * @return A tooltip component that displays a provided recipe.
	 */
	public static TooltipComponent getRecipeTooltipComponent(EmiRecipe recipe) {
		return new RecipeTooltipComponent(recipe);
	}
	
	/**
	 * @return A tooltip component that displays the remainder of a provided ingredient.
	 */
	public static TooltipComponent getRemainderTooltipComponent(EmiIngredient ingredient) {
		return new RemainderTooltipComponent(ingredient);
	}
	
	/**
	 * @return A tooltip component that displays the the cost breakdown of a provided recipe.
	 */
	public static TooltipComponent getRecipeCostTooltipComponent(EmiRecipe recipe) {
		return new RecipeCostTooltipComponent(recipe);
	}
	
	/**
	 * @return A tooltip component that displays a collection of stacks to represent an ingredient
	 */
	public static TooltipComponent getIngredientTooltipComponent(List<? extends EmiIngredient> stacks) {
		return new IngredientTooltipComponent(stacks);
	}
	
	/**
	 * @return A tooltip component that displays the amount of a provided stack.
	 */
	public static TooltipComponent getAmount(EmiIngredient ingredient) {
		return of(EmiRenderHelper.getAmountText(ingredient, ingredient.getAmount()).copy().formatted(Formatting.GRAY));
	}
	
	/**
	 * A shorthand to create a tooltip component from text
	 */
	public static TooltipComponent of(Text text) {
		return TooltipComponent.of(text.asOrderedText());
	}
}