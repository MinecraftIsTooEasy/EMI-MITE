package dev.emi.emi.api.plugin;

import com.google.common.collect.Sets;
import dev.emi.emi.EmiRenderHelper;
import dev.emi.emi.EmiUtil;
import dev.emi.emi.Prototype;
import dev.emi.emi.config.EffectLocation;
import dev.emi.emi.config.EmiConfig;
import dev.emi.emi.handler.CookingRecipeHandler;
import dev.emi.emi.handler.CraftingRecipeHandler;
import dev.emi.emi.handler.InventoryRecipeHandler;
import dev.emi.emi.platform.EmiAgnos;
import dev.emi.emi.recipe.*;
import dev.emi.emi.recipe.special.*;
import dev.emi.emi.registry.EmiStackList;
import dev.emi.emi.registry.EmiTags;
import dev.emi.emi.runtime.EmiDrawContext;
import dev.emi.emi.runtime.EmiReloadLog;
import dev.emi.emi.screen.Bounds;
import dev.emi.emi.stack.serializer.ItemEmiStackSerializer;
import dev.emi.emi.stack.serializer.TagEmiIngredientSerializer;
import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.*;
import dev.emi.emi.api.render.EmiRenderable;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.*;
import moddedmite.emi.api.EMIGuiContainerCreative;
import moddedmite.emi.api.EMIShapelessRecipes;
import shims.java.com.unascribed.retroemi.PredicateAsSet;
import shims.java.com.unascribed.retroemi.RetroEMI;
import shims.java.net.minecraft.tag.TagKey;
import shims.java.net.minecraft.util.SyntheticIdentifier;
import net.minecraft.*;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static dev.emi.emi.api.recipe.VanillaEmiRecipeCategories.*;

@EmiEntrypoint
public class VanillaPlugin implements EmiPlugin {
	public static EmiRecipeCategory TAG =
			new EmiRecipeCategory(new ResourceLocation("emi:tag"), EmiStack.of(Item.itemsList[Block.oreIron.blockID]), simplifiedRenderer(240, 208),
					EmiRecipeSorting.none());
	
	public static EmiRecipeCategory INGREDIENT =
			new EmiRecipeCategory(new ResourceLocation("emi:ingredient"), EmiStack.of(Item.compass), simplifiedRenderer(240, 208));
	public static EmiRecipeCategory RESOLUTION =
			new EmiRecipeCategory(new ResourceLocation("emi:resolution"), EmiStack.of(Item.compass), simplifiedRenderer(240, 208));
	
	static {
		CRAFTING = new EmiRecipeCategory(new ResourceLocation("minecraft:crafting"), EmiStack.of(Block.workbench), simplifiedRenderer(240, 240),
				EmiRecipeSorting.compareOutputThenInput());
		SMELTING = new EmiRecipeCategory(new ResourceLocation("minecraft:smelting"), EmiStack.of(Block.furnaceIdle), simplifiedRenderer(224, 240),
				EmiRecipeSorting.compareOutputThenInput());
		ANVIL_REPAIRING = new EmiRecipeCategory(new ResourceLocation("emi:anvil_repairing"), EmiStack.of(Block.anvil), simplifiedRenderer(240, 224),
				EmiRecipeSorting.none());
		BREWING = new EmiRecipeCategory(new ResourceLocation("minecraft:brewing"), EmiStack.of(Item.brewingStand), simplifiedRenderer(224, 224),
				EmiRecipeSorting.none());
		WORLD_INTERACTION = new EmiRecipeCategory(new ResourceLocation("emi:world_interaction"), EmiStack.of(Item.itemsList[Block.grass.blockID]),
				simplifiedRenderer(208, 224), EmiRecipeSorting.none());
		EmiRenderable flame = (matrices, x, y, delta) -> {
			EmiTexture.FULL_FLAME.render(matrices, x + 1, y + 1, delta);
		};
		FUEL = new EmiRecipeCategory(new ResourceLocation("emi:fuel"), flame, flame, EmiRecipeSorting.compareInputThenOutput());
		INFO = new EmiRecipeCategory(new ResourceLocation("emi:info"), EmiStack.of(Item.writableBook), simplifiedRenderer(208, 224), EmiRecipeSorting.none());
	}
	
