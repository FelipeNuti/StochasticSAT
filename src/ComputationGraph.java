/**
 * Compilation: javac ComputationGraph.java
 * Execution: java ComputationGraph < inputFile
 * Dependencies: (external) java.util.ArrayList, java.util.Scanner, java.util.Stack
 * (internal) ComputationNode, InputNode, SigmoidGate, AndGate, OrGate, NotGate, CostNode
 * Data files: testInput, testInput2, parsedCNF
 * <p>
 * Stochastic SAT solver that builds a logical circuit from a series of logical expressions, and can be used
 * to approximate inputs that make all the logical expressions true.
 */

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;

/**
 * The {@code ComputationGraph} class represents and augmented logical circuit which can take inputs that range from 0
 * to 1. It supports traditional <em>forward</em> propagation to calculate the output of the circuit given an array of inputs,
 * but also supports <em>backward</em> propagation to compute the gradient of the output of the circuit with respect to the inputs.
 * <p>
 * This is to enable a stochastic approach to Boolean Satisfiability. As exemplified in the <em>main</em> method, this API can
 * be used to implement stochastic gradient descent on the inputs of the circuit, aimed at maximizing the truth value of logical
 * expressions.
 * <p>
 * The class takes as input in System.in a list of logical expressions (syntax detailed later) and parses them into a
 * <em>ComputationGraph</em> which takes as inputs Double values (of any size), converts them to numbers between 0 and 1
 * using a <em>SigmoidGate</em>, and feeds the result to an augmented logical circuit. The <em>forward</em> method takes
 * in inputs and outputs the cross-entropy loss of the circuit (defined as minus the log of the output of the circuit), whereas
 * <em>backward</em> can be called **after** <em>forward</em> to obtain the gradients of this loss with respect to each
 * real-valued input.
 *
 * @author Felipe Nuti
 */
public class ComputationGraph {
    InputNode[] varNodes;
    SigmoidGate[] inputNodes;
    CostNode[] costNode;
    Integer n;
    Integer counter;

    /**
     * Computes the cross-entropy cost of the <em>ComputationGraph</em> given real-valued inputs <em>vars</em>.
     *
     * @param vars - array of Doubles, with var[i] corresponding to the value of the $i input variable of the circuit (the truth value
     *             will be obtained by applying the Sigmoid function to these values).
     * @return <em>cost</em> - the cross-entropy loss of the circuit with <em>vars</em> as input.
     */
    public Double forward(Double[] vars) {
        for (int i = 0; i < n; i++) {
            InputNode c = varNodes[i];
            c.updateVar(vars[i]);
        }
        Double cost = 0.0;
        for (ComputationNode c : costNode)
            cost += c.forward();
        return cost;
    }

    /**
     * Computes the gradients of the cross-entropy cost of the circuit on the last <em>forward</em> call with respect to each of the
     * inputs given in that call.
     *
     * @return <em>grads</em>, where grads[i] = derivative of the cost with respect to vars[i], given when calling <em>forward</em>.
     */
    public Double[] backward() {
        Double[] grads = new Double[varNodes.length];
        for (int i = 0; i < varNodes.length; i++) {
            InputNode c = varNodes[i];
            grads[i] = c.resultingGrad();
        }
        for (ComputationNode c : varNodes) {
            c.resetGrad();
        }
        return grads;
    }

    /**
     * Does the character c represent a number?
     *
     * @param c
     * @return true if c represents a number from 0 to 9; false otherwise.
     */
    private boolean isNum(char c) {
        //System.out.printf("isNum called at %c%n", c);
        return (c - '0' >= 0 && c - '0' <= 9);
    }

    /**
     * When the end of a parenthesis is reached or a variable is parsed, this method finishes connecting the logical gates
     * that were waiting for this parenthesis or variable to be parsed.
     *
     * @param ops  - stack of operations left to do in the parsing
     * @param vals - stack of values calculated during parsing
     * @param b    - whether the method should stop when a '(' parenthesis is reached.
     */
    private void connectRemaining(Stack<ComputationNode> ops, Stack<ComputationNode> vals, boolean b) {
        while (!ops.isEmpty() && (b || ops.peek().type() != '(')) {
            ComputationNode computationNode = ops.pop();
            //System.out.printf("Connected %d (%c) to %d (%c)\n", computationNode.id(), computationNode.type(), vals.peek().id(),
            //vals.peek().type());
            computationNode.addInput(vals.pop());
            vals.push(computationNode);
        }
    }

