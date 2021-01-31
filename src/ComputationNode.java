/**
 * Interface for <em>ComputationNode</em> type used in <em>ComputationGraph</em> to represent augmented logical circuits.
 */
public interface ComputationNode {
    /**
     * Implements a forward propagation step. Computes a function (which depends on the node type) based on the inputs
     * of the computation nodes and returns it. This enables a recursive implementation of forward propagation on a
     * Computation Graph
     *
     * @return
     */
    public Double forward();

    /**
     * Runs backward propagation on the node's children (i.e. nodes for which it is an input) and returns a function of the
     * values returned by these children. This enables a recursive implementation of backward propagation on a Computation
     * Graph.
     */
    public void backward();

    /**
     * Adds a <em>ComputationNode</em> c as an input to the computation node it is called on, and sets the current node
     * as a child of c.
     *
     * @param c
     */
    public void addInput(ComputationNode c);

    /**
     * Sets a <em>ComputationNode</em> c as a child of the current computation node.
     *
     * @param c
     */
    public void setChild(ComputationNode c);

    /**
     * Increments the gradient of the current node by d.
     *
     * @param d
     */
    public void setGrad(Double d);

    /**
     * Resets the gradient field of the current node to 0, resets its output to Null, and calls resetGrad on the node's
     * child(ren). This is used to "clean" the <em>ComputationGraph</em> in between propagations.
     */
    public void resetGrad();

    /**
     * Returns a character representing the type of the computation node it was called on.
     * - i: input node
     * - s: sigmoid gate
     * - v: OR gate
     * - ^: AND gate
     * - ~: NOT gate
     * - c: cost node
     *
     * @return
     */
    public char type();

    /**
     * Returns an identification number of the node. This is for debugging purposes.
     *
     * @return
     */
    public Integer id();
}
