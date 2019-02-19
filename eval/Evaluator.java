/**
 * Author: Jake Wachs
 * CS 403, Programming Languages
 * The University of Alabama
 *
 * Evaluator class
 */

// Need to write evalMethodCall() and evalIDCall(), as well as add BUILTIN to types
// isFunctionCall() may be incorrect if there is an empty (ie null) exprlist to the cdr

// lookup should be returning the closure correct? so is there another env method I need?

package eval;

import lex.*;
import parse.*;
import env.*;

import java.io.*;



public class Evaluator implements Types
{
	/**
	 * Default constructor
	 */
	public Evaluator()
	{
	}

/***** Helper Methods *****/
	/**
	 * Construct method for binding together Lexemes
	 * @type The type of the new Lexeme
	 * @l The pointer to the left of the Lexeme
	 * @r The pointer to the right of the Lexeme
	 */
	private Lexeme cons(String type, Lexeme l, Lexeme r)
	{
		Lexeme ret = new Lexeme(type);
		ret.setCar(l);
		ret.setCdr(r);

		return ret;
	}

	/**
	 * Checks to make sure there is a correct number of command line args
	 * @args String array of command line args
	 */
	private static void checkCmdArgs(String[] args)
	{
		if (args.length != 1)
		{
			System.out.println("Incorrect number of command line args");
			System.exit(-1);
		}
	}

	/**
	 * Safe method for opening file
	 * @filename The name of the file to be opened
	 * @return The pointer to the opened file object
	 */
	private static File openFile(String filename) throws IOException
	{
		File file = new File(filename);

		if ( !file.exists() )
		{
			System.out.println("File does not exist");
			System.exit(-1);
		}

		return file;
	}


	/**
	 * Gets the parameters from the function definition
	 * @closure The closure from which to get the params
	 * @return The root of the paramList
	 */
	private Lexeme getParams(Lexeme closure)
	{
		return closure.getCdr().getCdr().getCar();
	}

	/**
	 * Gets the body from the function definition
	 * @closure The closure from which to get the body
	 * @return The root of the body/block
	 */
	private Lexeme getBody(Lexeme closure)
	{
		return closure.getCdr().getCdr().getCdr();
	}

	/**
	 * Discerns whether or not the given lexeme is an operator type
	 * @l Lexeme to check
	 * @return True if l is an operator
	 */
	private static boolean isOperator(Lexeme l)
	{
		switch (l.getType())
		{
			case PLUS:
			case MINUS:
			case TIMES:
			case DIVIDE:
			case INCREMENT:
			case DECREMENT:
			case GREATER_THAN:
			case LESS_THAN:
			case EQUAL_TO:
			case GT_EQUAL:
			case LT_EQUAL:
			case MODULO:
			case PLUS_EQUAL:
			case MINUS_EQUAL:
				return true;
		}

		return false;
	}
	
	/**
	 * Discerns if the given Lexeme tree holds a function call
	 * @tree Root of tree to test
	 * @return If the given tree represents a function call
	 */
	private boolean isFunctionCall(Lexeme tree)
	{
		if (tree.getCdr().getType() == EXPRLIST)
			return true;
		return false;
	}

	/**
	 * Discerns if the given Lexeme tree holds a method call
	 * @tree Root of the tree to test
	 * @return If the given tree represents a method call
	 */
	private boolean isMethodCall(Lexeme tree)
	{
		if (tree.getCdr().getType() == GLUE)
		{
			if (tree.getCdr().getCar().getType() == ID)
				return true;
		}
		return false;
	}

	/**
	 * Determines if the function Lexeme is a built-in function
	 * @function The function in question
	 * @return True if the function is built-in
	 */
	private boolean isBuiltIn(Lexeme function)
	{
		if (function.getName() != "print" && function.getName() != "destroy")
			return false;
		return true;
	}


/***** Private Methods *****/
	/**
	 * Gets the value of the ID at tree root from the environment
	 * @tree Lexeme for which to search environment
	 * @env Environment for which to search for Lexeme id
	 * @return The value of the id at tree
	 */
	private Lexeme lookup(Lexeme tree, Environment env)
	{
		return env.getVal(tree);
	}

	/**
	 * Adds variable value to its scope
	 */													//FIXME: come back!!!!!!!!!!!!!!!!!!!!!!!!!
	private Lexeme evalVarDef(Lexeme tree, Environment env)
	{
		return null;
	}

	/**
	 * Adds function variables to its scope
	 * @tree Root of FUNCDEF tree to use for environment insertion
	 * @env The environment into which the newly created tree will be inserted
	 * @return The inserted Lexeme
	 */
	private Lexeme evalFuncDef(Lexeme tree, Environment env)
	{
		Lexeme closure = cons(CLOSURE, env.getEnv(), tree);
		env.insertEnv(tree.getCar(), closure);
		return closure;
	}

