package main;

import classes.QuasigroupCell;
import utilities.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Hashtable;
import java.util.Scanner;

import java.io.File;
import java.io.IOException;

public class Main {
    private static QuasigroupCell getNextVariable(String orderingHeuristic, QuasigroupCell[][] quasigroup, ArrayList<QuasigroupCell> unassignedCells) {
        int index = -1;
        if(orderingHeuristic.equalsIgnoreCase("SmallestDomainFirst") || orderingHeuristic.equalsIgnoreCase("BrelazHeuristic") || orderingHeuristic.equalsIgnoreCase("BrelazHeuristicLite")) {
            int smallestDomain = Integer.MAX_VALUE;
            for(int i=0; i<unassignedCells.size(); i++) {
                if(unassignedCells.get(i).getAssignedValue() != 0) {
                    continue;
                }
                if(unassignedCells.get(i).getCurrentDomainSize() < smallestDomain) {
                    smallestDomain = unassignedCells.get(i).getCurrentDomainSize();
                    index = i;
                } else if(orderingHeuristic.equalsIgnoreCase("BrelazHeuristic") && unassignedCells.get(i).getCurrentDomainSize()==smallestDomain) {
                    index = (unassignedCells.get(i).getDynamicDegree(quasigroup)>unassignedCells.get(index).getDynamicDegree(quasigroup)? i: index);
                } else if(orderingHeuristic.equalsIgnoreCase("BrelazHeuristicLite") && unassignedCells.get(i).getCurrentDomainSize()==smallestDomain) {
                    index = (unassignedCells.get(i).getDynamicDegree(quasigroup)<unassignedCells.get(index).getDynamicDegree(quasigroup)? i: index);
                }
            }
        } else if(orderingHeuristic.equalsIgnoreCase("MaxStaticDegree") || orderingHeuristic.equalsIgnoreCase("RandomOrdering")) {
            ArrayList<QuasigroupCell> temp = new ArrayList<>(unassignedCells);
            Collections.shuffle(temp);
            for(int i=0; i<temp.size(); i++) {
                if(temp.get(i).getAssignedValue() != 0) {
                    continue;
                }
                index = i;
                break;
            }
            for(int i=0; i<unassignedCells.size(); i++) {
                if(unassignedCells.get(i).getCoordinate().equals(temp.get(index).getCoordinate())) {
                    index = i;
                    break;
                }
            }
        } else if(orderingHeuristic.equalsIgnoreCase("MaxDynamicDegree")) {
            int maxDynamicDegree = Integer.MIN_VALUE;
            for(int i=0; i<unassignedCells.size(); i++) {
                if(unassignedCells.get(i).getAssignedValue() != 0) {
                    continue;
                }
                if(unassignedCells.get(i).getDynamicDegree(quasigroup) > maxDynamicDegree) {
                    maxDynamicDegree = unassignedCells.get(i).getDynamicDegree(quasigroup);
                    index = i;
                }
            }
        } else if(orderingHeuristic.equalsIgnoreCase("MinDynamicDegree")) {
            int minDynamicDegree = Integer.MAX_VALUE;
            for(int i=0; i<unassignedCells.size(); i++) {
                if(unassignedCells.get(i).getAssignedValue() != 0) {
                    continue;
                }
                if(unassignedCells.get(i).getDynamicDegree(quasigroup) < minDynamicDegree) {
                    minDynamicDegree = unassignedCells.get(i).getDynamicDegree(quasigroup);
                    index = i;
                }
            }
        } else if(orderingHeuristic.equalsIgnoreCase("DomDeg") || orderingHeuristic.equalsIgnoreCase("DomDDeg")) {
            double minRatio = Double.MAX_VALUE;
            for(int i=0; i<unassignedCells.size(); i++) {
                if(unassignedCells.get(i).getAssignedValue() != 0) {
                    continue;
                }
                if(orderingHeuristic.equalsIgnoreCase("DomDeg") && (double)(unassignedCells.get(i).getCurrentDomainSize()/unassignedCells.get(i).getStaticDegree())<minRatio) {
                    minRatio = (double)(unassignedCells.get(i).getCurrentDomainSize()/unassignedCells.get(i).getStaticDegree());
                    index = i;
                } else if(orderingHeuristic.equalsIgnoreCase("DomDDeg")) {
                    if(unassignedCells.get(i).getDynamicDegree(quasigroup) == 0) {
                        index = i;
                        break;
                    } else if((double)(unassignedCells.get(i).getCurrentDomainSize()/unassignedCells.get(i).getDynamicDegree(quasigroup)) < minRatio) {
                        minRatio = (double)(unassignedCells.get(i).getCurrentDomainSize()/unassignedCells.get(i).getDynamicDegree(quasigroup));
                        index = i;
                    }
                }
            }
        }
        return unassignedCells.get(index);
    }

