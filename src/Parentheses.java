public class Parentheses implements ComputationNode {
    char type;
    Integer id;

    Parentheses(char c, Integer id) {
        type = c;
        this.id = id;
    }

    public Integer id() {
        return this.id;
    }

    public Double forward() {
        return null;
    }

    public void backward() {

    }

    public void getBackward() {

    }

    public void addInput(ComputationNode c) {

    }

    public void setChild(ComputationNode c) {

    }

    public void setGrad(Double d) {

    }

    public void resetGrad() {

    }

    public char type() {
        return type;
    }
}
