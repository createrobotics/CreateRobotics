package com.workert.robotics.helpers;

import com.simibubi.create.content.contraptions.components.deployer.DeployerFakePlayer;
import com.simibubi.create.content.contraptions.components.deployer.DeployerHandler;
import com.workert.robotics.Robotics;
import com.workert.robotics.entities.AbstractRobotEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

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
		name.replaceAll("[^a-zA-Z]", "").trim();
		return String.valueOf(name.charAt(0)).toLowerCase() + name.substring(1);
	}

	public static void registerDefaultCommands() {
		CodeHelper.internalVariableLookupMap.put("xPos", robot -> Double.toString(robot.getX()));
		CodeHelper.internalVariableLookupMap.put("yPos", robot -> Double.toString(robot.getY()));
		CodeHelper.internalVariableLookupMap.put("zPos", robot -> Double.toString(robot.getZ()));
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
						CodeHelper.brodcastErrorToNearbyPlayers(robot,
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
									.isEmpty() && (arguments.size() < 4 || Registry.ITEM.getKey(
											handler.getStackInSlot(slot).getItem()).toString()
									.equals(arguments.get(3).trim())) && robot.wantsToPickUp(
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
			robot.getLevel().getExistingBlockEntity(pos).getCapability(ForgeCapabilities.ITEM_HANDLER)
					.ifPresent(handler -> {
						Item itemToPush = Items.AIR;
						if (arguments.size() > 3) {
							itemToPush = Registry.ITEM.get(new ResourceLocation(arguments.get(4).trim().split(":")[0],
									arguments.get(4).trim().split(":")[1]));
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
		CodeHelper.registerCommand("click", (robot, arguments) -> {
			if (arguments.size() < 3)
				throw new IllegalArgumentException();
			BlockPos pos = new BlockPos(CodeHelper.eval(arguments.get(0)), CodeHelper.eval(arguments.get(1)),
					CodeHelper.eval(arguments.get(2)));
			if (!pos.closerToCenterThan(robot.position(), 5)) return;
			try {
				DeployerFakePlayer fakePlayer = new DeployerFakePlayer((ServerLevel) robot.getLevel());
				fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, robot.getInventory().getItem(0));
				fakePlayer.setPos(robot.position());

				Method method = DeployerHandler.class.getDeclaredMethod("activate", DeployerFakePlayer.class,
						Vec3.class, BlockPos.class, Vec3.class, Class.forName(
								"com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity$Mode"));
				method.setAccessible(true);

				Object mode;
				switch (arguments.get(3).trim()) {
					case "Mode.PUNCH":
						mode = Class.forName(
										"com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity$Mode")
								.getEnumConstants()[0];
					case "Mode.USE":
						mode = Class.forName(
										"com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity$Mode")
								.getEnumConstants()[1];
				}

				method.invoke(DeployerHandler.class, fakePlayer, robot.position(), pos, Vec3.ZERO, Class.forName(
								"com.simibubi.create.content.contraptions.components.deployer.DeployerTileEntity$Mode")
						.getEnumConstants()[1]);

				robot.getInventory().setItem(0, fakePlayer.getItemInHand(InteractionHand.MAIN_HAND));
				fakePlayer.discard();
			} catch (Exception e) {
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
						CodeHelper.brodcastErrorToNearbyPlayers(robot,
								"\"" + commandLine[0] + "\" encountered an error. Please open the logs to learn more.");
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
					CodeHelper.brodcastErrorToNearbyPlayers(robot,
							"The Variable Declaration expected a \"public\" or \"private\" declaration infront, got \"" + commandLine[0] + "\"");
				}
			}
		}

	}

	private static void brodcastErrorToNearbyPlayers(AbstractRobotEntity robot, String message) {
		int messageDistance = 256;
		robot.getLevel().getEntitiesOfClass(Player.class,
						new AABB(robot.blockPosition().offset(-messageDistance, -messageDistance, -messageDistance),
								robot.blockPosition().offset(messageDistance, messageDistance, messageDistance)))
				.forEach(player -> {
					player.displayClientMessage(Component.literal("<!> ").withStyle(ChatFormatting.YELLOW)
							.append(Component.literal(message).withStyle(Style.EMPTY)), false);
				});
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
					if (func.equals("sqrt")) x = Math.sqrt(x);
					else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
					else throw new RuntimeException("Unknown function: " + func);
				} else {
					throw new RuntimeException("Unexpected: " + (char) this.ch);
				}

				if (this.eat('^')) x = Math.pow(x, this.parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}
}