	@Override
	public void register(EmiRegistry registry) {
		registry.addIngredientSerializer(ItemEmiStack.class, new ItemEmiStackSerializer());
		registry.addIngredientSerializer(TagEmiIngredient.class, new TagEmiIngredientSerializer());
		registry.addCategory(CRAFTING);
		registry.addCategory(ANVIL_REPAIRING);
		registry.addCategory(SMELTING);
		registry.addCategory(BREWING);
		registry.addCategory(WORLD_INTERACTION);
		registry.addCategory(FUEL);
		registry.addCategory(INFO);
		registry.addCategory(TAG);
		registry.addCategory(INGREDIENT);
		registry.addCategory(RESOLUTION);

		for (int i = 4; i < 11; i++) {
			registry.addWorkstation(CRAFTING, EmiStack.of(new ItemStack(Block.workbench, 1, 0)));
//			registry.addWorkstation(CRAFTING, EmiStack.of(new ItemStack(Block.workbench, 1, 12)));
			registry.addWorkstation(CRAFTING, EmiStack.of(new ItemStack(Block.workbench, 1 , i)));
		}

		registry.addWorkstation(ANVIL_REPAIRING, EmiStack.of(Block.anvil));
		registry.addWorkstation(SMELTING, EmiStack.of(Block.furnaceIdle));
		registry.addWorkstation(BREWING, EmiStack.of(Item.brewingStand));
		
		registry.addRecipeHandler(ContainerPlayer.class, new InventoryRecipeHandler());
		registry.addRecipeHandler(ContainerWorkbench.class, new CraftingRecipeHandler());
		registry.addRecipeHandler(ContainerFurnace.class, new CookingRecipeHandler<>(SMELTING));

		registry.addExclusionArea(GuiContainerCreative.class, (screen, consumer) -> {
			int left = ((EMIGuiContainerCreative) screen).getGuiLeft();
			int top = ((EMIGuiContainerCreative) screen).getGuiTop();
			int width = ((EMIGuiContainerCreative) screen).getxSize();
			int bottom = top + ((EMIGuiContainerCreative) screen).getySize();
			consumer.accept(new Bounds(left, top - 28, width, 28));
			consumer.accept(new Bounds(left, bottom, width, 28));
		});
		
		registry.addGenericExclusionArea((screen, consumer) -> {
			if (screen instanceof GuiInventory inv) {
				Minecraft client = Minecraft.getMinecraft();
				Collection collection = client.thePlayer.getActivePotionEffects();
				if (!collection.isEmpty()) {
					int k = 33;
					if (collection.size() > 5) {
						k = 132 / (collection.size() - 1);
					}
					int right = ((EMIGuiContainerCreative) inv).getGuiLeft() + ((EMIGuiContainerCreative) inv).getxSize() + 2;
					int rightWidth = inv.width - right;
					if (rightWidth >= 32) {
						int top = ((EMIGuiContainerCreative) inv).getGuiTop();
						int height = (collection.size() - 1) * k + 32;
						int left, width;
						if (EmiConfig.effectLocation == EffectLocation.TOP) {
							int size = collection.size();
							top = ((EMIGuiContainerCreative) inv).getGuiTop() - 34;
                            int xOff = 34;
							if (size == 1) {
								xOff = 122;
							}
							else if (size > 5) {
								xOff = (((EMIGuiContainerCreative) inv).getxSize() - 32) / (size - 1);
							}
							width = Math.max(122, (size - 1) * xOff + 32);
							left = ((EMIGuiContainerCreative) inv).getGuiLeft() + (((EMIGuiContainerCreative) inv).getxSize() - width) / 2;
							height = 32;
						}
						else {
							left = switch (EmiConfig.effectLocation) {
								case LEFT_COMPRESSED -> ((EMIGuiContainerCreative) inv).getGuiLeft() - 2 - 32;
								case LEFT -> ((EMIGuiContainerCreative) inv).getGuiLeft() - 2 - 120;
								default -> right;
							};
							width = switch (EmiConfig.effectLocation) {
								case LEFT, RIGHT -> 120;
								case LEFT_COMPRESSED, RIGHT_COMPRESSED -> 32;
								default -> 32;
							};
						}
						consumer.accept(new Bounds(left, top, width, height));
					}
				}
			}
		});
		
		Comparison potionComparison = Comparison.of((a, b) -> RetroEMI.getEffects(a).equals(RetroEMI.getEffects(b)));
		
		registry.setDefaultComparison(Item.potion, potionComparison);
		registry.setDefaultComparison(Item.enchantedBook, Comparison.compareNbt());
		var prev = EmiStack.of(Item.enchantedBook);
		for (var ench : Enchantment.enchantmentsList) {
			if (ench == null) continue;
			var book = new ItemStack(Item.enchantedBook);
			EnchantmentHelper.setEnchantments(Map.of(ench.effectId, ench.getNumLevels()), book);
			registry.addEmiStackAfter(prev = EmiStack.of(book), prev);
		}
		
		PredicateAsSet<Item> hiddenItems = i -> {
			for (var inv : EmiStackList.invalidators) {
				if (inv.test(EmiStack.of(i))) {
					return true;
				}
			}
			return false;
		};
		
		// This is hardcoded in CraftingManager in 1.6
		for (Item i : EmiRepairItemRecipe.TOOLS) {
			if (!hiddenItems.contains(i)) {
				addRecipeSafe(registry, () -> new EmiRepairItemRecipe(i, synthetic("crafting/repairing", EmiUtil.subId(i))));
			}
		}
		
		for (IRecipe recipe : (List<IRecipe>) registry.getRecipeManager().getRecipeList()) {
			if (recipe instanceof RecipesMapExtending map) {
				EmiStack paper = EmiStack.of(Item.paper);
				addRecipeSafe(registry, () -> new EmiCraftingRecipe(List.of(paper, paper, paper, paper, EmiStack.of(Item.map), paper, paper, paper, paper),
						EmiStack.of(Item.map), new ResourceLocation("minecraft", "map_extending"), false, null), recipe);
			}

			else if (recipe instanceof ShapedRecipes shaped) {
				addRecipeSafe(registry, () -> new EmiShapedRecipe(shaped, (int) shaped.getUnmodifiedDifficulty()), recipe);
			}
			else if (recipe instanceof ShapelessRecipes shapeless) {
				addRecipeSafe(registry, () -> new EmiShapelessRecipe((EMIShapelessRecipes) shapeless, shapeless, (int) shapeless.getUnmodifiedDifficulty()), recipe);
			}

			else if (recipe instanceof RecipesArmorDyes dye) {
				for (Item i : EmiArmorDyeRecipe.DYEABLE_ITEMS) {
					if (i.hasMaterial(Material.leather)) {
						if (!hiddenItems.contains(i)) {
							addRecipeSafe(registry, () -> new EmiArmorDyeRecipe(i, synthetic("crafting/dying", EmiUtil.subId(i))), recipe);
						}
					}
				}
			}
			else if (recipe instanceof RecipeFireworks fwork) {
				// All firework recipes are one recipe in 1.6
				addRecipeSafe(registry, () -> new EmiFireworkStarRecipe(new ResourceLocation("minecraft", "firework_star")), recipe);
				addRecipeSafe(registry, () -> new EmiFireworkStarFadeRecipe(new ResourceLocation("minecraft", "firework_star_fade")), recipe);
				addRecipeSafe(registry, () -> new EmiFireworkRocketRecipe(new ResourceLocation("minecraft", "firework_rocket")), recipe);
			}
			else if (recipe instanceof RecipesMapCloning map) {
				addRecipeSafe(registry, () -> new EmiMapCloningRecipe(new ResourceLocation("minecraft", "map_cloning")), recipe);
			}
			else {
				MITEPlugin.addCustomIRecipes(recipe, registry);
				// No way to introspect arbitrary recipes in 1.6. :(
			}
		}
		
		for (var recipe : ((Map<Integer, ItemStack>) FurnaceRecipes.smelting().getSmeltingList()).entrySet()) {
			int id = recipe.getKey();
			ItemStack in = new ItemStack(Item.itemsList[id]);
			ItemStack out = recipe.getValue();
			TileEntityFurnace furnace = new TileEntityFurnace();
			int fuel = furnace.getFuelHeatLevel();
			addRecipeSafe(registry, () -> new EmiCookingRecipe(new ResourceLocation("minecraft", "oven/" + id), in, out, SMELTING, fuel));
		}

		for (Item i : Item.itemsList) {
			if (i == null) continue;
			if (hiddenItems.contains(i)) {
				continue;
			}
			if (i.isRepairable()) {
				if (i instanceof ItemArmor ai && ai.getArmorMaterial() != null && ai.getArmorMaterial().getMaterialMobility() != 0) {
					var material = Item.itemsList[ai.getArmorMaterial().getMaterialMobility()];
					addRecipeSafe(registry, () -> new EmiAnvilRecipe(EmiStack.of(i), EmiStack.of(material),
							new ResourceLocation("minecraft", "anvil/armor/" + SyntheticIdentifier.describe(i) + "/"+SyntheticIdentifier.describe(material))));
				} else if (i instanceof ItemTool ti && ti.getToolMaterial().getMaterialMobility() != 0) {
					var material = Item.itemsList[ti.getToolMaterial().getMaterialMobility()];
					addRecipeSafe(registry, () -> new EmiAnvilRecipe(EmiStack.of(i), EmiStack.of(material),
							new ResourceLocation("minecraft", "anvil/tool/" + SyntheticIdentifier.describe(i) + "/" + SyntheticIdentifier.describe(material))));
				}
			}
			if (i.isDamageable()) {
				addRecipeSafe(registry, () -> new EmiAnvilRepairItemRecipe(i, new ResourceLocation("minecraft", "anvil/repair/" + SyntheticIdentifier.describe(i))));
			}
			var is = new ItemStack(i);
			if (is.isEnchantable()) {
				for (Enchantment e : EmiAnvilEnchantRecipe.ENCHANTMENTS) {
					if (e.canEnchantItem(is.getItem())) {
						int max = e.getNumLevels();
						int min = e.getLevel(is);
						while (min <= max) {
							int finalMin = min;
							if (max == min)
								addRecipeSafe(registry, () -> new EmiAnvilEnchantRecipe(i, e, finalMin,
									new ResourceLocation("minecraft", "anvil/enchant/" + SyntheticIdentifier.describe(i) + "/" + e.effectId+"/" + SyntheticIdentifier.describe(finalMin))));
							min++;
						}
					}
				}
			}
		}
		
		EmiAgnos.addBrewingRecipes(registry);
		
		for (TagKey<?> key : EmiTags.TAGS) {
			if (new TagEmiIngredient(key, 1).getEmiStacks().size() > 1) {
				addRecipeSafe(registry, () -> new EmiTagRecipe(key));
			}
		}
		
		addFuel(registry, hiddenItems);
	}
	
