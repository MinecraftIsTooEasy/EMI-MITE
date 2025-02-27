package moddedmite.emi.mixin.crafting;

import moddedmite.emi.api.EMICraftingManager;
import net.minecraft.CraftingManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(CraftingManager.class)
public class CraftingManagerMixin implements EMICraftingManager {
    @Shadow private List recipes;

    @Override
    public List getRecipes() {
        return recipes;
    }
}
