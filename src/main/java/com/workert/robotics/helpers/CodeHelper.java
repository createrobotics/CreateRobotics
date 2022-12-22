package com.workert.robotics.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiConsumer;

import com.workert.robotics.entities.AbstractRobotEntity;

public class CodeHelper {
	private static HashMap<String, BiConsumer<AbstractRobotEntity, List<Object>>> commandMap = new HashMap<>();

	/**
	 * Registers a command for use by the Coding Mechanics.
	 *
	 * @param prefix the prefix of the command, like <code>goTo</code> for
	 * <code>robot.goTo(1, 1, 1)</code>. May only contain a-Z
	 * @param function a {@link BiConsumer} with two arguments: the Robot Entity and
	 * an {@link ArrayList} with all provided arguments to the command
	 */
	public static void registerCommand(String prefix, BiConsumer<AbstractRobotEntity, List<Object>> function) {
		// TODO edit prefix to always be ok
		commandMap.put(prefix, function);
	}

	public static void registerDefaultCommands() {
		registerCommand("goTo", (robot, coordinateList) -> {
			try {
				robot.getNavigation().moveTo(eval((String) coordinateList.get(0)), eval((String) coordinateList.get(1)),
						eval((String) coordinateList.get(2)), 1);
			} catch (Exception exception) {
				throw new IllegalArgumentException(
						"\"robot.goTo\" takes three arguments from the type \"Double\".\nException message: \""
								+ exception.getLocalizedMessage() + "\"");
			}
		});
	}

	public static void runCode(AbstractRobotEntity robot, String code) {
		code.lines().forEach(command -> {
			command = command.replace(" ", "");
			command = command.replace("robot.getX()", Double.toString(robot.getX()));
			command = command.replace("robot.getY()", Double.toString(robot.getY()));

			final String commandFinal = command;
			commandMap.forEach((prefix, function) -> {
				if (commandFinal.startsWith("robot." + prefix))
					function.accept(robot,
							Arrays.asList(commandFinal.substring(commandFinal.indexOf("(") + 1).split(",")));
			});
		});
	}

	public static double eval(final String str) {
		return new Object() {
			int pos = -1, ch;

			void nextChar() {
				this.ch = (++this.pos < str.length()) ? str.charAt(this.pos) : -1;
			}

			boolean eat(int charToEat) {
				while (this.ch == ' ')
					this.nextChar();
				if (this.ch == charToEat) {
					this.nextChar();
					return true;
				}
				return false;
			}

			double parse() {
				this.nextChar();
				double x = this.parseExpression();
				if (this.pos < str.length())
					throw new RuntimeException("Unexpected: " + (char) this.ch);
				return x;
			}

			// Grammar:
			// expression = term | expression `+` term | expression `-` term
			// term = factor | term `*` factor | term `/` factor
			// factor = `+` factor | `-` factor | `(` expression `)` | number
			//        | functionName `(` expression `)` | functionName factor
			//        | factor `^` factor

			double parseExpression() {
				double x = this.parseTerm();
				for (;;) {
					if (this.eat('+'))
						x += this.parseTerm(); // addition
					else if (this.eat('-'))
						x -= this.parseTerm(); // subtraction
					else
						return x;
				}
			}

			double parseTerm() {
				double x = this.parseFactor();
				for (;;) {
					if (this.eat('*'))
						x *= this.parseFactor(); // multiplication
					else if (this.eat('/'))
						x /= this.parseFactor(); // division
					else
						return x;
				}
			}

			double parseFactor() {
				if (this.eat('+'))
					return +this.parseFactor(); // unary plus
				if (this.eat('-'))
					return -this.parseFactor(); // unary minus

				double x;
				int startPos = this.pos;
				if (this.eat('(')) { // parentheses
					x = this.parseExpression();
					if (!this.eat(')'))
						throw new RuntimeException("Missing ')'");
				} else if ((this.ch >= '0' && this.ch <= '9') || this.ch == '.') { // numbers
					while ((this.ch >= '0' && this.ch <= '9') || this.ch == '.')
						this.nextChar();
					x = Double.parseDouble(str.substring(startPos, this.pos));
				} else if (this.ch >= 'a' && this.ch <= 'z') { // functions
					while (this.ch >= 'a' && this.ch <= 'z')
						this.nextChar();
					String func = str.substring(startPos, this.pos);
					if (this.eat('(')) {
						x = this.parseExpression();
						if (!this.eat(')'))
							throw new RuntimeException("Missing ')' after argument to " + func);
					} else {
						x = this.parseFactor();
					}
					if (func.equals("sqrt"))
						x = Math.sqrt(x);
					else if (func.equals("sin"))
						x = Math.sin(Math.toRadians(x));
					else if (func.equals("cos"))
						x = Math.cos(Math.toRadians(x));
					else if (func.equals("tan"))
						x = Math.tan(Math.toRadians(x));
					else
						throw new RuntimeException("Unknown function: " + func);
				} else {
					throw new RuntimeException("Unexpected: " + (char) this.ch);
				}

				if (this.eat('^'))
					x = Math.pow(x, this.parseFactor()); // exponentiation

				return x;
			}
		}.parse();
	}
}
