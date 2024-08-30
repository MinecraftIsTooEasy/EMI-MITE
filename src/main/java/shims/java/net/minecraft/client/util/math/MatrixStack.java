package shims.java.net.minecraft.client.util.math;

import static org.lwjgl.opengl.GL11.*;

public class MatrixStack {

	public static final MatrixStack INSTANCE = new MatrixStack();
	
	public MatrixStack() {}

	public void push() {
		glPushMatrix();
	}
	
	public void pop() {
		glPopMatrix();
	}
	
	public void translate(double x, double y, double z) {
		glTranslated(x, y, z);
	}
	
	public void scale(double x, double y, double z) {
		glScaled(x, y, z);
	}
	
	public void multiply(Runnable r) {
		r.run();
	}
	
}