	private static void addFuel(EmiRegistry registry, PredicateAsSet<Item> hiddenItems) {
		Map<Prototype, Integer> fuelMap = EmiAgnos.getFuelMap();
		Map<Prototype, Integer> heatMap = EmiAgnos.getHeatMap();
		compressRecipesToTags(fuelMap.keySet(), Comparator.comparingInt(fuelMap::get), tag -> {
			EmiIngredient stack = EmiIngredient.of(tag);
			Prototype item = Prototype.of(stack.getEmiStacks().get(0).getItemStack());
			int time = fuelMap.getOrDefault(item, 0);
			int heat = heatMap.getOrDefault(item, 0);
			registry.addRecipe(new EmiFuelRecipe(stack, time, heat, synthetic("fuel/tag", EmiUtil.subId(tag.id()))));
		}, item -> {
			if (!hiddenItems.contains(item.getItem())) {
				int time = fuelMap.get(item);
				int heat = heatMap.get(item);
				registry.addRecipe(new EmiFuelRecipe(EmiStack.of(item), time, heat,
						synthetic("fuel/item", EmiUtil.subId(item.getItem()) + "/" + item.toStack().getItemSubtype())));
			}
		});
	}
	
	private static void compressRecipesToTags(Set<Prototype> stacks, Comparator<Prototype> comparator, Consumer<TagKey<Prototype>> tagConsumer,
			Consumer<Prototype> itemConsumer) {
		Set<Prototype> handled = Sets.newHashSet();
		outer:
		for (TagKey<Prototype> key : EmiTags.getTags(Prototype.class)) {
			List<Prototype> items = key.get();
			if (items.size() < 2) {
				continue;
			}
			Prototype base = items.get(0);
			if (!stacks.contains(base)) {
				continue;
			}
			for (int i = 1; i < items.size(); i++) {
				Prototype item = items.get(i);
				if (!stacks.contains(item) || comparator.compare(base, item) != 0) {
					continue outer;
				}
			}
			if (handled.containsAll(items)) {
				continue;
			}
			handled.addAll(items);
			tagConsumer.accept(key);
		}
		for (Prototype item : stacks) {
			if (handled.contains(item)) {
				continue;
			}
			itemConsumer.accept(item);
		}
	}
	
