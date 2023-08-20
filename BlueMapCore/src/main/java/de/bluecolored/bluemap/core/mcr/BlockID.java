package de.bluecolored.bluemap.core.mcr;

import java.util.HashMap;
import java.util.Map;

public enum BlockID {
	// block names copied from Bukkit
	STONE(1, "minecraft:stone"),
	GRASS(2, "minecraft:grass_block"),
	DIRT(3, "minecraft:dirt"),
	COBBLESTONE(4, "minecraft:cobblestone"),
	WOOD(5, "minecraft:oak_planks"),
	OAK_SAPLING(6, "minecraft:oak_sapling"),
	SPRUCE_SAPLING(6, 1, "minecraft:spruce_sapling"),
	BIRCH_SAPLING(6, 2, "minecraft:birch_sapling"),
	BEDROCK(7, "minecraft:bedrock"),
	WATER(8, "minecraft:flowing_water"),
	STATIONARY_WATER(9, "minecraft:water"),
	LAVA(10, "minecraft:flowing_lava"),
	STATIONARY_LAVA(11, "minecraft:lava"),
	SAND(12, "minecraft:sand"),
	GRAVEL(13, "minecraft:gravel"),
	GOLD_ORE(14, "minecraft:gold_ore"),
	IRON_ORE(15, "minecraft:iron_ore"),
	COAL_ORE(16, "minecraft:coal_ore"),
	OAK_LOG(17, "minecraft:oak_log"),
	SPRUCE_LOG(17, 1, "minecraft:spruce_log"),
	BIRCH_LOG(17, 2, "minecraft:birch_log"),
	OAK_LEAVES(18, "minecraft:oak_leaves"),
	SPRUCE_LEAVES(18, 1, "minecraft:spruce_leaves"),
	BIRCH_LEAVES(18, 2, "minecraft:birch_leaves"),
	SPECIAL_LEAVES(18, 3, "minecraft:acacia_leaves"),
	SPONGE(19, "minecraft:sponge"),
	GLASS(20, "minecraft:glass"),
	LAPIS_ORE(21, "minecraft:lapis_ore"),
	LAPIS_BLOCK(22, "minecraft:lapis_block"),
	DISPENSER(23, "minecraft:dispenser"),
	SANDSTONE(24, "minecraft:sandstone"),
	NOTE_BLOCK(25, "minecraft:note_block"),
	BED(26, "minecraft:red_bed"),
	GOLDEN_RAIL(27, "minecraft:powered_rail"),
	DETECTOR_RAIL(28, "minecraft:detector_rail"),
	PISTON_STICKY(29, "minecraft:sticky_piston"),
	WEB(30, "minecraft:cobweb"),
	LONG_GRASS(31, "minecraft:grass"),
	FERN(31, 1, "minecraft:fern"),
	DEAD_BUSH(32, "minecraft:dead_bush"),
	PISTON(33, "minecraft:piston"),
	PISTON_EXTENSION(34, "minecraft:piston_head"),
	WOOL(35, "minecraft:white_wool"),
	ORANGE_WOOL(35, 1, "minecraft:orange_wool"),
	MAGENTA_WOOL(35, 2, "minecraft:magenta_wool"),
	LIGHTBLUE_WOOL(35, 3, "minecraft:light_blue_wool"),
	YELLOW_WOOL(35, 4, "minecraft:yellow_wool"),
	LIME_WOOL(35, 5, "minecraft:lime_wool"),
	PINK_WOOL(35, 6, "minecraft:pink_wool"),
	GRAY_WOOL(35, 7, "minecraft:gray_wool"),
	LIGHTGRAY_WOOL(35, 8, "minecraft:light_gray_wool"),
	CYAN_WOOL(35, 9, "minecraft:cyan_wool"),
	PURPLE_WOOL(35, 10, "minecraft:purple_wool"),
	BLUE_WOOL(35, 11, "minecraft:blue_wool"),
	BROWN_WOOL(35, 12, "minecraft:brown_wool"),
	GREEN_WOOL(35, 13, "minecraft:green_wool"),
	RED_WOOL(35, 14, "minecraft:red_wool"),
	BLACK_WOOL(35, 15, "minecraft:black_wool"),
	PISTON_MOVING(36, "minecraft:moving_piston"),
	YELLOW_FLOWER(37, "minecraft:dandelion"),
	RED_ROSE(38, "minecraft:poppy"),
	BROWN_MUSHROOM(39, "minecraft:brown_mushroom"),
	RED_MUSHROOM(40, "minecraft:red_mushroom"),
	GOLD_BLOCK(41, "minecraft:gold_block"),
	IRON_BLOCK(42, "minecraft:iron_block"),
	DOUBLE_STEP_STONE(43, "minecraft:smooth_stone_slab"),
	DOUBLE_STEP_SANDSTONE(43, 1, "minecraft:sandstone_slab"),
	DOUBLE_STEP_OAK(43, 2, "minecraft:oak_slab"),
	DOUBLE_STEP_COBBLESTONE(43, 3, "minecraft:cobblestone_slab"),
	DOUBLE_STEP_SPECIAL(43, 4, "minecraft:smooth_stone"),
	STEP_STONE(44, "minecraft:smooth_stone_slab"),
	STEP_SANDSTONE(44, 1, "minecraft:sandstone_slab"),
	STEP_OAK(44, 2, "minecraft:oak_slab"),
	STEP_COBBLESTONE(44, 3, "minecraft:cobblestone_slab"),
	BRICK(45, "minecraft:brick_block"),
	TNT(46, "minecraft:tnt"),
	BOOKSHELF(47, "minecraft:bookshelf"),
	MOSSY_COBBLESTONE(48, "minecraft:mossy_cobblestone"),
	OBSIDIAN(49, "minecraft:obsidian"),
	TORCH_GROUND(50, 5, "minecraft:torch"),
	TORCH_WALL(50, "minecraft:wall_torch"), // TODO
	FIRE(51, "minecraft:fire"), // TODO later
	MOB_SPAWNER(52, "minecraft:spawner"),
	WOOD_STAIRS(53, "minecraft:oak_stairs"), // TODO
	CHEST(54, "minecraft:chest"),
	REDSTONE_WIRE(55, "minecraft:redstone_wire"), // TODO
	DIAMOND_ORE(56, "minecraft:diamond_ore"),
	DIAMOND_BLOCK(57, "minecraft:diamond_block"),
	WORKBENCH(58, "minecraft:crafting_table"),
	CROPS(59, "minecraft:wheat"), // TODO
	SOIL(60, "minecraft:farmland"), // TODO
	FURNACE(61, "minecraft:furnace"),
	BURNING_FURNACE(62, "minecraft:furnace"),
	SIGN_POST(63, "minecraft:oak_sign"), // TODO
	WOODEN_DOOR(64, "minecraft:oak_door"), // TODO
	LADDER(65, "minecraft:ladder"), // TODO
	RAILS(66, "minecraft:rail"),
	COBBLESTONE_STAIRS(67, "minecraft:cobblestone_stairs"), // TODO
	WALL_SIGN(68, "minecraft:oak_sign"), // TODO
	LEVER(69, "minecraft:lever"), // TODO
	STONE_PLATE(70, "minecraft:stone_pressure_plate"),
	IRON_DOOR_BLOCK(71, "minecraft:iron_door"), // TODO
	WOOD_PLATE(72, "minecraft:oak_pressure_plate"),
	REDSTONE_ORE(73, "minecraft:redstone_ore"),
	GLOWING_REDSTONE_ORE(74, "minecraft:redstone_ore"),
	REDSTONE_TORCH_OFF_GROUND(75, 5, "minecraft:redstone_torch"),
	REDSTONE_TORCH_OFF_WALL(75, "minecraft:wall_redstone_torch"), // TODO
	REDSTONE_TORCH_ON_GROUND(76, 5, "minecraft:redstone_torch"),
	REDSTONE_TORCH_ON_WALL(76, "minecraft:wall_redstone_torch"), // TODO
	STONE_BUTTON(77, "minecraft:stone_button"), // TODO
	SNOW(78, "minecraft:snow"), // TODO
	ICE(79, "minecraft:ice"),
	SNOW_BLOCK(80, "minecraft:snow_block"),
	CACTUS(81, "minecraft:cactus"),
	CLAY(82, "minecraft:clay_block"),
	SUGAR_CANE_BLOCK(83, "minecraft:sugar_cane"),
	JUKEBOX(84, "minecraft:jukebox"),
	FENCE(85, "minecraft:oak_fence"), // TODO later
	PUMPKIN(86, "minecraft:pumpkin"), // TODO
	NETHERRACK(87, "minecraft:netherrack"),
	SOUL_SAND(88, "minecraft:soul_sand"),
	GLOWSTONE(89, "minecraft:glowstone"),
	PORTAL(90, "minecraft:nether_portal"), // TODO
	JACK_O_LANTERN(91, "minecraft:jack_o_lantern"), // TODO
	CAKE_BLOCK(92, "minecraft:cake"), // TODO
	DIODE_OFF(93, "minecraft:repeater"), // TODO
	DIODE_ON(94, "minecraft:repeater"), // TODO
	LOCKED_CHEST(95, "minecraft:chest_locked_aprilfools_super_old_legacy_we_should_not_even_have_this"),
	TRAP_DOOR(96, "minecraft:oak_trapdoor"); // TODO
	