    private static boolean revise(QuasigroupCell vi, QuasigroupCell vj) {
        boolean isDeleted = false;
        for(int i=0; i<vi.getDomainSize(); i++) {
            if(vi.getDomainAt(i) == 0) {
                continue;
            }
            boolean isConsistent = false;
            for(int j=0; j<vj.getDomainSize(); j++) {
                if(vj.getDomainAt(j)!=0 && vi.getDomainAt(i)!=vj.getDomainAt(j)) {
                    isConsistent = true;
                    break;
                }
            }
            if(!isConsistent) {
                vi.removeDomainAt(i);
                isDeleted = true;
            }
        }
        return isDeleted;
    }

    private static boolean doConsistencyChecking(String consistencyChecking, QuasigroupCell quasigroupCell, QuasigroupCell[][] quasigroup) {
        LinkedList<Arc> queue = new LinkedList<>();
        Hashtable<Arc, String> hashtable = new Hashtable<>();

        for(int row=0, y=quasigroupCell.getCoordinate().y; row<quasigroup.length; row++) {
            if(consistencyChecking.equalsIgnoreCase("Backtracking") && row!=quasigroupCell.getCoordinate().x && quasigroup[row][y].getAssignedValue()!=0) {
                queue.offer(new Arc(quasigroup[row][y], quasigroupCell));
            } else if(consistencyChecking.equalsIgnoreCase("ForwardChecking") && quasigroup[row][y].getAssignedValue()==0) {
                queue.offer(new Arc(quasigroup[row][y], quasigroupCell));
            } else if(consistencyChecking.equalsIgnoreCase("MaintainingArcConsistency") && quasigroup[row][y].getAssignedValue()==0) {
                Arc arc = new Arc(quasigroup[row][y], quasigroupCell);
                queue.offer(arc);
                hashtable.put(arc, arc.toString());
            }
        }
        for(int col=0, x=quasigroupCell.getCoordinate().x; col<quasigroup[0].length; col++) {
            if(consistencyChecking.equalsIgnoreCase("Backtracking") && col!=quasigroupCell.getCoordinate().y && quasigroup[x][col].getAssignedValue()!=0) {
                queue.offer(new Arc(quasigroup[x][col], quasigroupCell));
            } else if(consistencyChecking.equalsIgnoreCase("ForwardChecking") && quasigroup[x][col].getAssignedValue()==0) {
                queue.offer(new Arc(quasigroup[x][col], quasigroupCell));
            } else if(consistencyChecking.equalsIgnoreCase("MaintainingArcConsistency") && quasigroup[x][col].getAssignedValue()==0) {
                Arc arc = new Arc(quasigroup[x][col], quasigroupCell);
                queue.offer(arc);
                hashtable.put(arc, arc.toString());
            }
        }
        boolean isConsistent = true;

        while(!queue.isEmpty() && isConsistent) {
            Arc arc = queue.poll();
            if(consistencyChecking.equalsIgnoreCase("Backtracking")) {
                isConsistent = !(revise(arc.vi, arc.vj));
            } else if(consistencyChecking.equalsIgnoreCase("ForwardChecking")) {
                if(revise(arc.vi, arc.vj)) {
                    isConsistent = (arc.vi.getCurrentDomainSize()!=0);
                }
            } else if(consistencyChecking.equalsIgnoreCase("MaintainingArcConsistency")) {
                hashtable.remove(arc);
                if(revise(arc.vi, arc.vj)) {
                    for(int row=0, y=arc.vi.getCoordinate().y; row<quasigroup.length; row++) {
                        if(!arc.vi.getCoordinate().equals(quasigroup[row][y].getCoordinate()) && !arc.vj.getCoordinate().equals(quasigroup[row][y].getCoordinate()) && quasigroup[row][y].getAssignedValue()==0) {
                            Arc temp = new Arc(quasigroup[row][y], arc.vi);
                            if(!hashtable.containsKey(temp)) {
                                queue.offer(temp);
                                hashtable.put(temp, temp.toString());
                            }
                        }
                    }
                    for(int col=0, x=arc.vi.getCoordinate().x; col<quasigroup[0].length; col++) {
                        if(!arc.vi.getCoordinate().equals(quasigroup[x][col].getCoordinate()) && !arc.vj.getCoordinate().equals(quasigroup[x][col].getCoordinate()) && quasigroup[x][col].getAssignedValue()==0) {
                            Arc temp = new Arc(quasigroup[x][col], arc.vi);
                            if(!hashtable.containsKey(temp)) {
                                queue.offer(temp);
                                hashtable.put(temp, temp.toString());
                            }
                        }
                    }
                    isConsistent = (arc.vi.getCurrentDomainSize()!=0);
                }
            }
        }
        return isConsistent;
    }

