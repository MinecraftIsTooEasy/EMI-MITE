package dev.emi.emi.screen.widget.config;

import dev.emi.emi.EmiPort;
import dev.emi.emi.config.ConfigEnum;
import dev.emi.emi.screen.ConfigEnumScreen;
import dev.emi.emi.screen.ConfigScreen;
import dev.emi.emi.screen.ConfigScreen.Mutator;
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import shims.java.net.minecraft.client.gui.widget.ButtonWidget;
import shims.java.net.minecraft.text.Text;
import net.minecraft.Minecraft;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class EnumWidget extends ConfigEntryWidget {
	private final Mutator<ConfigEnum> mutator;
	private ButtonWidget button;
	
	public EnumWidget(Text name, List<TooltipComponent> tooltip, Supplier<String> search, Mutator<ConfigEnum> mutator, Predicate<ConfigEnum> filter) {
		super(name, tooltip, search, 20);
		this.mutator = mutator;
		
		button = EmiPort.newButton(0, 0, 150, 20, getText(), button -> {
			page(mutator.get(), filter, mutator::set);
		});
		this.setChildren(List.of(button));
	}
	
	public static void page(ConfigEnum original, Predicate<ConfigEnum> filter, Consumer<ConfigEnum> consumer) {
		Enum<?> e = (Enum<?>) original;
		Enum<?>[] values = e.getClass().getEnumConstants();
		Minecraft client = Minecraft.getMinecraft();
		if (client.currentScreen instanceof ConfigScreen cs) {
			client.displayGuiScreen(new ConfigEnumScreen<ConfigEnum>(cs, Stream.of(values).filter(f -> filter.test((ConfigEnum) f)).map(v -> {
				ConfigEnum en = (ConfigEnum) v;
				return new ConfigEnumScreen.Entry<ConfigEnum>(en, en.getText(), List.of());
			}).collect(Collectors.toList()), consumer));
		}
	}
	
	public Text getText() {
		return mutator.get().getText();
	}
	
	@Override
	public void update(int y, int x, int width, int height) {
		button.x = x + width - button.getWidth();
		button.y = y;
	}
}
