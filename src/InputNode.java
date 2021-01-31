/**
 * {@code InputeNode} contains a real-valued number which will be fed into a SigmoidGate, and then onto an augmented
 * logical circuit.
 */

public class InputNode implements ComputationNode {
    Double output;
    Double grad;
    ComputationNode child;
    Integer id;

    /**
     * Initializes an InputNode with output value var and a specified id.
     *
     * @param var
     * @param id
     */
    InputNode(Double var, Integer id) {
        output = var;
        grad = 0.0;
        child = null;
        this.id = id;
    }

    /**
     * Runs <em>backward</em> on all children nodes.
     */
    private void getBackward() {
        if (child == null) throw new NullPointerException("Child not defined");
        child.backward();
    }

    /**
     * Returns the gradient of the cost of a computation graph with respect to the variable stored in the InputNode. This
     * is used to implement stochastic gradient descent on the inputs to a computation graph.
     *
     * @return
     */
    public Double resultingGrad() {
        getBackward();
        return grad;
    }

    /**
     * Updates the value stored in the InputNode. This is used to recycle a ComputationGraph by feeding it different inputs.
     *
     * @param var
     */
    public void updateVar(Double var) {
        output = var;
    }

    /**
     * Standard <em>ComputationNode</em> methods
     **/

    public Double forward() {
        //System.out.println("a");
        return output;
    }

    public void backward() {
        throw new IllegalArgumentException("Cannot call backward from input node");
    }

    public void addInput(ComputationNode c) {
        System.out.println("Cannot add inputs to input node");
        return;
    }

    public void setChild(ComputationNode c) {
        if (child != null) throw new IllegalCallerException("Child already defined");
        child = c;
    }

    public void setGrad(Double d) {
        grad += d;
        //System.out.printf("Grad at %d (%c) -> %f\n", this.id(), this.type(), grad);
    }

    public void resetGrad() {
        grad = 0.0;
        child.resetGrad();
    }

    public char type() {
        return 'i';
    }

    public Integer id() {
        return this.id;
    }
}
