package moddedmite.emi.util;

import net.minecraft.ItemStack;
import net.minecraft.TileEntityFurnace;

public class TileEntityFurnaceEMI extends TileEntityFurnace{
    public static boolean isItemFuel0(ItemStack item_stack) {
        return item_stack.getItem().getHeatLevel(item_stack) > 0;
    }
}