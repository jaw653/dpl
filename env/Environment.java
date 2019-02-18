/**
 * Author: Jake Wachs
 * CS 403, Programming Languages
 * The University of Alabama
 *
 * Environment Class
 */


package env;

import lex.Lexeme;
import lex.Types;
import lex.Lexer;


public class Environment implements Types
{
    Lexeme env;

    /**
     * Default Constructor, serves as the
     *  newEnvironment() function from class
     */
    public Environment()
    {
        System.out.println("Creating a new environment");
        env = cons(ENV, cons(TABLE, null, null), null);
    }

	/**
	 * Overloaded constructor
	 * @env The root of the premade tree to become env
	 */
	public Environment(Lexeme env)
	{
		this.env = env;
	}

/********** Private Methods **********/
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

/********** Public Methods **********/
    /**
     * Getter method for env Lexeme
     * @return The env Lexeme member
     */
    public Lexeme getEnv()
    {
        return env;
    }

    /**
     * Method for inserting id/val binding into table
     * @env The Lexeme from which to add on to
     * @id The ID of the Lexeme
     * @val The actual held value of the Lexeme identified by ID
     */
    public Lexeme insertEnv(Lexeme id, Lexeme val)
    {
        Lexeme car = env.getCar();
        car.setCar( cons(I, id, car.getCar()) );
        car.setCdr( cons(V, val, car.getCdr()) );

		return val;
    }

    /**
     * Gets the value of the variable with a given name
     * @env The environment to search
     * @id The name of the variable of whom's value is wanted
     * @return A Lexeme with the searched-for id
     */
    public Lexeme getVal(Lexeme id)
    {
        Lexeme env = this.env;
        Lexeme vars;
        Lexeme vals;

        while (env != null)
        {
            vars = (env.getCar()).getCar();
            vals = (env.getCar()).getCdr();

            while (vars != null)
            {
                if ( id.getName().equals(vars.getCar().getName()) )
                    return vals.getCar();

                vars = vars.getCdr();
                vals = vals.getCdr();
            }

            env = env.getCdr();
        }

        System.out.println("Undefined variable error");
        System.exit(-1);

        return null;    // This was just added to surpress the no return error
    }

    /**
     * Sets the value of the variable with a given name
     * @env The environment to search
     * @id The name of the variable of whom's value is wanted
     * @newVal The new value of the variable mentioned above
     */
    public Lexeme updateVal(Lexeme id, Lexeme newVal)
    {
        Lexeme env = this.env;

        Lexeme vars;
        Lexeme vals;

        while (env != null)
        {
            vars = (env.getCar()).getCar();
            vals = (env.getCar()).getCdr();

            while (vars != null)
            {
                if ( id.getName().equals(vars.getCar().getName()) )
                {
                    vals.setCar(newVal);
                    return newVal;
                }

                vars = vars.getCdr();
                vals = vals.getCdr();
            }

            env = env.getCdr();
        }

        return null;
    }

    /**
     * Adds a new scope to the list of environments
     * @env The old environment to which this scope is being added
     * @vars The list of variables of the new scope
	 * @vals The corresponding values of the variables above
     * @return a pointer to the new environment scope
     */
    public Lexeme extendEnv(Lexeme vars, Lexeme vals)
    {
        env = cons(ENV, cons(TABLE, vars, vals), env);
		return env;
    }

    /**
     * Displays the current environment
	 * @envChoice 0 prints only curr env, 1 prints all envs
     */
    public void displayEnv(int envChoice)
    {
        Lexeme env = this.env;

        System.out.println("Environment is:");

        Lexeme vars;
        Lexeme vals;

        while (env != null)
        {
            vars = (env.getCar()).getCar();
            vals = (env.getCar()).getCdr();
            
			while (vars != null)
            {
//                System.out.println("comparing " + id.getName() + " to " + vars.getCar().getName());
                System.out.println("<" + vars.getCar().getName() + "-" + vals.getCar().getInt() + ">");

                vars = vars.getCdr();
                vals = vals.getCdr();
            }

           if (envChoice == 1)
		   {
			   env = env.getCdr();
			   if (env != null)
			   {
				   System.out.println("  |");
				   System.out.println("  V");
			   }
		   }
		   else break;
        }
    }

    public static void main(String[] args)
    {
        /* Init empty environment with only a TABLE */
        Environment env = new Environment();

        env.displayEnv(0);

        String var = "a";
        int val = 1;

        Lexeme id;
        Lexeme value;

        System.out.println("\nTesting insertEnv()...");
        for (int i = 0; i < 3; i++)
        {
            System.out.println("Adding variable " + var + " with value: " + val);

            /* Set the value of the ID Lexeme */
            id = new Lexeme(I, var);

            /* Changing the value of var */
            char tmp = var.charAt(0);
            tmp++;
            var = String.valueOf(tmp);

            /* Set the value Lexeme and then increase for next set */
            value = new Lexeme(V, val++);

            /* Insert id, value pair to env */
            env.insertEnv(id, value);
        }

        env.displayEnv(0);

        System.out.println("\nTesting getVal()...");

        /* Resetting var to the beginning */
        var = "a";
        Lexeme getVal;
        for (int i = 0; i < 3; i++)
        {
            getVal = env.getVal(new Lexeme(I, var));

            System.out.println("ID " + var + " has value: " + getVal.getInt());

            char tmp = var.charAt(0);
            tmp++;
            var = String.valueOf(tmp);
        }

        env.displayEnv(0);

        System.out.println("\nTesting updateVal()...");
        System.out.println("Updating variable a to have value 50");
        env.updateVal(new Lexeme(I, "a"), new Lexeme(V, 50));
        System.out.println("New val of Lexeme with ID a is: " + env.getVal(new Lexeme(I, "a")).getInt() );

        env.displayEnv(0);

        System.out.println("\nUpdating variable c to have value 35");
        env.updateVal(new Lexeme(I, "c"), new Lexeme(V, 35));
        System.out.println("New val of Lexeme with ID c is: " + env.getVal(new Lexeme(I, "c")).getInt() );

        env.displayEnv(0);

        /* Building a list of vars and vals inside of env1 so I don't have to do it by hand */
        System.out.println("\nTesting extend()...");
        Environment env1 = new Environment();
        env1.insertEnv(new Lexeme(I, "var0"), new Lexeme(V, 81));
        env1.insertEnv(new Lexeme(I, "var1"), new Lexeme(V, 82));
        env1.insertEnv(new Lexeme(I, "var2"), new Lexeme(V, 83));
        Lexeme vars = env1.getEnv().getCar().getCar();
        Lexeme vals = env1.getEnv().getCar().getCdr();

		/* Actual testing of the extendEnv() method comes here */
        env.extendEnv(vars, vals);
        env.displayEnv(1);
    }
}
