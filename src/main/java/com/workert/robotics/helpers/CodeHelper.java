package com.workert.robotics.helpers;

import com.simibubi.create.Create;
import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.contraptions.components.deployer.DeployerHandler;
import com.simibubi.create.content.logistics.IRedstoneLinkable;
import com.simibubi.create.content.logistics.RedstoneLinkNetworkHandler;
import com.simibubi.create.foundation.utility.Couple;
import com.workert.robotics.Robotics;
import com.workert.robotics.content.robotics.AbstractRobotEntity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CodeHelper {
	/**
	 * DO NOT USE!<br>See {@link CodeHelper#registerCommand} to register commands.
	 */
	public static final HashMap<String, BiConsumer<AbstractRobotEntity, List<String>>> commandMap = new HashMap<>();

	/**
	 * DO NOT USE!<br>See {@link CodeHelper#registerInternalVariableLookup} to register variable lookups.
	 */
	public static final HashMap<String, Function<AbstractRobotEntity, String>> internalVariableLookupMap = new HashMap<>();
	private static final HashMap<String, Function<AbstractRobotEntity, String>> publicVariableLookupMap = new HashMap<>();

	/**
	 * Registers a command for use by the Coding Mechanics.<br> The provided arguments from the {@link BiConsumer} may
	 * be an empty array if no arguments are provided. To cast an argument to a number (<code>Double</code>) please use
	 * the {@link CodeHelper#eval} function. IMPORTANT: The <code>function</code> {@link BiConsumer} will be run on a
	 * different Thread than the main Minecraft Thread!
	 *
	 * @param prefix   the prefix of the command, like <code>goTo</code> for
	 *                 <code>robot.goTo(x, y, z)</code>. May only contain a-Z and should start with a lowercase letter
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
	 *              <code>$xPos</code>. May only contain a-Z and should start with a lowercase letter
	 * @param value a {@link Function} with the Robot Entity as argument.<br> Should return a String that will get
	 *              replaced with the variable.
	 */
	public static void registerInternalVariableLookup(String name, Function<AbstractRobotEntity, String> value) {
		CodeHelper.internalVariableLookupMap.put(CodeHelper.validateRegistryName(name), value);
	}

	private static String validateRegistryName(String name) {
		if (name.matches(".*[^a-zA-Z].*")) throw new IllegalArgumentException(
				"RegistryNames may only contain a-Z and should start with a lowercase letter");
		return String.valueOf(name.trim().charAt(0)).toLowerCase() + name.trim().substring(1);
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
			if (robot.getLevel().getExistingBlockEntity(pos) == null) throw new IllegalArgumentException(
					"The block at the specified coordinates has no tile entity (is no container)!");
			robot.getLevel().getExistingBlockEntity(pos).getCapability(ForgeCapabilities.ITEM_HANDLER)
					.ifPresent(handler -> {
						for (int slot = 0; slot < handler.getSlots(); slot++) {
							while (!handler.getStackInSlot(slot)
									.isEmpty() && (arguments.size() < 4 || CodeHelper.getItemById(arguments.get(3))
									.equals(handler.getStackInSlot(slot).getItem())) && robot.wantsToPickUp(
									handler.extractItem(slot, 1, true))) {
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
			if (robot.getLevel().getExistingBlockEntity(pos) == null) throw new IllegalArgumentException(
					"The block at the specified coordinates has no tile entity (is no container)!");
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
			Direction direction = null;
			if (arguments.size() > 3) {
				if (arguments.get(3).trim().startsWith("Direction")) {
					direction = Direction.valueOf(arguments.get(3).trim().replace("Direction.", ""));
					if (arguments.size() > 4) item = CodeHelper.getItemById(arguments.get(4));
				} else item = CodeHelper.getItemById(arguments.get(3));
			}

			try {
				CodeHelper.click(robot, clickPos, direction, false, item);
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
			Direction direction = null;
			if (arguments.size() > 3) {
				if (arguments.get(3).trim().startsWith("Direction")) {
					direction = Direction.valueOf(arguments.get(3).trim().replace("Direction.", ""));
					if (arguments.size() > 4) item = CodeHelper.getItemById(arguments.get(4));
				} else item = CodeHelper.getItemById(arguments.get(3));
			}

			try {
				CodeHelper.click(robot, clickPos, direction, false, item);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		CodeHelper.registerCommand("wait", (robot, arguments) -> {
			if (arguments.size() < 1) throw new IllegalArgumentException("Expected one argument for command \"wait\"");
			try {
				Thread.sleep((long) CodeHelper.eval(arguments.get(0)));
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		});
		CodeHelper.registerCommand("waitForRedstoneLink", (robot, arguments) -> {
			if (arguments.size() < 1) throw new IllegalArgumentException(
					"Expected one or more arguments for command \"waitForRedstoneLink\"");


			RedstoneLinkNetworkHandler.Frequency secondFrequency = RedstoneLinkNetworkHandler.Frequency.EMPTY;
			if (arguments.size() > 1) secondFrequency = RedstoneLinkNetworkHandler.Frequency.of(
					CodeHelper.getItemById(arguments.get(1)).getDefaultInstance());

			while (!Create.REDSTONE_LINK_NETWORK_HANDLER.hasAnyLoadedPower(Couple.create(
					RedstoneLinkNetworkHandler.Frequency.of(
							CodeHelper.getItemById(arguments.get(0)).getDefaultInstance()), secondFrequency))) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);

				}
			}
		});
		CodeHelper.registerCommand("setRedstoneLink", (robot, arguments) -> {
			if (arguments.size() < 1)
				throw new IllegalArgumentException("Expected one or more arguments for command \"setRedstoneLink\"");

			RedstoneLinkNetworkHandler.Frequency secondFrequency = RedstoneLinkNetworkHandler.Frequency.EMPTY;
			int signalStrength = 0;
			if (arguments.size() > 1) {
				try {
					signalStrength = (int) CodeHelper.eval(arguments.get(1));
				} catch (RuntimeException e) {
					secondFrequency = RedstoneLinkNetworkHandler.Frequency.of(
							CodeHelper.getItemById(arguments.get(1)).getDefaultInstance());
					if (arguments.size() > 2) signalStrength = (int) CodeHelper.eval(arguments.get(1));
				}
			}

			RobotFrequencyEntry entry = robot.getRobotFrequencyEntry();
			entry.frequency = Couple.create(RedstoneLinkNetworkHandler.Frequency.of(
					CodeHelper.getItemById(arguments.get(0)).getDefaultInstance()), secondFrequency);
			entry.signalStrength = signalStrength;

			System.out.println(entry);
			System.out.println(Create.REDSTONE_LINK_NETWORK_HANDLER.networksIn(robot.getLevel()).containsKey(entry));
			Create.REDSTONE_LINK_NETWORK_HANDLER.addToNetwork(robot.getLevel(), entry);

		});
	}

	public static void runCode(AbstractRobotEntity robot, String code) {
		if (code == null || code.isBlank()) return;

		code = code.replace("\n", "").replace("\r", "");
		code = code.replaceAll("/\\*.*?\\*/", ""); // Comments in /* */

		Robotics.LOGGER.debug("Starting to run code!");
		Robotics.LOGGER.debug("Full code: \"" + code + "\"");

		runFormattedCodeSnippet(robot, code);
	}

	private static void runFormattedCodeSnippet(AbstractRobotEntity robot, String code) {
		Robotics.LOGGER.debug("Running: " + code);

		int charPos = 0;
		while (charPos < code.length()) {
			int nextSemicolon = code.indexOf(";", charPos);
			if (nextSemicolon == -1) nextSemicolon = code.length();

			String command = code.substring(charPos, nextSemicolon).trim();
			Robotics.LOGGER.debug("Command: \"" + command + "\"");

			if (command.matches("^if\\s*\\(.*")) { // Matches "if (" with zero or more spaces between "if" and "("
				Pattern pattern = Pattern.compile("\\)\\s*\\{"); // Matches ") {" with zero or more spaces
				Matcher matcher = pattern.matcher(command);
				String condition = command.substring(command.indexOf("("), matcher.start());
				System.out.println(condition);

				String codeInsideCurlyBraces = getTextInsideCurlyBraces(
						code.substring(charPos + command.indexOf("{") + 1));
				runFormattedCodeSnippet(robot, codeInsideCurlyBraces);
				nextSemicolon = charPos + command.indexOf("{") + codeInsideCurlyBraces.length() + 1;
			} else if (command.startsWith("public ") || command.startsWith("private ")) {
				Pattern pattern = Pattern.compile(
						"^(?:public|private)\\s+(\\S+)\\s*=\\s*(\\S+)"); // Matches "public TEXT1 = TEXT2" or "private  TEXT1=TEXT2" and outputs TEXT1 and TEXT2 as group
				Matcher matcher = pattern.matcher(command);
				if (matcher.matches()) {
					if (command.startsWith("public ")) {
						publicVariableLookupMap.put(CodeHelper.validateRegistryName(matcher.group(1)),
								robotFromFunction -> matcher.group(2));
					} else if (command.startsWith("private ")) {
						/*robot.privateVariableLookupMap.put(CodeHelper.validateRegistryName(matcher.group(1)),
								robotFromFunction -> matcher.group(2));*/
					}
				} else {
					CodeHelper.broadcastErrorToNearbyPlayers(robot,
							"Command \"" + command + "\" encountered an error assigning the variable.");
				}
			} else {
				runCommand(robot, command);
			}

			charPos = nextSemicolon + 1;
		}

		Robotics.LOGGER.debug("Finished running: " + code);
	}

	private static String getTextInsideCurlyBraces(String input) {
		int openedCurlyBraces = 0;
		int closeCurlyBraceSearchIndex = 0;
		int closeCurlyBracePos = -1;

		while (closeCurlyBracePos == -1) {
			switch (input.charAt(closeCurlyBraceSearchIndex)) {
				case '{' -> openedCurlyBraces++;
				case '}' -> {
					if (openedCurlyBraces == 0) closeCurlyBracePos = closeCurlyBraceSearchIndex;
					else openedCurlyBraces++;
				}
			}
			closeCurlyBraceSearchIndex++;
		}

		return input.substring(0, closeCurlyBraceSearchIndex - 1);
	}

	private static void runCommand(AbstractRobotEntity robot, String command) {
		if (command.isEmpty() || !command.endsWith(")")) return;

		command = command.substring(0, command.length() - 2); // Remove the ")" at the end

		final String[] commandToRun = {command};

		CodeHelper.internalVariableLookupMap.forEach((name, value) -> {
			commandToRun[0] = commandToRun[0].replace("${" + name + "}", value.apply(robot));
			Robotics.LOGGER.debug(
					"Trying to replace public variable \"${" + name + "}\" with \"" + value.apply(robot) + "\"");
		});

		/*robot.privateVariableLookupMap.forEach((name, value) -> {
			commandToRun[0] = commandToRun[0].replace("${" + name + "}", value.apply(robot));
			Robotics.LOGGER.debug(
					"Trying to replace private variable \"${" + name + "}\" with \"" + value.apply(robot) + "\"");
		});*/

		CodeHelper.publicVariableLookupMap.forEach((name, value) -> {
			commandToRun[0] = commandToRun[0].replace("${" + name + "}", value.apply(robot));
			Robotics.LOGGER.debug(
					"Trying to replace public variable \"${" + name + "}\" with \"" + value.apply(robot) + "\"");
		});

		Pattern pattern = Pattern.compile(".*(\\$\\{.*}).*"); // Matches "${}" with anything inside
		Matcher matcher = pattern.matcher(commandToRun[0]);
		while (matcher.matches()) {
			CodeHelper.broadcastErrorToNearbyPlayers(robot,
					"Command \"" + commandToRun[0] + "\" could not find the variable \"" + matcher.group() + "\", ignoring it");
			commandToRun[0] = commandToRun[0].replaceFirst("\\$\\{.*}", "");
		}

		CodeHelper.commandMap.forEach((prefix, function) -> {
			if (commandToRun[0].matches("^robot\\." + prefix + "\\s*\\(.*")) try {
				function.accept(robot,
						Arrays.asList(commandToRun[0].substring(commandToRun[0].indexOf("(") + 1).split(",")));
			} catch (Exception exception) {
				CodeHelper.broadcastErrorToNearbyPlayers(robot,
						"Command \"" + commandToRun[0] + "\" encountered an error. Detail message:\n" + exception.getLocalizedMessage());
				exception.printStackTrace();
			}
		});
	}

	public static Item getItemById(String id) {
		Item item = Registry.ITEM.get(new ResourceLocation(id.trim().split(":")[0], id.trim().split(":")[1]));

		if (item.equals(Items.AIR)) throw new IllegalArgumentException("Unknown item: \"" + id.trim() + "\"");

		return item;
	}

	public static void click(AbstractRobotEntity robot, BlockPos posToClick, Direction direction, boolean use, @Nullable Item itemToClickWith) throws ClassNotFoundException, NoSuchMethodException, InterruptedException, InvocationTargetException, IllegalAccessException {
		DeployerFakePlayer fakePlayer = new DeployerFakePlayer((ServerLevel) robot.getLevel());

		if (itemToClickWith != null) {
			if (robot.getInventory().countItem(itemToClickWith) <= 0) return;
			for (int slot = 0; slot < robot.getInventory().getContainerSize(); slot++) {
				if (robot.getInventory().getItem(slot).is(itemToClickWith)) {
					ItemStack stackToAddToPlayer = robot.getInventory().removeItem(slot, 1);
					if (stackToAddToPlayer.isEmpty()) break;
					stackToAddToPlayer.setCount(fakePlayer.getItemInHand(InteractionHand.MAIN_HAND).getCount() + 1);
					fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, stackToAddToPlayer);

				}

			}
		}

		if (direction == null) {
			direction = Direction.DOWN;
		}

		fakePlayer.setXRot(direction == Direction.UP ? -90 : direction == Direction.DOWN ? 90 : 0);
		fakePlayer.setYRot(direction.toYRot());

		Method method = DeployerHandler.class.getDeclaredMethod("activate", DeployerFakePlayer.class, Vec3.class,
				BlockPos.class, Vec3.class,
				Class.forName("com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity$Mode"));
		method.setAccessible(true);

		method.invoke(DeployerHandler.class, fakePlayer, robot.position(), posToClick,
				Vec3.atLowerCornerOf(direction.getNormal()),
				Class.forName("com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity$Mode")
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
				while (this.ch == ' ') {
					this.nextChar();
				}
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
					while ((this.ch >= '0' && this.ch <= '9') || this.ch == '.') {
						this.nextChar();
					}
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} else if (this.ch >= 'a' && this.ch <= 'z') { // functions
					while (this.ch >= 'a' && this.ch <= 'z') {
						this.nextChar();
					}
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

	public static class RobotFrequencyEntry implements IRedstoneLinkable {
		private final AbstractRobotEntity robot;
		private Couple<RedstoneLinkNetworkHandler.Frequency> frequency;
		private int signalStrength;

		public RobotFrequencyEntry(AbstractRobotEntity robot, Couple<RedstoneLinkNetworkHandler.Frequency> frequency, int signalStrength) {
			this.robot = robot;
			this.frequency = frequency;
			this.signalStrength = signalStrength;
		}

		@Override
		public int getTransmittedStrength() {
			return this.signalStrength;
		}

		@Override
		public void setReceivedStrength(int power) {
		}

		@Override
		public boolean isListening() {
			return false;
		}

		@Override
		public boolean isAlive() {
			return true;
		}


		@Override
		public Couple<RedstoneLinkNetworkHandler.Frequency> getNetworkKey() {
			return this.frequency;
		}

		@Override
		public BlockPos getLocation() {
			return this.robot.blockPosition();
		}
	}
}