	private static ResourceLocation synthetic(String type, String name) {
		return new ResourceLocation("emi", "/" + type + "/" + name);
	}
	
	private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier) {
		try {
			registry.addRecipe(supplier.get());
		}
		catch (Throwable e) {
			EmiReloadLog.warn("Exception when parsing EMI recipe (no ID available)");
			EmiReloadLog.error(e);
		}
	}
	
	private static void addRecipeSafe(EmiRegistry registry, Supplier<EmiRecipe> supplier, IRecipe recipe) {
		try {
			registry.addRecipe(supplier.get());
		}
		catch (Throwable e) {
			EmiReloadLog.warn("Exception when parsing vanilla recipe " + recipe);
			EmiReloadLog.error(e);
		}
	}
	
	private static EmiRenderable simplifiedRenderer(int u, int v) {
		return (raw, x, y, delta) -> {
			EmiDrawContext context = EmiDrawContext.wrap(raw);
			context.drawTexture(EmiRenderHelper.WIDGETS, x, y, u, v, 16, 16);
		};
	}
	
	private EmiRecipe basicWorld(EmiIngredient left, EmiIngredient right, EmiStack output, ResourceLocation id) {
		return basicWorld(left, right, output, id, true);
	}
	
	private EmiRecipe basicWorld(EmiIngredient left, EmiIngredient right, EmiStack output, ResourceLocation id, boolean catalyst) {
		return EmiWorldInteractionRecipe.builder().id(id).leftInput(left).rightInput(right, catalyst).output(output).build();
	}
}