//> Representing Code generate-ast
package com.workert.robotics.roboscript.tool;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
	public static void main(String[] args) throws IOException {
		if (args.length != 1) {
			System.err.println("Usage: generate_ast <output directory>");
			// System.exit(64);
		}
		String outputDir = "D:\\MinecraftMods\\CreateRobotics\\src\\main\\java\\com\\workert\\robotics\\roboscript"; //args[0];

		defineAst(outputDir, "Expr",
				Arrays.asList("Assign : Token name, Expr value", "Binary : Expr left, Token operator, Expr right",
						"Call : Expr callee, Token paren, List<Expr> arguments", "Get : Expr object, Token name",
						"Set : Expr object, Token name, Expr value", "Super : Token keyword, Token method",
						"This : Token keyword", "Grouping : Expr expression", "Literal : Object value",
						"Logical : Expr left, Token operator, Expr right", "Unary : Token operator, Expr right",
						"Variable : Token name"));
		defineAst(outputDir, "Stmt", Arrays.asList("Block : List<Stmt> statements",
				"Class : Token name, Expr.Variable superclass, List<Stmt.Function> methods",
				"Expression : Expr expression", "Function : Token name, List<Token> params, List<Stmt> body",
				"If : Expr condition, Stmt thenBranch," + " Stmt elseBranch", "Print : Expr expression",
				"Return : Token keyword, Expr value", "Break : Token keyword", "Var : Token name, Expr initializer",
				"While : Expr condition, Stmt body"));
		System.out.println("Finished generating AST!");
	}

	private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
		String path = outputDir + "/" + baseName + ".java";
		PrintWriter writer = new PrintWriter(path, "UTF-8");

		writer.println("//> Appendix II " + baseName.toLowerCase());
		writer.println("package com.workert.robotics.roboscript;");
		writer.println();
		writer.println("import java.util.List;");
		writer.println();
		writer.println("public abstract class " + baseName + " {");

		defineVisitor(writer, baseName, types);

		writer.println();
		writer.println("	// Nested " + baseName + " classes here...");
		// The AST classes.
		for (String type : types) {
			String className = type.split(":")[0].trim();
			String fields = type.split(":")[1].trim(); // [robust]
			defineType(writer, baseName, className, fields);
		}

		// The base accept() method.
		writer.println();
		writer.println("	abstract <R> R accept(Visitor<R> visitor);");

		writer.println("}");
		writer.println("//< Appendix II " + baseName.toLowerCase());
		writer.close();
	}

	private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
		writer.println("	interface Visitor<R> {");

		for (String type : types) {
			String typeName = type.split(":")[0].trim();
			writer.println(
					"		R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
		}

		writer.println("	}");
	}

	private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
		writer.println("//> " + baseName.toLowerCase() + "-" + className.toLowerCase());
		writer.println("	static class " + className + " extends " + baseName + " {");

		// Hack. Stmt.Class has such a long constructor that it overflows
		// the line length on the Appendix II page. Wrap it.
		if (fieldList.length() > 64) {
			fieldList = fieldList.replace(", ", ",\n					");
		}

		// Constructor.
		writer.println("		" + className + "(" + fieldList + ") {");

		fieldList = fieldList.replace(",\n					", ", ");
		// Store parameters in fields.
		String[] fields = fieldList.split(", ");
		for (String field : fields) {
			String name = field.split(" ")[1];
			writer.println("			this." + name + " = " + name + ";");
		}

		writer.println("		}");

		// Visitor pattern.
		writer.println();
		writer.println("		@Override");
		writer.println("		<R> R accept(Visitor<R> visitor) {");
		writer.println("			return visitor.visit" + className + baseName + "(this);");
		writer.println("		}");

		// Fields.
		writer.println();
		for (String field : fields) {
			writer.println("		final " + field + ";");
		}

		writer.println("	}");
		writer.println("//< " + baseName.toLowerCase() + "-" + className.toLowerCase());
	}
}