package dev.emi.emi.screen.widget.config;

import dev.emi.emi.EmiPort;
import shims.java.com.unascribed.retroemi.RetroEMI;
import shims.java.net.minecraft.client.gui.widget.TextFieldWidget;
import shims.java.net.minecraft.text.Text;
import net.minecraft.FontRenderer;
import net.minecraft.Minecraft;

public class ConfigSearch {
	public final ConfigSearchWidgetField field;
	
	public ConfigSearch(int x, int y, int width, int height) {
		Minecraft client = Minecraft.getMinecraft();
		
		field = new ConfigSearchWidgetField(client.fontRenderer, x, y, width, height, EmiPort.literal(""));
		field.setChangedListener(s -> {
			if (s.length() > 0) {
				field.setSuggestion("");
			}
			else {
				field.setSuggestion(RetroEMI.translate("emi.search_config"));
			}
		});
		field.setSuggestion(RetroEMI.translate("emi.search_config"));
	}
	
	public void setText(String query) {
		field.setText(query);
	}
	
	public String getSearch() {
		return field.getText();
	}
	
	public class ConfigSearchWidgetField extends TextFieldWidget {
		
		public ConfigSearchWidgetField(FontRenderer fontRenderer, int x, int y, int width, int height, Text text) {
			super(fontRenderer, x, y, width, height, text);
		}
		
		@Override
		public boolean mouseClicked(double mouseX, double mouseY, int button) {
			if (button == 1 && isMouseOver(mouseX, mouseY)) {
				this.setText("");
				EmiPort.focus(this, true);
				return true;
			}
			return super.mouseClicked(mouseX, mouseY, button);
		}
	}
}
