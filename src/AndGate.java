/**
 * {@code AndGate} represents an augmented logical AND gate, which takes as inputs Doubles a and b between 0 and 1 and
 * computes:
 * - a ^ b := a * b
 * - gradient of output w.r.t to each input:
 * - grad w.r.t a = b
 * - grad w.r.t b = a
 */
public class AndGate implements ComputationNode {
    private Double output;
    private Double grad;
    private ComputationNode a, b;
    private ComputationNode child;
    private Integer id;

    /**
     * Initializes an <em>AndGate</em> with one of the inputs being a, and with a given id.
     *
     * @param c
     * @param id
     */
    AndGate(ComputationNode c, Integer id) {
        output = null;
        grad = 0.0;
        a = c;
        a.setChild(this);
        b = null;
        child = null;
        this.id = id;
        //System.out.printf("Connected gate %d (%c) to gate %d (%c)\n", c.id(), c.type(), this.id(), this.type());
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
            output = a.forward() * b.forward();
        }

        return output;
    }

    public void backward() {
        getBackward();
        a.setGrad(grad * b.forward());
        b.setGrad(grad * a.forward()); // will later call "backward" on a and b
    }

    public void addInput(ComputationNode c) {
        if (b != null) {
            System.out.printf("Tried to connect gate %d (%c) to gate %d but gate %d (%c) was already connected\n",
                    c.id(), c.type(),
                    this.id(),
                    b.id(), b.type());
            throw new IllegalArgumentException("Both inputs already defined");
        }
        b = c;
        b.setChild(this);
    }

    public void setChild(ComputationNode c) {
        if (child != null) {
            throw new IllegalArgumentException("Child already defined");
        }
        child = c;
    }

    public void setGrad(Double d) {
        grad += d;
        //System.out.printf("Grad at %d (%c) -> %f\n", this.id(), this.type(), grad);
    }

    public void resetGrad() {
        grad = 0.0;
        output = null;
        child.resetGrad();
    }

    public char type() {
        return '^';
    }

    public Integer id() {
        return this.id;
    }
}
