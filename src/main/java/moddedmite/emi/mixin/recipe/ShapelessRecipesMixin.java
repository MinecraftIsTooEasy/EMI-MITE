package moddedmite.emi.mixin.recipe;

import moddedmite.emi.api.EMIShapelessRecipes;
import net.minecraft.IInventory;
import net.minecraft.ItemStack;
import net.minecraft.ShapelessRecipes;
import org.spongepowered.asm.mixin.*;

import java.util.List;

@Mixin(ShapelessRecipes.class)
public class ShapelessRecipesMixin implements EMIShapelessRecipes {
    @Mutable @Final @Shadow private final List recipeItems;
    @Unique private ItemStack[] recipeSecondaryOutputs;

    public ShapelessRecipesMixin(List recipeItems) {
        this.recipeItems = recipeItems;
    }

    @Override
    public List getRecipeItems() {
        return recipeItems;
    }

    @Override
    public ItemStack[] getSecondaryOutput(IInventory inventory) {
        return this.recipeSecondaryOutputs;
    }
}
