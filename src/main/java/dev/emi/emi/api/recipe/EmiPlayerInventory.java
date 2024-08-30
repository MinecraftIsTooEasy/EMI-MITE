package dev.emi.emi.api.recipe;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import dev.emi.emi.registry.EmiRecipeFiller;
import dev.emi.emi.registry.EmiStackList;
import dev.emi.emi.runtime.EmiFavorite;
import dev.emi.emi.api.EmiApi;
import dev.emi.emi.api.recipe.handler.EmiCraftContext;
import dev.emi.emi.api.recipe.handler.EmiRecipeHandler;
import dev.emi.emi.api.recipe.handler.StandardRecipeHandler;
import dev.emi.emi.api.stack.Comparison;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.*;
import org.jetbrains.annotations.ApiStatus;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings({"unchecked", "rawtypes"})
public class EmiPlayerInventory {
	private final Comparison none = Comparison.DEFAULT_COMPARISON;
	private final Comparison nbt = Comparison.compareNbt();
	public Map<EmiStack, EmiStack> inventory = Maps.newHashMap();
	
	@Deprecated
	@ApiStatus.Internal
	public EmiPlayerInventory(EntityPlayer entity) {
		GuiContainer screen = EmiApi.getHandledScreen();
		if (screen != null && screen.inventorySlots != null) {
			List<EmiRecipeHandler<?>> handlers = (List) EmiRecipeFiller.getAllHandlers(screen);
			if (!handlers.isEmpty()) {
				if (handlers.get(0) instanceof StandardRecipeHandler standard) {
					List<Slot> slots = standard.getInputSources(screen.inventorySlots);
					for (Slot slot : slots) {
						if (slot.canTakeStack(entity)) {
							addStack(slot.getStack());
						}
					}
					return;
				}
			}
		}
		
		InventoryPlayer pInv = entity.inventory;
		for (int i = 0; i < pInv.mainInventory.length; i++) {
			addStack(pInv.mainInventory[i]);
		}
		if (pInv.getItemStack() != null) {
			addStack(pInv.getItemStack());
		}
	}
	
	public EmiPlayerInventory(List<EmiStack> stacks) {
		for (EmiStack stack : stacks) {
			addStack(stack);
		}
		var ci = Minecraft.getMinecraft().thePlayer.inventory.getItemStack();
		if (ci != null) {
			addStack(ci);
		}
	}
	
	public static EmiPlayerInventory of(EntityPlayer entity) {
		GuiContainer screen = EmiApi.getHandledScreen();
		if (screen != null) {
			List<EmiRecipeHandler<?>> handlers = (List) EmiRecipeFiller.getAllHandlers(screen);
			if (!handlers.isEmpty()) {
				return handlers.get(0).getInventory(screen);
			}
		}
		return new EmiPlayerInventory(entity);
	}
	
	private void addStack(ItemStack is) {
		EmiStack stack = EmiStack.of(is).comparison(c -> none);
		addStack(stack);
	}
	
	private void addStack(EmiStack stack) {
		if (!stack.isEmpty()) {
			if (inventory.containsKey(stack)) {
				for (EmiStack other : inventory.keySet()) {
					if (other.isEqual(stack, nbt)) {
						other.setAmount(other.getAmount() + stack.getAmount());
						break;
					}
				}
			}
			else {
				inventory.put(stack, stack);
			}
		}
	}
	
	public Predicate<EmiRecipe> getPredicate() {
		GuiContainer screen = EmiApi.getHandledScreen();
		List<? extends EmiRecipeHandler<?>> handlers = EmiRecipeFiller.getAllHandlers(screen);
		if (!handlers.isEmpty()) {
			EmiCraftContext context = new EmiCraftContext(screen, this, EmiCraftContext.Type.CRAFTABLE);
			return r -> {
                for (EmiRecipeHandler handler : handlers) {
                    if (handler.supportsRecipe(r)) {
                        return handler.canCraft(r, context);
                    }
                }
				return false;
			};
		}
		return null;
	}
	
	public List<EmiIngredient> getCraftables() {
		Predicate<EmiRecipe> predicate = getPredicate();
		if (predicate == null) {
			return Collections.emptyList();
		}
		Set<EmiRecipe> set = Sets.newHashSet();
		for (EmiStack stack : inventory.keySet()) {
			set.addAll(EmiApi.getRecipeManager().getRecipesByInput(stack));
		}
		return set.stream().filter(r -> !r.hideCraftable() && predicate.test(r) && !r.getOutputs().isEmpty()).map(EmiFavorite.Craftable::new)
				.sorted(Comparator.comparingInt((EmiFavorite.Craftable a) -> EmiStackList.indices.getOrDefault(a.getStack(), Integer.MAX_VALUE)).thenComparingLong(EmiFavorite::getAmount)).collect(Collectors.toList());
	}
	
	public List<Boolean> getCraftAvailability(EmiRecipe recipe) {
		Object2LongMap<EmiStack> used = new Object2LongOpenHashMap<>();
		List<Boolean> states = Lists.newArrayList();
		outer:
		for (EmiIngredient ingredient : recipe.getInputs()) {
			for (EmiStack stack : ingredient.getEmiStacks()) {
				long desired = stack.getAmount();
				if (inventory.containsKey(stack)) {
					EmiStack identity = inventory.get(stack);
					long alreadyUsed = used.getOrDefault(identity, 0);
					long available = identity.getAmount() - alreadyUsed;
					if (available >= desired) {
						used.put(identity, desired + alreadyUsed);
						states.add(true);
						continue outer;
					}
				}
			}
			states.add(false);
		}
		return states;
	}
	
	public boolean canCraft(EmiRecipe recipe) {
		return canCraft(recipe, 1);
	}
	
	public boolean canCraft(EmiRecipe recipe, long amount) {
		Object2LongMap<EmiStack> used = new Object2LongOpenHashMap<>();
		outer:
		for (EmiIngredient ingredient : recipe.getInputs()) {
			if (ingredient.isEmpty()) {
				continue;
			}
			for (EmiStack stack : ingredient.getEmiStacks()) {
				long desired = stack.getAmount() * amount;
				if (inventory.containsKey(stack)) {
					EmiStack identity = inventory.get(stack);
					long alreadyUsed = used.getOrDefault(identity, 0);
					long available = identity.getAmount() - alreadyUsed;
					if (available >= desired) {
						used.put(identity, desired + alreadyUsed);
						continue outer;
					}
				}
			}
			return false;
		}
		return true;
	}
	
	public boolean isEqual(EmiPlayerInventory other) {
		if (other == null) {
			return false;
		}
		Comparison comparison = Comparison.of((a, b) -> {
			return nbt.compare(a, b) && a.getAmount() == b.getAmount();
		});
		if (other.inventory.size() != inventory.size()) {
			return false;
		}
		else {
			for (EmiStack stack : inventory.keySet()) {
				if (!other.inventory.containsKey(stack) || !other.inventory.get(stack).isEqual(stack, comparison)) {
					return false;
				}
			}
		}
		return true;
	}
}