    private static int nodesVisited = -1;
    private static int fails = -1;

    private static boolean doRecursiveBacktracking(String orderingHeuristic, String consistencyChecking, QuasigroupCell[][] quasigroup, ArrayList<QuasigroupCell> unassignedCells) {
        boolean isSolved = true;
        for(QuasigroupCell quasigroupCell: unassignedCells) {
            if(quasigroupCell.getAssignedValue() == 0) {
                isSolved = false;
                break;
            }
        }
        if(isSolved) {
            return true;
        }

        QuasigroupCell quasigroupCell = getNextVariable(orderingHeuristic, quasigroup, unassignedCells);
        int[][] domains = new int[unassignedCells.size()][];
        for(int i=0; i<quasigroupCell.getDomainSize() && !isSolved; i++) {
            if(quasigroupCell.getDomainAt(i) == 0) {
                continue;
            }
            nodesVisited++;
            for(int j=0; j<domains.length; j++) {
                domains[j] = unassignedCells.get(j).copyDomain();
            }

            quasigroupCell.setAssignedValue(quasigroupCell.getDomainAt(i));
            if(doConsistencyChecking(consistencyChecking, quasigroupCell, quasigroup)) {
                isSolved = doRecursiveBacktracking(orderingHeuristic, consistencyChecking, quasigroup, unassignedCells);
            } else {
                fails++;
            }
            if(!isSolved) {
                quasigroupCell.setAssignedValue(0);
                for(int j=0; j<unassignedCells.size(); j++) {
                    unassignedCells.get(j).setDomain(domains[j]);
                }
            }
        }
        return isSolved;
    }

    private static boolean doBacktrackingSearch(String orderingHeuristic, String consistencyChecking, QuasigroupCell[][] quasigroup, ArrayList<QuasigroupCell> unassignedCells) {
        nodesVisited = 1;
        fails = 0;
        return doRecursiveBacktracking(orderingHeuristic, consistencyChecking, quasigroup, unassignedCells);
    }

