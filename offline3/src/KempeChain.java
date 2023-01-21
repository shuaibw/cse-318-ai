import java.util.ArrayList;

public class KempeChain {
    public ArrayList<Vertex> chain;
    public int color1;
    public int color2;

    public KempeChain(ArrayList<Vertex> chain, int color1, int color2) {
        this.chain = chain;
        this.color1 = color1;
        this.color2 = color2;
    }
}
