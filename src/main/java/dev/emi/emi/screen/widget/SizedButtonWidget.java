package dev.emi.emi.screen.widget;

import dev.emi.emi.EmiPort;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.runtime.EmiDrawContext;
import org.lwjgl.opengl.GL11;
import shims.java.net.minecraft.client.gui.tooltip.TooltipComponent;
import shims.java.net.minecraft.client.gui.widget.ButtonWidget;
import shims.java.net.minecraft.client.util.math.MatrixStack;
import shims.java.net.minecraft.text.Text;
import net.minecraft.Minecraft;
import net.minecraft.ResourceLocation;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;

public class SizedButtonWidget extends ButtonWidget {
	private final BooleanSupplier isActive;
	private final IntSupplier vOffset;
	protected ResourceLocation texture = EmiRenderHelper.BUTTONS;
	protected Supplier<List<Text>> text;
	protected int u, v;
	
	public SizedButtonWidget(int x, int y, int width, int height, int u, int v, BooleanSupplier isActive, PressAction action) {
		this(x, y, width, height, u, v, isActive, action, () -> 0);
	}
	
	public SizedButtonWidget(int x, int y, int width, int height, int u, int v, BooleanSupplier isActive, PressAction action, List<Text> text) {
		this(x, y, width, height, u, v, isActive, action, () -> 0, () -> text);
	}
	
	public SizedButtonWidget(int x, int y, int width, int height, int u, int v, BooleanSupplier isActive, PressAction action, IntSupplier vOffset) {
		this(x, y, width, height, u, v, isActive, action, vOffset, null);
	}
	
	public SizedButtonWidget(int x, int y, int width, int height, int u, int v, BooleanSupplier isActive, PressAction action, IntSupplier vOffset,
			Supplier<List<Text>> text) {
		super(x, y, width, height, EmiPort.literal(""), action, s -> s.get());
		this.u = u;
		this.v = v;
		this.isActive = isActive;
		this.vOffset = vOffset;
		this.text = text;
	}
	
	protected int getU(int mouseX, int mouseY) {
		return this.u;
	}
	
	protected int getV(int mouseX, int mouseY) {
		int v = this.v + vOffset.getAsInt();
		this.active = this.isActive.getAsBoolean();
		if (!this.active) {
			v += this.height * 2;
		}
		else if (this.isMouseOver(mouseX, mouseY)) {
			v += this.height;
		}
		return v;
	}
	
	@Override
	public void renderButton(MatrixStack matrices, int mouseX, int mouseY, float delta) {
		EmiDrawContext context = EmiDrawContext.instance();
		glEnable(GL_DEPTH_TEST);
		context.drawTexture(texture, this.x, this.y, getU(mouseX, mouseY), getV(mouseX, mouseY), this.width, this.height);
		if (this.isMouseOver(mouseX, mouseY) && text != null && this.active) {
			context.push();
			GL11.glColor4f(1F, 1F, 1F, 1F);
			glDisable(GL_DEPTH_TEST);
			Minecraft client = Minecraft.getMinecraft();
			EmiRenderHelper.drawTooltip(client.currentScreen, context,
					text.get().stream().map(EmiPort::ordered).map(TooltipComponent::of).collect(Collectors.toList()), mouseX, mouseY);
			context.pop();
		}
	}
}
