package dev.emi.emi.screen.widget.config;

import dev.emi.emi.EmiPort;
import dev.emi.emi.config.ScreenAlign;
import dev.emi.emi.screen.ConfigScreen;
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import shims.java.net.minecraft.client.gui.widget.ButtonWidget;
import shims.java.net.minecraft.text.Text;

import java.util.List;
import java.util.function.Supplier;

public class ScreenAlignWidget extends ConfigEntryWidget {
	private final ConfigScreen.Mutator<ScreenAlign> mutator;
	private ButtonWidget horizontal, vertical;
	
	public ScreenAlignWidget(Text name, List<TooltipComponent> tooltip, Supplier<String> search, ConfigScreen.Mutator<ScreenAlign> mutator) {
		super(name, tooltip, search, 20);
		this.mutator = mutator;
		
		horizontal = EmiPort.newButton(0, 0, 106, 20, getHorizontalText(), button -> {
			EnumWidget.page(mutator.get().horizontal, v -> true, c -> {
				mutator.get().horizontal = (ScreenAlign.Horizontal) c;
				mutator.set(mutator.get());
			});
		});
		vertical = EmiPort.newButton(0, 0, 106, 20, getVerticalText(), button -> {
			EnumWidget.page(mutator.get().vertical, v -> true, c -> {
				mutator.get().vertical = (ScreenAlign.Vertical) c;
				mutator.set(mutator.get());
			});
		});
		this.setChildren(List.of(horizontal, vertical));
	}
	
	public Text getHorizontalText() {
		return mutator.get().horizontal.getText();
	}
	
	public Text getVerticalText() {
		return mutator.get().vertical.getText();
	}
	
	@Override
	public void update(int y, int x, int width, int height) {
		horizontal.x = x + width - horizontal.getWidth() - vertical.getWidth() - 7;
		horizontal.y = y;
		vertical.x = x + width - vertical.getWidth();
		vertical.y = y;
	}
}