	/**
	 * Evaluates a block parse tree
	 * @tree Root of the BLOCK tree to use for evaluation
	 * @env Corresponding environment
	 * @return The evaluated result of the block in Lexeme form
	 */
	private void evalBlock(Lexeme tree, Environment env)
	{
		if (tree.getCar() != null) eval(tree.getCar(), env);		//FIXME: need to figure this out
	}

	/**
	 * Adds class variables to its scope
	 * @tree Root of CLASSDEF tree to use for environmenet insertion
	 * @env The environment into which the newly created tree will be inserted
	 * @return The inserted Lexeme
	 */
	private Lexeme evalClassDef(Lexeme tree, Environment env)
	{
		return env.insertEnv(tree.getCar(), cons(OCLOSURE, env.getEnv(), tree));	//FIXME: is this needed/does it need to be fixed?
	}

	/**
	 * Evaluates an operation between unaries and/or expressions
	 * @tree Root of the EXPRDEF
	 * @env The corresponding environment
	 * @return A new Lexeme with the value of the result of the op
	 */
	private Lexeme evalOp(Lexeme tree, Environment env)
	{
		String operandType = tree.getCar().getType();
		String operatorType = tree.getCdr().getCar().getType();

		Lexeme arg0 = eval(tree.getCar(), env);
		Lexeme arg1 = eval(tree.getCdr().getCdr(), env);

		if (operandType == INTEGER)
		{
			int a0 = arg0.getInt();
			int a1 = arg1.getInt();

			if (operatorType == PLUS)
				return new Lexeme(operandType, a0+a1);
			else if (operatorType == MINUS)
				return new Lexeme(operandType, a0-a1);
			else if (operatorType == TIMES)
				return new Lexeme(operandType, a0*a1);
			else if (operatorType == DIVIDE)
				return new Lexeme(operandType, a0/a1);
			else if (operatorType == INCREMENT)
				return new Lexeme (operandType, a0+1);
			else if (operatorType == DECREMENT)
				return new Lexeme(operandType, a0-1);
			else if (operatorType == GREATER_THAN)
				return new Lexeme(operandType, a0>a1);		// Returns 0 or 1 based on bool
			else if (operatorType == LESS_THAN)
				return new Lexeme(operandType, a0<a1);
			else if (operatorType == EQUAL_TO)
				return new Lexeme(operandType, a0 == a1);
			else if (operatorType == GT_EQUAL)
				return new Lexeme(operandType, a0>=a1);
			else if (operatorType == LT_EQUAL)
				return new Lexeme(operandType, a0<=a1);
			else if (operatorType == MODULO)
				return new Lexeme(operandType, a0%a1);
			else if (operatorType == PLUS_EQUAL)
				return new Lexeme(operandType, a0+=a1);
			else if (operatorType == MINUS_EQUAL)
				return new Lexeme(operandType, a0-=a1);
			else
				return new Lexeme(UNKNOWN);
		}
		else if (operandType == REAL)
		{
			double a0 = arg0.getReal();
			double a1 = arg1.getReal();

			if (operatorType == PLUS)
				return new Lexeme(operandType, a0+a1);
			else if (operatorType == MINUS)
				return new Lexeme(operandType, a0-a1);
			else if (operatorType == TIMES)
				return new Lexeme(operandType, a0*a1);
			else if (operatorType == DIVIDE)
				return new Lexeme(operandType, a0/a1);
			else if (operatorType == INCREMENT)
				return new Lexeme (operandType, a0+1);
			else if (operatorType == DECREMENT)
				return new Lexeme(operandType, a0-1);
			else if (operatorType == GREATER_THAN)
				return new Lexeme(operandType, a0>a1);		// Returns 0 or 1 based on bool
			else if (operatorType == LESS_THAN)
				return new Lexeme(operandType, a0<a1);
			else if (operatorType == EQUAL_TO)
				return new Lexeme(operandType, a0 == a1);
			else if (operatorType == GT_EQUAL)
				return new Lexeme(operandType, a0>=a1);
			else if (operatorType == LT_EQUAL)
				return new Lexeme(operandType, a0<=a1);
			else if (operatorType == MODULO)
				return new Lexeme(operandType, a0%a1);
			else if (operatorType == PLUS_EQUAL)
				return new Lexeme(operandType, a0+=a1);
			else if (operatorType == MINUS_EQUAL)
				return new Lexeme(operandType, a0-=a1);
			else
				return new Lexeme(UNKNOWN);
		}

		return null;				// This was placed here to circumvent error. If time replace all those returns with assignments and finish with one return
	}

	/**
	 * Evaluates the constructor of a function/method call			FIXME: I think...?
	 * @closure Parent closure of the constructor
	 * @env Corresponding environment
	 * @return The extended environment
	 */
	private Lexeme evalConstructor(Lexeme closure, Environment env)
	{
		Environment senv = new Environment(closure.getCar());
		Environment xenv = new Environment(senv.extendEnv(null, null));
		Lexeme body = closure.getCdr().getCdr();
		eval(body, xenv);
		return xenv.getEnv();
	}

