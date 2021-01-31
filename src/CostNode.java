public class CostNode implements ComputationNode {
    private Double crossEntropy(Double x) {
        return -Math.log(x);
    }

    private Double derivative(Double x) {
        return -1.0 / x;
    }

    Double output;
    Double grad;
    ComputationNode a, child;
    Integer id;

    CostNode(ComputationNode c, Integer id) {
        output = null;
        grad = 1.0;
        a = c;
        a.setChild(this);
        child = null;
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }

    public Double forward() {
        if (output == null) {
            //System.out.printf("Arrived at cost node: %f\n", a.forward());
            output = crossEntropy(a.forward());
        }

        return output;
    }

    public void backward() {
        a.setGrad(grad * derivative(a.forward()));
    }

    public void getBackward() {
        throw new IllegalArgumentException("Cannot get backward from last node in network");
    }

    public void setChild(ComputationNode c) {
        if (child != null) throw new IllegalArgumentException("Child already defined");
        child = c;
    }

    public void addInput(ComputationNode c) {
        throw new IllegalArgumentException("Input already defined");
    }

    public void setGrad(Double d) {
        throw new IllegalArgumentException("Cannot modify grad of cost function wrt to itself");
    }

    public void resetGrad() {
        grad = 1.0;
        output = null;
        //System.out.printf("Grad at %d (%c) -> %f\n", this.id(), this.type(), grad);
    }

    public char type() {
        return 'c';
    }

    public static void main(String[] args) {
        InputNode x1 = new InputNode(1.0, 0);
        InputNode x2 = new InputNode(0.0, 1);
        OrGate a = new OrGate(x1, 2);
        a.addInput(x2);
        a.setGrad(0.4763);
        a.backward();

        System.out.println(a.forward());
        System.out.println(x1.resultingGrad());
    }
}
