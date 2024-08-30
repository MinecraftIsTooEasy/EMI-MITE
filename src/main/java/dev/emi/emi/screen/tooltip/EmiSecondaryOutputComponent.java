package dev.emi.emi.screen.tooltip;

import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import shims.java.net.minecraft.text.Text;
import net.minecraft.FontRenderer;
import net.minecraft.ItemStack;
import net.minecraft.Minecraft;

import java.util.Arrays;
import java.util.List;

public class EmiSecondaryOutputComponent implements EmiTooltipComponent {
	private final List<? extends EmiIngredient> secondaryOutputs;
	
	public EmiSecondaryOutputComponent(ItemStack[] secondaryOutputs) {
		this.secondaryOutputs = Arrays.stream(secondaryOutputs).map(EmiStack::of).toList();
	}
	
	@Override
	public int getHeight() {
		return 27;
	}
	
	@Override
	public int getWidth(FontRenderer textRenderer) {
		int charWidth = Minecraft.getMinecraft().fontRenderer.getStringWidth(Text.translatable("tooltip.emi.recipe_remainder").toString());
		return Math.max(charWidth, secondaryOutputs.size() * 18);
	}
	
	@Override
	public void drawTooltip(EmiDrawContext context, TooltipRenderData tooltip) {
		context.drawTextWithShadow(Text.translatable("tooltip.emi.recipe_remainder"), 0, 0, 0xffffff);
		for (int i = 0; i < secondaryOutputs.size(); i++) {
			context.drawStack(secondaryOutputs.get(i), (18 * i), 9);
		}
	}
}