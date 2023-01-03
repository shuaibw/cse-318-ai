package utilities;

import classes.QuasigroupCell;

public class Arc {
    public QuasigroupCell vi;
    public QuasigroupCell vj;

    public Arc(QuasigroupCell vi, QuasigroupCell vj) {
        this.vi = vi;
        this.vj = vj;
    }

    @Override
    public String toString() {
        return vi.getCoordinate()+" -> "+vj.getCoordinate();
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) {
            return true;
        }
        if(o==null || !(o instanceof Arc)) {
            return false;
        }
        Arc arc = (Arc) o;
        return (vi.getCoordinate().equals(arc.vi.getCoordinate()) && vj.getCoordinate().equals(arc.vj.getCoordinate()));
    }

    @Override
    public int hashCode() {
        return vi.getCoordinate().hashCode()*10+vj.getCoordinate().hashCode();
    }
}