	private final int id;
	private final int data;
	private final String val;
	private final HashMap<String, String> properties = new HashMap<>();
	
	private BlockID(int i, int data, String value) {
		this.id = i;
		this.data = data;
		this.val = value;
	}
	
	private BlockID(int i, String value) {
		this(i, 0, value);
	}
	
	private void putProperty(String key, String val) {
		this.properties.put(key, val);
	}
	
	public static BlockID query(int id) {
		return query(id, 0);
	}

	public static BlockID query(int id, int data) {
		for (BlockID bid : BlockID.values()) {
			int cleardata = data;
			if (isLeaves(bid))
				cleardata = data & 3;
			
			if (bid.id == id && bid.data == cleardata) {
				return bid;
			}
		}
		return null;
	}

	public int getId() {
		return this.id;
	}
	
	public String getModernId() {
		return this.val;
	}
	
	public int getData() {
		return this.data;
	}
	
	public Map<String, String> getBasicProperties() {
		return this.properties;
	}
	
	static {
		// absolutes
		OAK_LOG.putProperty("axis", "y");
		SPRUCE_LOG.putProperty("axis", "y");
		BIRCH_LOG.putProperty("axis", "y");
		
		DOUBLE_STEP_STONE.putProperty("type", "double");
		DOUBLE_STEP_SANDSTONE.putProperty("type", "double");
		DOUBLE_STEP_OAK.putProperty("type", "double");
		DOUBLE_STEP_COBBLESTONE.putProperty("type", "double");
		
		STEP_STONE.putProperty("type", "bottom");
		STEP_SANDSTONE.putProperty("type", "bottom");
		STEP_OAK.putProperty("type", "bottom");
		STEP_COBBLESTONE.putProperty("type", "bottom");
		
		BURNING_FURNACE.putProperty("lit", "true");
		GLOWING_REDSTONE_ORE.putProperty("lit", "true");
		REDSTONE_TORCH_OFF_GROUND.putProperty("lit", "false");
		REDSTONE_TORCH_OFF_WALL.putProperty("lit", "false");
		REDSTONE_TORCH_ON_GROUND.putProperty("lit", "true");
		REDSTONE_TORCH_ON_WALL.putProperty("lit", "true");
		DIODE_ON.putProperty("lit", "true");
		DIODE_OFF.putProperty("lit", "false");
	}
	