    private static void runSchemeOn(String inputFileName, String orderingHeuristic, String consistencyChecking) throws IOException {
        int order = -1;
        QuasigroupCell[][] quasigroup = null;
        ArrayList<QuasigroupCell> assignedCells = new ArrayList<>();
        ArrayList<QuasigroupCell> unassignedCells = new ArrayList<>();

        /* reading from input file and preparing containers */
        Scanner scanner = new Scanner(new File("input-data/"+inputFileName));
        for(int i=0; i<2; i++, scanner.nextLine()) {
            if(i == 0) {
                String scannedLine = scanner.nextLine();
                order = Integer.parseInt(scannedLine.substring(2, scannedLine.length()-1));
                quasigroup = new QuasigroupCell[order][order];
            }
        }
        for(int i=0; i<order; i++) {
            String[] temp = scanner.nextLine().split(", ");
            for(int j=0; j<temp.length; j++) {
                quasigroup[i][j] = new QuasigroupCell(new Coordinate(i, j), 2*(order-1));
                quasigroup[i][j].setAssignedValue((j<temp.length-1? Integer.parseInt(temp[j]): Integer.parseInt(temp[j].substring(0, temp[j].indexOf(" ")))));
                if(quasigroup[i][j].getAssignedValue() == 0) {
                    unassignedCells.add(quasigroup[i][j]);
                } else {
                    assignedCells.add(quasigroup[i][j]);
                }
            }
        }
        scanner.close();

        /* finalizing domains of unassigned cells by initial inference */
        for(int i=0, x, y; i<assignedCells.size(); i++) {
            x = assignedCells.get(i).getCoordinate().x;
            y = assignedCells.get(i).getCoordinate().y;
            for(int row=0; row<order; row++) {
                if(quasigroup[row][y].getAssignedValue() == 0) {
                    quasigroup[row][y].removeDomainAt(assignedCells.get(i).getAssignedValue()-1);
                }
            }
            for(int col=0; col<order; col++) {
                if(quasigroup[x][col].getAssignedValue() == 0) {
                    quasigroup[x][col].removeDomainAt(assignedCells.get(i).getAssignedValue()-1);
                }
            }
        }

        /* solving Quasigroup Completion Problem (QCP) */
        boolean isSolved = doBacktrackingSearch(orderingHeuristic, consistencyChecking, quasigroup, unassignedCells);

        /* printing result */
        if(isSolved) {
            System.out.println(inputFileName+" << "+consistencyChecking+"+"+orderingHeuristic+" -> nodesVisited: "+nodesVisited+"; fails: "+fails);
        } else {
            System.out.println(inputFileName+" << "+consistencyChecking+"+"+orderingHeuristic+" -> QCP could not be solved.");
        }
    }

    public static void main(String[] args) throws IOException {
        /* orderingHeuristic: SmallestDomainFirst, MaxStaticDegree, MaxDynamicDegree, MinDynamicDegree, BrelazHeuristic, BrelazHeuristicLite, DomDeg, DomDDeg, RandomOrdering */
        String orderingHeuristic = "SmallestDomainFirst";

        /* consistencyChecking: Backtracking (BT), ForwardChecking (FC), MaintainingArcConsistency (MAC) */
        String consistencyChecking = "Backtracking";
//
//        /* running solution schemes on given input-data */
//        runSchemeOn("d-10-01.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-06.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-07.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-08.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-09.txt.txt", orderingHeuristic, consistencyChecking);
//        System.out.println("");

        orderingHeuristic = "MinDynamicDegree";
        consistencyChecking = "ForwardChecking";
        runSchemeOn("d-10-01.txt.txt", orderingHeuristic, consistencyChecking);
        runSchemeOn("d-10-06.txt.txt", orderingHeuristic, consistencyChecking);
        runSchemeOn("d-10-07.txt.txt", orderingHeuristic, consistencyChecking);
        runSchemeOn("d-10-08.txt.txt", orderingHeuristic, consistencyChecking);
        runSchemeOn("d-10-09.txt.txt", orderingHeuristic, consistencyChecking);
        runSchemeOn("d-15-01.txt.txt", orderingHeuristic, consistencyChecking);
        System.out.println("");
//
//        orderingHeuristic = "DomDeg";
//        consistencyChecking = "MaintainingArcConsistency";
//        runSchemeOn("d-10-01.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-06.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-07.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-08.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-09.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-15-01.txt.txt", orderingHeuristic, consistencyChecking);
//        System.out.println("");
//
//        orderingHeuristic = "BrelazHeuristicLite";
//        consistencyChecking = "MaintainingArcConsistency";
//        runSchemeOn("d-10-01.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-06.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-07.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-08.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-10-09.txt.txt", orderingHeuristic, consistencyChecking);
//        runSchemeOn("d-15-01.txt.txt", orderingHeuristic, consistencyChecking);
    }
}
