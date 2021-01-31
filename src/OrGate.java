/**
 * {@code OrGate} represents an augmented logical OR gate, which takes as inputs Doubles a and b between 0 and 1 and
 * computes:
 * - a v b := a + b - a * b
 * - gradient of output w.r.t to each input:
 * - grad w.r.t a = 1 - b
 * - grad w.r.t b = 1 - a
 */
public class OrGate implements ComputationNode {
    private Double output;
    private Double grad;
    private ComputationNode a, b, child;
    private Integer id;

    /**
     * Initializes an <em>OrGate</em> with one of the inputs being a, and with a given id.
     *
     * @param c
     * @param id
     */
    OrGate(ComputationNode c, Integer id) {
        output = null;
        grad = 0.0;
        a = c;
        a.setChild(this);
        b = null;
        child = null;
        this.id = id;
    }

    /**
     * Runs <em>backward</em> on the child node.
     */
    private void getBackward() {
        if (child == null) throw new NullPointerException("Child not defined");
        child.backward();
    }

    /**
     * Standard <em>ComputationNode</em> methods
     **/

    public Double forward() {
        if (output == null) {
            output = a.forward() + b.forward() - a.forward() * b.forward();
        }

        return output;
    }

    public void backward() {
        if (b == null) throw new IllegalArgumentException("Backpropagation with undefined inputs");
        this.getBackward();
        a.setGrad(grad * (1 - b.forward()));
        b.setGrad(grad * (1 - a.forward())); // will later call "backward" on a and b
    }

    public void addInput(ComputationNode c) {
        if (b != null) throw new IllegalArgumentException("Both inputs already defined");
        b = c;
        b.setChild(this);
    }

    public void setChild(ComputationNode c) {
        if (child != null) throw new IllegalArgumentException("Child already defined");
        child = c;
    }

    public void setGrad(Double d) {
        grad += d;
        //System.out.printf("Grad at %d (%c) -> %f\n", this.id(), this.type(), grad);
    }

    public void resetGrad() {
        grad = 0.0;
        child.resetGrad();
        output = null;
    }

    public char type() {
        return 'v';
    }

    public Integer id() {
        return this.id;
    }
}