    /**
     * Parses the logical expression in sString into an augmented logical circuit, and connects the output to the jth cost
     * node of the circuit.
     *
     * @param sString
     * @param j
     */
    private void parse(String sString, Integer j) {
        int i = 0;
        char[] s = sString.toCharArray();

        Stack<ComputationNode> ops = new Stack<>();
        Stack<ComputationNode> vals = new Stack<>();

        //System.out.println(sString);

        while (i < s.length) {
            char c = s[i];
            //System.out.println(c);
            if (c == '$') {
                StringBuffer stringBuffer = new StringBuffer();
                c = s[++i];

                while (isNum(c) && i < s.length) {
                    //System.out.println(c);
                    stringBuffer.append(c);
                    if (i + 1 != s.length) c = s[++i];
                    else i++;
                }

                Integer x = Integer.parseInt(stringBuffer.toString());
                if (x >= n) throw new IllegalArgumentException("Variable name out of bounds");
                //if (!ops.isEmpty()) System.out.printf("Type of ops.top is %c\n", ops.peek().type());
                if (ops.size() == 0 || ops.peek().type() == '(') {
                    //System.out.println("Start of sentence");
                    vals.push(inputNodes[x]);
                } else {
                    ops.peek().addInput(inputNodes[x]);
                    vals.push(ops.pop());
                    connectRemaining(ops, vals, false);
                }

                //i++;
            } else if (c == ')') {
                //System.out.println("Arrived at )");
                assert (ops.pop().type() == '(');
                //System.out.println(ops.peek().type());
                ops.pop();
                connectRemaining(ops, vals, false);
                i++;
            } else if (c == '^' || c == 'v') {
                ComputationNode node;
                if (c == '^') node = new AndGate(vals.pop(), counter++);
                else node = new OrGate(vals.pop(), counter++);
                ops.push(node);
                i++;
            } else if (c == '(') {
                //System.out.println("Arrived");
                ops.push(new Parentheses('(', counter++));
                i++;
            } else if (c == '~') {
                ops.push(new NotGate(counter++));
                i++;
            } else i++;
        }
        connectRemaining(ops, vals, false);
        costNode[j] = new CostNode(vals.pop(), counter++);
        assert (vals.size() == 0 && ops.size() == 0);
    }

    /**
     * <em>ComputationGraph</em> constructor. Takes an array of strings <em>s</em>, each representing a logical expression,
     * and an integer <em>n</em> representing the total number of logical variable inputs for these expressions. Parses theses
     * expressions into an augmented logical circuit with as many outputs as there are strings in <em>s</em>.
     * <p>
     * Format of the strings:
     * - The ith variable is denoted by $i (i.e. $0, $1, $10, etc.).
     * - The logical AND operation is denoted by ^ (i.e. $3 ^ $5 represents 3 AND 4).
     * - The logical OR operation is denoted by v (i.e. $0 v $1 represents 3 OR 4).
     * - The logical NOT operation is denoted by ~ (i.e. ~$3 represents NOT 3).
     * - For now, only these operations are supported, but they are sufficient to write any boolean function as a logicaal expression
     * <p>
     * Expression example:
     * $10 v ~($11 ^ ~$12)
     *
     * @param s
     * @param n
     */
    ComputationGraph(ArrayList<String> s, int n) {
        this.n = n;
        this.inputNodes = new SigmoidGate[n];
        this.varNodes = new InputNode[n];
        this.counter = 0;

        for (int i = 0; i < n; i++) {
            varNodes[i] = new InputNode(0.0, counter);
        }

        for (int i = 0; i < n; i++) {
            inputNodes[i] = new SigmoidGate(varNodes[i], counter++);
        }

        this.costNode = new CostNode[s.size()];
        for (int i = 0; i < s.size(); i++) {
            if (!s.get(i).isBlank()) parse(s.get(i), i);
            //System.out.println("Parsed successfully");
        }
    }

    /**
     * Example of use of the <em>ComputationGraph</em> class for Stochastic SAT solving. Produces an ArrayList of logical
     * expressions from System.in, passes them onto the <em>ComputationGraph</em> constructor and initializes an array of
     * <em>vars</em> (i.e. real-number values corresponding to truth values of inputs to the circuit). Then, it uses the
     * <em>forward</em> and <em>backward</em> methods to implement stochastic gradient descent on these variables, with the
     * objective of maximizing the truth values of each of the logical expressions given by the user.
     *
     * @param args
     */

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        int n = sc.nextInt();
        sc.nextLine();
        ArrayList<String> arrS = new ArrayList<>();
        while (sc.hasNextLine()) {
            String line = sc.nextLine();
            if (!line.isBlank()) arrS.add(line);
        }

        ComputationGraph cg = new ComputationGraph(arrS, n);

        Double alpha = 0.1;

        Double[] vars = new Double[n];

        for (int i = 0; i < n; i++) vars[i] = Math.random() * 0.1;

        //System.out.println(cg.forward(vars));

        int t = 4000;

        while (t-- > 0) {
            //System.out.println(cg.forward(vars));
            cg.forward(vars);
            Double[] grads = cg.backward();
            for (int i = 0; i < vars.length; i++) {
                vars[i] += -alpha * grads[i];
            }
        }
        for (int i = 0; i < vars.length; i++) {
            System.out.printf("$%d = %f\n", i, 1 / (1 + Math.exp(-vars[i])));
        }
    }
}
