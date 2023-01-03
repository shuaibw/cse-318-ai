package classes;

import utilities.Coordinate;

public class QuasigroupCell {
    private Coordinate coordinate;
    private int assignedValue;
    private int[] domain;
    private int staticDegree;

    public QuasigroupCell(Coordinate coordinate, int staticDegree) {
        this.coordinate = coordinate;
        this.staticDegree = staticDegree;

        domain = new int[staticDegree/2+1];
        for(int i=0; i<domain.length; i++) {
            domain[i] = i+1;
        }
    }

    public Coordinate getCoordinate() {
        return coordinate;
    }

    public void setAssignedValue(int assignedValue) {
        this.assignedValue = assignedValue;
        if(assignedValue != 0) {
            for(int i=0; i<domain.length; i++) {
                domain[i] = (i+1==assignedValue? assignedValue: 0);
            }
        }
    }

    public int getAssignedValue() {
        return assignedValue;
    }

    public int getDomainSize() {
        return domain.length;
    }

    public void removeDomainAt(int index) {
        domain[index] = 0;
    }

    public int getDomainAt(int index) {
        return domain[index];
    }

    public int getStaticDegree() {
        return staticDegree;
    }

    public int getCurrentDomainSize() {
        int currentDomainSize = 0;
        for(int value: domain) {
            currentDomainSize += (value!=0? 1: 0);
        }
        return currentDomainSize;
    }

    public int getDynamicDegree(QuasigroupCell[][] quasigroup) {
        /* aka getForwardDegree() */
        int dynamicDegree = 0;
        for(int row=0; row<quasigroup.length; row++) {
            if(row!=coordinate.x && quasigroup[row][coordinate.y].assignedValue==0) {
                dynamicDegree++;
            }
        }
        for(int col=0; col<quasigroup[0].length; col++) {
            if(col!=coordinate.y && quasigroup[coordinate.x][col].assignedValue==0) {
                dynamicDegree++;
            }
        }
        return dynamicDegree;
    }

    public void setDomain(int[] domain) {
        this.domain = domain;
    }

    public int[] copyDomain() {
        int[] copy = new int[domain.length];
        System.arraycopy(domain, 0, copy, 0, copy.length);
        return copy;
    }

    @Override
    public String toString() {
        return assignedValue+"";
    }
}
