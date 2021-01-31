/**
 * {@code NotGate} computes:
 * - the logical inverse of its input: ~a = 1 - a
 * - gradient of output w.r.t to its only input:
 * - grad(a) = -1
 */
public class NotGate implements ComputationNode {
    Double output;
    Double grad;
    ComputationNode a, child;
    Integer id;

    /**
     * Initializes an <em>NotGate</em> with a given id.
     *
     * @param id
     */
    NotGate(Integer id) {
        output = null;
        grad = 0.0;
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
            output = 1 - a.forward();
        }

        return output;
    }

    public void backward() {
        this.getBackward();
        a.setGrad(-grad);
    }

    public void addInput(ComputationNode c) {
        if (a != null) throw new IllegalArgumentException("Input already defined");
        a = c;
        a.setChild(this);
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
        return '~';
    }

    public Integer id() {
        return this.id;
    }
}
