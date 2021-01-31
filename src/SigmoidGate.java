/**
 * {@code SigmoidGate} computes:
 * - the sigmoid of its only input: sigmoid(x) = 1/(1+exp(-x))
 * - gradient of output w.r.t to its only input:
 * - grad(x) = sigmoid(x) * (1 - sigmoid(x))
 */

import java.util.ArrayList;

public class SigmoidGate implements ComputationNode {
    private Double output;
    private Double grad;
    private ComputationNode a;
    private ArrayList<ComputationNode> children;
    private Integer id;

    /**
     * Initializes an <em>OrGate</em> with its only input being a, and with a given id.
     *
     * @param c
     * @param id
     */
    SigmoidGate(ComputationNode c, Integer id) {
        output = null;
        grad = 0.0;
        a = c;
        a.setChild(this);
        children = new ArrayList<>();
        this.id = id;
    }

    /**
     * Computes sigmoid(x).
     *
     * @param x
     * @return sigmoid(x)
     */
    private Double sigmoid(Double x) {
        return 1 / (1 + Math.exp(-x));
    }

    /**
     * Runs <em>backward</em> on all children nodes.
     */
    private void getBackward() {
        for (ComputationNode c : children) {
            c.backward();
        }
    }

    /**
     * Standard <em>ComputationNode</em> methods
     **/

    public Double forward() {
        if (output == null) {
            output = sigmoid(a.forward());
        }

        return output;
    }

    public void backward() {
        this.getBackward();
        a.setGrad(grad * forward() * (1 - forward()));
    }

    public void addInput(ComputationNode c) {
        throw new IllegalArgumentException("Input already defined");
    }

    public void setChild(ComputationNode c) {
        children.add(c);
    }

    public void setGrad(Double d) {
        grad += d;
        //System.out.printf("Grad at %d (%c) -> %f\n", this.id(), this.type(), grad);
    }

    public void resetGrad() {
        grad = 0.0;
        for (ComputationNode c : children) c.resetGrad();
        output = null;
    }

    public char type() {
        return 's';
    }

    public Integer id() {
        return this.id;
    }
}