	/**
	 * Evaluates the arguments as a helper to evalFunctionCall
	 * @args Root of args to be evaluated
	 * @env The corresponding environment
	 * @return Root of new tree with args and their corresponding params		//FIXME: this might be wrong
	 */
	private Lexeme evalArgs(Lexeme args, Environment env)
	{
		if (args == null)
			return null;
		else
			return cons(GLUE, eval(args.getCar(), env), evalArgs(args.getCdr(), env));
	}

	/**
	 * Evaluates the actual use of a functioncall
	 * @tree Root of function tree to use for evaluation
	 * @env Environment corresponding to the function call in tree
	 * @return Root Lexeme
	 */
	private Lexeme evalFunctionCall(Lexeme tree, Environment env)
	{
		Lexeme closure = lookup(tree.getCar(), env);		//FIXME: eval may be returning a value in the closure env, I believe it should return the closure itself
		Lexeme args = evalArgs(tree.getCdr(), env);
//		if (isBuiltIn(closure)) return evalBuiltIn(closure, args);	//FIXME: uncomment, need to write ebuiltin
		Environment senv = new Environment(closure.getCar());
		Lexeme params = getParams(closure);
		Environment lenv = new Environment(senv.extendEnv(params, args));
		Lexeme body = getBody(closure);

		return eval(body, lenv);
	}
/*
	private Lexeme evalMethodCall(Lexeme tree, Environment env)
	{
		Lexeme closure = eval(tree.getCar(), env);
	}
*/
	/**
	 * Main evaluator method
	 * @tree Root of the tree to be evaluated
	 * @env Corresponding environment to tree
	 * @return root of another Lexeme tree
	 */
	private Lexeme eval(Lexeme tree, Environment env)
	{
// System.out.println("flag");
		switch (tree.getType())
		{
			case PROG:
			case DEF:
				return eval(tree.getCar(), env);
			case INTEGER://FIXME: continue to edit these as the trees that can simply return the root lexeme become more clear
			case REAL:
			case STRING:
				return tree;
			case ID:
				return lookup(tree, env);
//			case VARDEF:
//				return evalVarDef(tree, env);
			case FUNCDEF:
				return evalFuncDef(tree, env);
			case BLOCK:
//				return evalBlock(tree, env);			//FIXME: implement evalBlock() and uncomment
			case CLASSDEF:
				return evalClassDef(tree, env);
			case EXPRDEF:
				if (tree.getCdr() != null)
				{
					if (tree.getCdr().getCar().getType() == ASSIGN)
					{
						Lexeme name = tree.getCar();
						Lexeme val = eval(tree.getCdr().getCdr(), env);
						env.updateVal(name, val);
						return val;
					}
					else if (isOperator(tree.getCdr().getCar()))
						return evalOp(tree, env);
				}
				else
					return eval(tree.getCar(), env);
				break;							//In reality, this will never hit. It's here to surpress javac warning
			case UNARY:
				if (tree.getCdr() != null)
				{
//					eval(tree.getCar());		//FIXME: Implement these two calls
//					eval(tree.getCdr());
				}
				else
					return eval(tree.getCar(), env);
				return null;					//FIXME: placeholder null return

			case IDSTART:
				Lexeme ret = null;

				if (isFunctionCall(tree))
					ret = evalFunctionCall(tree, env);
//				else if (isMethodCall(tree))
//					ret = evalMethodCall(tree, env);
//				else
//					ret = evalIDstart(tree, env);
				
				return ret;
		}

		return null;							//FIXME: placeholder null return
	}


/***** Public Methods *****/
	public static void main(String[] args) throws IOException
	{
		checkCmdArgs(args);

		File file = openFile(args[0]);
		PushbackInputStream stream = new PushbackInputStream(new FileInputStream(file));

		Evaluator e = new Evaluator();

		Lexer i = new Lexer(stream);
		Lexeme curr = i.lex();

		Parser p = new Parser(curr, stream);


		Environment env = new Environment();
		Lexeme tree = p.program();
		e.eval(tree, env);

/*
		tree.display();
		tree.getCar().display();
		tree.getCar().getCar().display();
		tree.getCar().getCar().getCar().display();
		tree.getCar().getCar().getCdr().display();
		tree.getCar().getCar().getCdr().getCdr().display();	//block
		tree.getCar().getCar().getCdr().getCdr().getCar().display();	//statements
//		tree.getCar().getCar().getCdr().getCdr().getCar().getCar().display();
		Lexeme statement = tree.getCar().getCar().getCdr().getCdr().getCar().getCar();
		statement.display();
		statement.getCar().display();
		statement.getCar().getCar().display();
		statement.getCar().getCdr().display();
		Lexeme un = statement.getCar().getCdr().getCar();
		un.display();
		un.getCar().display();
//		tree.getCar().getCar().getCdr().getCar().getCar().getCar().getCar().display();
//		tree.getCar().getCar().getCdr().getCar().getCar().getCar().getCar().getCar().display();
//		env.displayEnv(1);
*/

		stream.close();
	}
}