	protected static boolean isLeaves(BlockID bid) {
		return bid == OAK_LEAVES || bid == SPRUCE_LEAVES || bid == BIRCH_LEAVES || bid == SPECIAL_LEAVES;
	}
	
	protected static boolean isLog(BlockID bid) {
		return bid == OAK_LOG || bid == SPRUCE_LOG || bid == BIRCH_LOG;
	}
	
	protected static boolean isFluid(BlockID bid) {
		return bid == WATER || bid == LAVA;
	}
	
	protected static boolean isCobbleContainerBlock(BlockID bid) {
		return bid == DISPENSER || bid == FURNACE || bid == BURNING_FURNACE;
	}
	
	protected static boolean isRail(BlockID bid) {
		return bid == RAILS || bid == GOLDEN_RAIL || bid == DETECTOR_RAIL;
	}
	
	protected static boolean isPistonVariant(BlockID bid) {
		return bid == PISTON_STICKY || bid == PISTON || bid == PISTON_EXTENSION || bid == PISTON_MOVING;
	}
	
	public static Map<String, String> metadataToProperties(BlockID bid, int metadata) {
		HashMap<String, String> hm = new HashMap<String, String>();
		hm.putAll(bid.getBasicProperties());
		
		if (bid == BlockID.BED) {
			if (metadata == 0 || metadata == 4 || metadata == 8 || metadata == 12)
				hm.put("facing", "west");
			else if (metadata == 1 || metadata == 5 || metadata == 9 || metadata == 13)
				hm.put("facing", "north");
			else if (metadata == 2 || metadata == 6 || metadata == 10 || metadata == 14)
				hm.put("facing", "east");
			else if (metadata == 3 || metadata == 7 || metadata == 11 || metadata == 15)
				hm.put("facing", "south");
			
			if (metadata < 8)
				hm.put("part", "foot");
			else
				hm.put("part", "head");
			
		} else if (isFluid(bid)) {
			metadata &= 15;
			
			hm.put("level", "" + metadata);
		} else if (isCobbleContainerBlock(bid)) {
			if (metadata == 4)
				hm.put("facing", "north");
			else if (metadata == 2)
				hm.put("facing", "east");
			else if (metadata == 5)
				hm.put("facing", "south");
			else if (metadata == 3)
				hm.put("facing", "west");
			
		} else if (isRail(bid)) {
			if (metadata == 0)
				hm.put("shape", "east_west");
			else if (metadata == 1)
				hm.put("shape", "north_south");
			else if (metadata == 2 || metadata == 10)
				hm.put("shape", "ascending_south");
			else if (metadata == 3 || metadata == 11)
				hm.put("shape", "ascending_north");
			else if (metadata == 4 || metadata == 12)
				hm.put("shape", "ascending_east");
			else if (metadata == 5 || metadata == 13)
				hm.put("shape", "ascending_west");
			else if (metadata == 6)
				hm.put("shape", "south_west");
			else if (metadata == 7)
				hm.put("shape", "north_west");
			else if (metadata == 8)
				hm.put("shape", "north_east");
			else if (metadata == 9)
				hm.put("shape", "south_east");
			
			if (bid != BlockID.RAILS) {
				if (metadata < 8)
					hm.put("powered", "false");
				else
					hm.put("powered", "true");
			}
		} else if (isPistonVariant(bid)) {
			if (metadata == 0 || metadata == 8)
				hm.put("facing", "down");
			else if (metadata == 1 || metadata == 9)
				hm.put("facing", "up");
			else if (metadata == 2 || metadata == 10)
				hm.put("facing", "east");
			else if (metadata == 3 || metadata == 11)
				hm.put("facing", "west");
			else if (metadata == 4 || metadata == 12)
				hm.put("facing", "north");
			else if (metadata == 5 || metadata == 13)
				hm.put("facing", "south");
			
			if (metadata < 8)
				hm.put("extended", "false");
			else
				hm.put("extended", "true");
			
		} else if (bid == BlockID.SNOW) {
			metadata &= 8;
			
			// b1.7.3 counts from 0, modern versions count from 1
			hm.put("layers", "" + (metadata + 1));
		}
		return hm;
	}
}
