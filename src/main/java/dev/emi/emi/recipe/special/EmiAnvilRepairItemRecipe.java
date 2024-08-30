package dev.emi.emi.recipe.special;

import com.google.common.collect.Lists;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.recipe.VanillaEmiRecipeCategories;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;
import net.minecraft.Item;
import net.minecraft.ItemStack;
import net.minecraft.ResourceLocation;

import java.util.List;
import java.util.Random;

public class EmiAnvilRepairItemRecipe implements EmiRecipe {
    private final Item tool;
    private final ResourceLocation id;
    private final int uniq = EmiUtil.RANDOM.nextInt();

    public EmiAnvilRepairItemRecipe(Item tool, ResourceLocation id) {
        this.tool = tool;
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
        return List.of(EmiStack.of(tool), EmiStack.of(tool));
    }

    @Override
    public List<EmiStack> getOutputs() {
        return List.of(EmiStack.of(tool));
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
        int notUniq= uniq;
        widgets.addGeneratedSlot(r -> getItem(r, 0), notUniq, 0, 0);
        widgets.addGeneratedSlot(r -> getItem(r, 1), notUniq, 49, 0);
        widgets.addGeneratedSlot(r -> getItem(r, 2), notUniq, 107, 0).recipeContext(this);
    }

    protected EmiStack getItem(Random random, int item) {
        List<ItemStack> items = Lists.newArrayList();
        items.add(getTool(random));
        items.add(getTool(random));
        items.add(getMergeItems(items));

        return EmiStack.of(items.get(item));
    }

    private ItemStack getMergeItems(List<ItemStack> items) {
        ItemStack item = new ItemStack(tool);
        int maxDamage = tool.getNumSubtypes();
        int damage = items.get(0).getItemSubtype() - (21 * maxDamage)/20 + items.get(1).getItemSubtype();
        if (damage > 0) {
            item.setItemSubtype(damage);
        }
        return item;
    }

    private ItemStack getTool(Random r) {
        ItemStack stack = new ItemStack(tool);
        if (stack.getItemSubtype() <= 0) {
            return stack;
        }
        int d = r.nextInt(stack.getItemSubtype());
        stack.setItemSubtype(d);
        return stack;
    }
}
