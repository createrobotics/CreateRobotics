package com.workert.robotics.helpers;

import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.contraptions.components.deployer.DeployerHandler;
import com.workert.robotics.Robotics;
import com.workert.robotics.entities.AbstractRobotEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class CodeHelper {
	private static final HashMap<String, BiConsumer<AbstractRobotEntity, List<String>>> commandMap = new HashMap<>();

	private static final HashMap<String, Function<AbstractRobotEntity, String>> internalVariableLookupMap = new HashMap<>();
	private static final HashMap<String, Function<AbstractRobotEntity, String>> publicVariableLookupMap = new HashMap<>();

	/**
	 * Registers a command for use by the Coding Mechanics.<br> The provided arguments from the {@link BiConsumer} may
	 * be an empty array if no arguments are provided. To cast an argument to a number (<code>Double</code>) please use
	 * the {@link CodeHelper#eval} function. IMPORTANT: The <code>function</code> {@link BiConsumer} will be run on a
	 * different Thread than the main Minecraft Thread!
	 *
	 * @param prefix   the prefix of the command, like <code>goTo</code> for
	 *                 <code>robot.goTo(x, y, z)</code>. May only contain a-Z
	 * @param function a {@link BiConsumer} with two arguments: the Robot Entity and an {@link ArrayList} with all
	 *                 provided arguments to the command.
	 */
	public static void registerCommand(String prefix, BiConsumer<AbstractRobotEntity, List<String>> function) {
		CodeHelper.commandMap.put(CodeHelper.validateRegistryName(prefix), function);
	}

	/**
	 * Registers a variable lookup for use by the Coding Mechanics.<br> The provided arguments from the
	 * <code>BiConsumer</code> may be an empty array if no arguments are provided.<br> To cast an argument to a number
	 * (<code>Double</code>) please use the {@link CodeHelper#eval} function as the argument may contain variables.
	 *
	 * @param name  the name of the variable, like <code>xPos</code> for
	 *              <code>$xPos</code>. May only contain a-Z
	 * @param value a {@link Function} with the Robot Entity as argument.<br> Should return a String that will get
	 *              replaced with the variable.
	 */
	public static void registerInternalVariableLookup(String name, Function<AbstractRobotEntity, String> value) {
		CodeHelper.internalVariableLookupMap.put(CodeHelper.validateRegistryName(name), value);
	}

	private static String validateRegistryName(String name) {
		name = name.replaceAll("[^a-zA-Z]", "").trim();
		return String.valueOf(name.charAt(0)).toLowerCase() + name.substring(1);
	}

	public static void registerDefaultCommands() {
		CodeHelper.registerInternalVariableLookup("xPos", robot -> Double.toString(robot.getX()));
		CodeHelper.registerInternalVariableLookup("yPos", robot -> Double.toString(robot.getY()));
		CodeHelper.registerInternalVariableLookup("zPos", robot -> Double.toString(robot.getZ()));

		CodeHelper.registerCommand("goTo", (robot, arguments) -> {

			robot.getNavigation().moveTo(CodeHelper.eval(arguments.get(0)), CodeHelper.eval(arguments.get(1)),
					CodeHelper.eval(arguments.get(2)), 1);
			int tryTimer = 0;
			while (robot.getNavigation().isInProgress()) {
				try {
					Thread.sleep(200);
					tryTimer++;
					if (tryTimer >= 10) {
						robot.getNavigation().recomputePath();
						tryTimer = 0;
					}
					if (robot.getNavigation().isStuck()) {
						CodeHelper.broadcastErrorToNearbyPlayers(robot,
								"Robot \"" + robot.getName().getString() + "\" is stuck! Trying to recompute path.");
						robot.getNavigation().recomputePath();
					}
				} catch (InterruptedException exception) {
					exception.printStackTrace();
				}
			}
			robot.getNavigation().stop();
			robot.setDeltaMovement(robot.getDeltaMovement().x, 0, robot.getDeltaMovement().z);

		});
		CodeHelper.registerCommand("getItems", (robot, arguments) -> {
			BlockPos pos = new BlockPos(CodeHelper.eval(arguments.get(0)), CodeHelper.eval(arguments.get(1)),
					CodeHelper.eval(arguments.get(2)));
			if (!pos.closerToCenterThan(robot.position(), 5)) return;
			robot.getLevel().getExistingBlockEntity(pos).getCapability(ForgeCapabilities.ITEM_HANDLER)
					.ifPresent(handler -> {
						for (int slot = 0; slot < handler.getSlots(); slot++) {
							while (!handler.getStackInSlot(slot)
									.isEmpty() && (arguments.size() < 4 || CodeHelper.getItemById(arguments.get(3))
									.equals(handler.getStackInSlot(slot).getItem())
									&& robot.wantsToPickUp(
									handler.extractItem(slot, 1, true)))) {
								robot.getInventory().addItem(handler.extractItem(slot, 1, false));
							}
						}
					});
			robot.getLevel().blockUpdated(pos, robot.getLevel().getBlockState(pos).getBlock());
		});
		CodeHelper.registerCommand("pushItems", (robot, arguments) -> {
			BlockPos pos = new BlockPos(CodeHelper.eval(arguments.get(0)), CodeHelper.eval(arguments.get(1)),
					CodeHelper.eval(arguments.get(2)));
			if (!pos.closerToCenterThan(robot.position(), 5)) return;
			robot.getLevel().getExistingBlockEntity(pos).getCapability(ForgeCapabilities.ITEM_HANDLER)
					.ifPresent(handler -> {
						Item itemToPush = Items.AIR;
						if (arguments.size() > 3) {
							itemToPush = CodeHelper.getItemById(arguments.get(3));
						}
						for (int slot = 0; slot < robot.getInventory().getContainerSize(); slot++) {
							if (itemToPush.equals(Items.AIR) || (robot.getInventory()
									.countItem(itemToPush) > 0 && robot.getInventory().getItem(slot).is(itemToPush))) {
								for (int containerSlot = 0; containerSlot < handler.getSlots(); containerSlot++) {
									robot.getInventory().setItem(slot,
											handler.insertItem(containerSlot, robot.getInventory().getItem(slot),
													false));
								}
							}
						}
					});
			robot.getLevel().blockUpdated(pos, robot.getLevel().getBlockState(pos).getBlock());
		});
		CodeHelper.registerCommand("punch", (robot, arguments) -> {
			if (arguments.size() < 3)
				throw new IllegalArgumentException("Expected three or more arguments for command \"punch\"");
			BlockPos clickPos = new BlockPos(CodeHelper.eval(arguments.get(0)), CodeHelper.eval(arguments.get(1)),
					CodeHelper.eval(arguments.get(2)));
			if (!clickPos.closerToCenterThan(robot.position(), 5)) return;

			Item item = null;
			if (arguments.size() > 4)
				item = CodeHelper.getItemById(arguments.get(4));

			try {
				CodeHelper.click(robot, clickPos, arguments.size() > 3 ? Direction.valueOf(
								arguments.get(3).trim().replace("Direction.", "")) : null,
						false,
						item);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		CodeHelper.registerCommand("use", (robot, arguments) -> {
			if (arguments.size() < 3)
				throw new IllegalArgumentException("Expected three or more arguments for command \"use\"");
			BlockPos clickPos = new BlockPos(CodeHelper.eval(arguments.get(0)), CodeHelper.eval(arguments.get(1)),
					CodeHelper.eval(arguments.get(2)));
			if (!clickPos.closerToCenterThan(robot.position(), 5)) return;

			Item item = null;
			if (arguments.size() > 4)
				item = CodeHelper.getItemById(arguments.get(4));

			try {
				CodeHelper.click(robot, clickPos, arguments.size() > 3 ? Direction.valueOf(
								arguments.get(3).trim().replace("Direction.", "")) : null,
						false, item);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		CodeHelper.registerCommand("wait", (robot, arguments) -> {
			if (arguments.size() < 1)
				throw new IllegalArgumentException("Expected one argument for command \"wait\"");
			try {
				Thread.sleep((long) CodeHelper.eval(arguments.get(0)));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
	}

	public static void runCode(AbstractRobotEntity robot, String code) {
		final String[] commandLine = new String[1];
		if (code == null || code.isBlank()) return;

		code = code.replace("\n", "").replace("\r", "");

		Robotics.LOGGER.debug("Starting to run code!");
		for (String command : code.split(";")) {
			commandLine[0] = command;

			if (commandLine[0].startsWith("//")) return;

			Robotics.LOGGER.debug("Running line:\"" + commandLine[0] + "\"");

			if (commandLine[0] == null || commandLine[0].isBlank()) return;
			commandLine[0] = commandLine[0].trim();


			CodeHelper.internalVariableLookupMap.forEach((name, value) -> {
				commandLine[0] = commandLine[0].replace("${" + name + "}", value.apply(robot));
				Robotics.LOGGER.debug(
						"Trying to replace public variable \"${" + name + "}\" with \"" + value.apply(robot) + "\"");
			});

			robot.privateVariableLookupMap.forEach((name, value) -> {
				commandLine[0] = commandLine[0].replace("${" + name + "}", value.apply(robot));
				Robotics.LOGGER.debug(
						"Trying to replace private variable \"${" + name + "}\" with \"" + value.apply(robot) + "\"");
			});

			CodeHelper.publicVariableLookupMap.forEach((name, value) -> {
				commandLine[0] = commandLine[0].replace("${" + name + "}", value.apply(robot));
				Robotics.LOGGER.debug(
						"Trying to replace public variable \"${" + name + "}\" with \"" + value.apply(robot) + "\"");
			});

			Robotics.LOGGER.debug("Reduced to:\"" + commandLine[0] + "\"");

			if (commandLine[0].startsWith("robot.")) {
				CodeHelper.commandMap.forEach((prefix, function) -> {
					if (commandLine[0].startsWith("robot." + prefix)) try {
						function.accept(robot, Arrays.asList(commandLine[0].substring(commandLine[0].indexOf("(") + 1,
								commandLine[0].lastIndexOf(")")).split(",")));
					} catch (Exception exception) {
						CodeHelper.broadcastErrorToNearbyPlayers(robot,
								"\"" + commandLine[0] + "\" encountered an error. Detail message:\n" + exception.getLocalizedMessage() + "\nPlease look at the logs to learn more.");
						exception.printStackTrace();
					}
				});
			} else if (commandLine[0].contains("=")) {
				if (commandLine[0].startsWith("public ")) {
					String[] list = commandLine[0].substring(7).split("=");
					CodeHelper.internalVariableLookupMap.put(CodeHelper.validateRegistryName(list[0].trim()),
							robotFromFunction -> list[1].trim());
				} else if (commandLine[0].startsWith("private ")) {
					String[] list = commandLine[0].substring(8).split("=");
					robot.privateVariableLookupMap.put(CodeHelper.validateRegistryName(list[0].trim()),
							robotFromFunction -> list[1].trim());
				} else {
					CodeHelper.broadcastErrorToNearbyPlayers(robot,
							"The Variable Declaration expected a \"public\" or \"private\" declaration in front, got \"" + commandLine[0] + "\"");
				}
			}
		}
	}

	public static Item getItemById(String id) {
		Item item = Registry.ITEM.get(new ResourceLocation(id.trim().split(":")[0],
				id.trim().split(":")[1]));

		if (item.equals(Items.AIR))
			throw new IllegalArgumentException("Unknown item: \"" + id.trim() + "\"");

		return item;
	}

	public static void click(AbstractRobotEntity robot, BlockPos posToClick, Direction direction, boolean use, @Nullable Item itemToClickWith)
			throws ClassNotFoundException, NoSuchMethodException, InterruptedException,
			InvocationTargetException, IllegalAccessException {
		DeployerFakePlayer fakePlayer = new DeployerFakePlayer((ServerLevel) robot.getLevel());

		if (itemToClickWith != null) {
			if (robot.getInventory().countItem(itemToClickWith) <= 0) return;
			for (int slot = 0; slot < robot.getInventory().getContainerSize(); slot++) {
				if (robot.getInventory().getItem(slot).is(itemToClickWith)) {
					ItemStack stackToAddToPlayer = robot.getInventory().removeItem(slot, 1);
					if (stackToAddToPlayer.isEmpty())
						break;
					stackToAddToPlayer.setCount(
							fakePlayer.getItemInHand(InteractionHand.MAIN_HAND).getCount() + 1);
					fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stackToAddToPlayer);

				}

			}
		}

		if (direction == null) {
			direction = Direction.DOWN;
		}

		fakePlayer.setXRot(direction == Direction.UP ? -90 : direction == Direction.DOWN ? 90 : 0);
		fakePlayer.setYRot(direction.toYRot());

		Method method = DeployerHandler.class.getDeclaredMethod("activate", DeployerFakePlayer.class,
				Vec3.class, BlockPos.class, Vec3.class, Class.forName(
						"com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity$Mode"));
		method.setAccessible(true);

		method.invoke(DeployerHandler.class, fakePlayer, robot.position(), posToClick,
				Vec3.atLowerCornerOf(direction.getNormal()), Class.forName(
								"com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity$Mode")
						.getEnumConstants()[use ? 0 : 1]);

		fakePlayer.getInventory().items.forEach(itemStack -> {
			if (robot.wantsToPickUp(itemStack)) {
				robot.getInventory().addItem(itemStack);
			} else {
				fakePlayer.drop(itemStack, true);
			}
		});
		fakePlayer.discard();

		Thread.sleep(200);
	}

	public static void broadcastErrorToNearbyPlayers(AbstractRobotEntity robot, String message) {
		int messageDistance = 256;
		for (Player player : robot.getLevel().getEntitiesOfClass(Player.class,
				new AABB(robot.blockPosition().offset(-messageDistance, -messageDistance, -messageDistance),
						robot.blockPosition().offset(messageDistance, messageDistance, messageDistance)))) {
			player.displayClientMessage(Component.literal("<!> ").withStyle(ChatFormatting.YELLOW)
					.append(Component.literal(message).withStyle(Style.EMPTY)), false);
		}
	}

	public static double eval(final String str) {
		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				this.ch = (++this.pos < str.length()) ? str.charAt(this.pos) : -1;
			}

			boolean eat(int charToEat) {
				while (this.ch == ' ') this.nextChar();
				if (this.ch == charToEat) {
					this.nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				this.nextChar();
				double x = this.parseExpression();
				if (this.pos < str.length()) throw new RuntimeException("Unexpected: " + (char) this.ch);
				return x;
			}

			double parseExpression() {
				double x = this.parseTerm();
				for (; ; ) {
					if (this.eat('+')) x += this.parseTerm(); // addition
					else if (this.eat('-')) x -= this.parseTerm(); // subtraction
					else return x;
				}
			}

			double parseTerm() {
				double x = this.parseFactor();
				for (; ; ) {
					if (this.eat('*')) x *= this.parseFactor(); // multiplication
					else if (this.eat('/')) x /= this.parseFactor(); // division
					else return x;
				}
			}

			double parseFactor() {
				if (this.eat('+')) return +this.parseFactor(); // unary plus
				if (this.eat('-')) return -this.parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (this.eat('(')) { // parentheses
					x = this.parseExpression();
					if (!this.eat(')')) throw new RuntimeException("Missing ')'");
				} else if ((this.ch >= '0' && this.ch <= '9') || this.ch == '.') { // numbers
					while ((this.ch >= '0' && this.ch <= '9') || this.ch == '.') this.nextChar();
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} else if (this.ch >= 'a' && this.ch <= 'z') { // functions
					while (this.ch >= 'a' && this.ch <= 'z') this.nextChar();
					String func = str.substring(startPos, this.pos);
					if (this.eat('(')) {
						x = this.parseExpression();
						if (!this.eat(')')) throw new RuntimeException("Missing ')' after argument to " + func);
					} else {
						x = this.parseFactor();
					}
					x = switch (func) {
						case "sqrt" -> Math.sqrt(x);
						case "sin" -> Math.sin(Math.toRadians(x));
						case "cos" -> Math.cos(Math.toRadians(x));
						case "tan" -> Math.tan(Math.toRadians(x));
						default -> throw new RuntimeException("Unknown function: " + func);
					};
				} else {
					throw new RuntimeException("Unexpected: " + (char) this.ch);
				}

				if (this.eat('^')) x = Math.pow(x, this.parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}
}
