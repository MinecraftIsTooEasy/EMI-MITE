package dev.emi.emi.api.recipe;

import com.google.common.collect.Lists;
import dev.emi.emi.registry.EmiStackList;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import moddedmite.emi.api.EMIResourceLocation;
import net.minecraft.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.Comparator;
import java.util.List;

@ApiStatus.Experimental
public class EmiRecipeSorting {
	private static final Comparator<EmiRecipe> NONE = (a, b) -> 0;
	
	public static Comparator<EmiRecipe> none() {
		return NONE;
	}
	
	public static Comparator<EmiRecipe> identifier() {
		return (ar, br) -> {
			ResourceLocation a = ar.getId();
			ResourceLocation b = br.getId();
			if (a == null) {
				if (b == null) {
					return 0;
				}
				else {
					return 1;
				}
			}
			else if (b == null) {
				return -1;
			}
			return ((EMIResourceLocation) a).compareTo(b);
		};
	}
	
	public static Comparator<EmiRecipe> compareOutputThenInput() {
		return (a, b) -> {
			int comp = compareStacks(a.getOutputs(), b.getOutputs());
			if (comp != 0) {
				return comp;
			}
			comp = compareIngredients(a.getInputs(), b.getInputs());
			if (comp != 0) {
				return comp;
			}
			return 0;
		};
	}
	
	public static Comparator<EmiRecipe> compareInputThenOutput() {
		return (a, b) -> {
			int comp = compareIngredients(a.getInputs(), b.getInputs());
			if (comp != 0) {
				return comp;
			}
			comp = compareStacks(a.getOutputs(), b.getOutputs());
			if (comp != 0) {
				return comp;
			}
			return 0;
		};
	}
	
	private static int compareStacks(List<EmiStack> ao, List<EmiStack> bo) {
		ao = filterEmpty(ao);
		bo = filterEmpty(bo);
		if (ao.isEmpty() || bo.isEmpty()) {
			return Integer.compare(ao.size(), bo.size());
		}
		int min = Math.min(ao.size(), bo.size());
		for (int i = 0; i < min; i++) {
			int ai = Integer.compare(getIndex(ao.get(i)), getIndex(bo.get(i)));
			if (ai != 0) {
				return ai;
			}
		}
		return Integer.compare(ao.size(), bo.size());
	}
	
	private static int compareIngredients(List<EmiIngredient> ao, List<EmiIngredient> bo) {
		ao = filterEmpty(ao);
		bo = filterEmpty(bo);
		if (ao.isEmpty() || bo.isEmpty()) {
			return Integer.compare(ao.size(), bo.size());
		}
		int min = Math.min(ao.size(), bo.size());
		for (int i = 0; i < min; i++) {
			int ai = Integer.compare(getIndex(ao.get(i).getEmiStacks().get(0)), getIndex(bo.get(i).getEmiStacks().get(0)));
			if (ai != 0) {
				return ai;
			}
		}
		return Integer.compare(ao.size(), bo.size());
	}
	
	private static int getIndex(EmiStack stack) {
		return EmiStackList.indices.getOrDefault(stack, Integer.MAX_VALUE);
	}
	
	private static <T extends EmiIngredient> List<T> filterEmpty(List<T> list) {
		List<T> stacks = Lists.newArrayList();
		for (int i = 0; i < list.size(); i++) {
			T stack = list.get(i);
			if (!stack.isEmpty()) {
				stacks.add(stack);
			}
		}
		return stacks;
	}
}
