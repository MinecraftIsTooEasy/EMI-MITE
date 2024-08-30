package emi.dev.emi.emi.recipe;

import java.util.List;
import java.util.Random;

import emi.dev.emi.emi.EmiUtil;
import emi.dev.emi.emi.api.recipe.EmiRecipe;
import emi.dev.emi.emi.api.recipe.EmiRecipeCategory;
import emi.dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import emi.dev.emi.emi.api.render.EmiTexture;
import emi.dev.emi.emi.api.stack.EmiIngredient;
import emi.dev.emi.emi.api.stack.EmiStack;
import emi.dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.Item;
import net.minecraft.ResourceLocation;
import net.minecraft.ItemStack;

public class EmiAnvilRecipe implements EmiRecipe {
    private final EmiStack tool;
    private final EmiIngredient resource;
    private final ResourceLocation id;
    private final int uniq = EmiUtil.RANDOM.nextInt();

    public EmiAnvilRecipe(EmiStack tool, EmiIngredient resource, ResourceLocation id) {
        this.tool = tool;
        this.resource = resource;
        this.id = id;
    }

    @Override
    public EmiRecipeCategory getCategory() {
        return VanillaEmiRecipeCategories.ANVIL_REPAIRING;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public List<EmiIngredient> getInputs() {
        return List.of(tool, resource);
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(tool);
    }

    @Override
    public boolean supportsRecipeTree() {
        return false;
    }

    @Override
    public int getDisplayWidth() {
        return 125;
    }

    @Override
    public int getDisplayHeight() {
        return 18;
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(EmiTexture.PLUS, 27, 3);
        widgets.addTexture(EmiTexture.EMPTY_ARROW, 75, 1);
        widgets.addGeneratedSlot(r -> getTool(r, false), uniq, 0, 0);
        widgets.addSlot(resource, 49, 0);
        widgets.addGeneratedSlot(r -> getTool(r, true), uniq, 107, 0).recipeContext(this);
    }

    private EmiStack getTool(Random r, boolean repaired) {
        ItemStack stack = tool.getItemStack().copy();
        ItemStack nugget = tool.getItemStack().getRepairItem().getItemStackForStatsIcon();
        if (stack.getItemSubtype() <= 0) {
            return tool;
        }
        int d = r.nextInt(stack.getItemSubtype());
        if (repaired) {
            d -= stack.getItemSubtype() / 4;
            if (d <= 0) {
                return tool;
            }
        }
        stack.setItemSubtype(d);
        return EmiStack.of(stack);
    }
}

