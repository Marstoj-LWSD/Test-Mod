package net.marstoj.testmod.entity;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.marstoj.testmod.block.ModBlocks;
import net.marstoj.testmod.screen.ElectricFurnaceScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.*;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ElectricFurnaceBlockEntity extends BlockEntity implements ExtendedScreenHandlerFactory, ImplementedInventory, SidedInventory {

    public final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(2, ItemStack.EMPTY);
    private static int INPUT_SLOT = 0;
    private static int OUTPUT_SLOT = 1;
    private int cookTime;
    private int totalCookTime = 200;

    protected final PropertyDelegate propertyDelegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch(index) {
                case 0: {
                    return ElectricFurnaceBlockEntity.this.cookTime;
                }
                case 1: {
                    return ElectricFurnaceBlockEntity.this.totalCookTime;
                }
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch(index) {
                case 0: {
                    ElectricFurnaceBlockEntity.this.cookTime = value;
                    break;
                }
                case 1: {
                    ElectricFurnaceBlockEntity.this.totalCookTime = value;
                    break;
                }
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    private final RecipeManager.MatchGetter<Inventory, ? extends AbstractCookingRecipe> matchGetter;

    public ElectricFurnaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.ELECTRIC_FURNACE_BLOCK_ENTITY, pos, state);
        this.matchGetter = RecipeManager.createCachedMatchGetter(RecipeType.SMELTING);
    }

    @Override
    public Object getScreenOpeningData(ServerPlayerEntity player) {
        return new ElectricFurnacePayload(this.pos);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public Text getDisplayName() {
        return Text.literal("Electric Furnace Block");
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
    }
    public void tick(World world, BlockPos pos, BlockState state, ElectricFurnaceBlockEntity blockEntity) {
        if (blockEntity.isBurning(world, pos)) {
            if (!world.getBlockState(pos).get(Properties.LIT)) {
                world.setBlockState(pos, state.with(Properties.LIT, true));
            }
            if (isOutputSlotEmptyOrReceivable()) {
                if (this.hasRecipe()) {
                    this.increaseCookTime();
                    markDirty(world, pos, state);
                    if(maxCookedTimeReached()) {
                        this.craftItem();
                        this.resetProgress();
                    }
                } else {
                    this.resetProgress();
                }
            } else {
                this.resetProgress();
                markDirty(world, pos, state);
            }
        } else {
            world.setBlockState(pos, state.with(Properties.LIT, false));
        }
    }

    private boolean isBurning(World world, BlockPos pos) {
        return world.isReceivingRedstonePower(pos);
    }

    private void resetProgress() {
        this.cookTime = 0;
    }

    protected boolean isSmeltable(ItemStack itemStack) {
        return this.world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, new SimpleInventory(itemStack), this.world).isPresent();
    }

    private ItemStack getResultItem(ItemStack input) {
        Optional<RecipeEntry<SmeltingRecipe>> recipeEntry = this.world.getRecipeManager()
                .getFirstMatch(RecipeType.SMELTING, new SimpleInventory(input), this.world);
        if (recipeEntry.isPresent()) {
            return recipeEntry.get().value().craft(new SimpleInventory(input), null);
        } else {
            return ItemStack.EMPTY;
        }
    }

    private void craftItem() {
        ItemStack removedStack = this.removeStack(INPUT_SLOT, 1);
        ItemStack result = getResultItem(removedStack);
        this.setStack(OUTPUT_SLOT, new ItemStack(result.getItem(), getStack(OUTPUT_SLOT).getCount() + result.getCount()));
    }

    private boolean maxCookedTimeReached() {
        return cookTime >= totalCookTime;
    }

    private void increaseCookTime() {
        cookTime++;
    }

    private boolean hasRecipe() {
        boolean hasInput = isSmeltable(getStack(INPUT_SLOT));
        if (!hasInput) {
            return false;
        }
        ItemStack result = getResultItem(getStack(INPUT_SLOT));
        return hasInput && canInsertAmountIntoOutputSlot(result) && canInsertItemIntoOutputSlot(result.getItem());
    }

    private boolean canInsertItemIntoOutputSlot(Item item) {
        return this.getStack(OUTPUT_SLOT).getItem() == item || this.getStack(OUTPUT_SLOT).isEmpty();
    }

    private boolean canInsertAmountIntoOutputSlot(ItemStack result) {
        return this.getStack(OUTPUT_SLOT).getCount() + result.getCount() <= getStack(OUTPUT_SLOT).getMaxCount();
    }

    private boolean isOutputSlotEmptyOrReceivable() {
        return this.getStack(OUTPUT_SLOT).isEmpty() || this.getStack(OUTPUT_SLOT).getCount() < this.getStack(OUTPUT_SLOT).getMaxCount();
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, Direction direction) {
        return slot == INPUT_SLOT;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction direction) {
        return slot == OUTPUT_SLOT;
    }


    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new ElectricFurnaceScreenHandler(syncId, playerInventory, this, this.propertyDelegate);
    }
}
