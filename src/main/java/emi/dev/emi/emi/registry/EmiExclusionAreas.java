package emi.dev.emi.emi.registry;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import emi.dev.emi.emi.api.EmiExclusionArea;
import emi.dev.emi.emi.runtime.EmiLog;
import emi.dev.emi.emi.screen.Bounds;
import emi.dev.emi.emi.screen.EmiScreen;
import emi.dev.emi.emi.screen.EmiScreenManager;
import emi.shims.java.com.unascribed.retroemi.RetroEMI;
import net.minecraft.GuiScreen;

import java.util.List;
import java.util.Map;

public class EmiExclusionAreas {
	public static Map<Class<?>, List<EmiExclusionArea<?>>> fromClass = Maps.newHashMap();
	public static List<EmiExclusionArea<?>> generic = Lists.newArrayList();

	public static void clear() {
		fromClass.clear();
		generic.clear();
	}
	
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static List<Bounds> getExclusion(GuiScreen screen) {
		List<Bounds> list = Lists.newArrayList();
		if (RetroEMI.emify(screen) instanceof EmiScreen emi) {
			int left = emi.emi$getLeft();
			int right = emi.emi$getRight();
			int top = emi.emi$getTop();
			int bottom = emi.emi$getBottom();
			list.add(new Bounds(left, top, right - left, bottom - top));
			// EMI buttons
			list.add(new Bounds(0, screen.height - 22, left, 22));
			// Search bar
			list.add(new Bounds(EmiScreenManager.search.x - 1, EmiScreenManager.search.y - 1, EmiScreenManager.search.getWidth() + 2, EmiScreenManager.search.getHeight() + 2));
		}
		try {
			if (fromClass.containsKey(screen.getClass())) {
				for (EmiExclusionArea exclusion : fromClass.get(screen.getClass())) {
					exclusion.addExclusionArea(screen, rect -> {
						list.add((Bounds) rect);
					});
				}
			}
			for (EmiExclusionArea exclusion : generic) {
				exclusion.addExclusionArea(screen, rect -> {
					list.add((Bounds) rect);
				});
			}
		} catch (Exception e) {
			EmiLog.error("Exception thrown when adding exclusion areas");
			e.printStackTrace();
		}
		return list;
	}
}
