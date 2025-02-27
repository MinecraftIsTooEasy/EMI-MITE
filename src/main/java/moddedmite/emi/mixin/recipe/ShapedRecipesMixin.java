package moddedmite.emi.mixin.recipe;

import moddedmite.emi.api.EMIShapedRecipes;
import net.minecraft.IInventory;
import net.minecraft.ItemStack;
import net.minecraft.ShapedRecipes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ShapedRecipes.class)
public class ShapedRecipesMixin implements EMIShapedRecipes {
    @Shadow private int recipeWidth;
    @Shadow private int recipeHeight;
    @Shadow private ItemStack[] recipeItems;
    @Unique private ItemStack[] recipeSecondaryOutputs;

    @Override
    public int getRecipeHeight() {
        return recipeHeight;
    }

    @Override
    public int getRecipeWidth() {
        return recipeWidth;
    }

    @Override
    public ItemStack[] getRecipeItems() {
        return recipeItems;
    }

    @Override
    public ItemStack[] getSecondaryOutput(IInventory inventory) {
        return this.recipeSecondaryOutputs;
    }
}
