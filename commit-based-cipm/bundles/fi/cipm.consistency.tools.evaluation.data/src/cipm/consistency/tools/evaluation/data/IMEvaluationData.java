package cipm.consistency.tools.evaluation.data;

import java.util.ArrayList;
import java.util.List;

/**
 * A data structure for the evaluation of the update of the extended IM.
 * 
 * @author Martin Armbruster
 */
public class IMEvaluationData {
    private int numberMatchedIP = -1;
    private int numberAllIP = -1;
    private int numberSIP = -1;
    private int numberAIP = -1;
    private int numberDeactivatedAIP = -1;
    private int numberActivatedAIP = -1;
    private double deactivatedIPAllIPRatio = -1;
    private double deactivatedAIPAllAIPRatio = -1;
    private List<String> unmatchedSEFFElements = new ArrayList<>();
    private int differenceChangedActionsActivatedAIP = -1;

    public int getNumberMatchedIP() {
        return numberMatchedIP;
    }

    public void setNumberMatchedIP(int numberMatchedIP) {
        this.numberMatchedIP = numberMatchedIP;
    }

    public int getNumberAllIP() {
        return numberAllIP;
    }

    public void setNumberAllIP(int numberAllIP) {
        this.numberAllIP = numberAllIP;
    }

    public int getNumberSIP() {
        return numberSIP;
    }

    public void setNumberSIP(int numberSIP) {
        this.numberSIP = numberSIP;
    }

    public int getNumberAIP() {
        return numberAIP;
    }

    public void setNumberAIP(int numberAIP) {
        this.numberAIP = numberAIP;
    }

    public int getNumberDeactivatedAIP() {
        return numberDeactivatedAIP;
    }

    public void setNumberDeactivatedAIP(int numberDeactivatedAIP) {
        this.numberDeactivatedAIP = numberDeactivatedAIP;
    }

    public int getNumberActivatedAIP() {
        return numberActivatedAIP;
    }

    public void setNumberActivatedAIP(int numberActivatedAIP) {
        this.numberActivatedAIP = numberActivatedAIP;
    }

    public double getDeactivatedIPAllIPRatio() {
        return deactivatedIPAllIPRatio;
    }

    public void setDeactivatedIPAllIPRatio(double deactivatedIPAllIPRatio) {
        if (deactivatedIPAllIPRatio == Double.NaN) {
            this.deactivatedAIPAllAIPRatio = -1;
        } else {
            this.deactivatedIPAllIPRatio = deactivatedIPAllIPRatio;
        }
    }

    public double getDeactivatedAIPAllAIPRatio() {
        return deactivatedAIPAllAIPRatio;
    }

    public void setDeactivatedAIPAllAIPRatio(double deactivatedAIPAllAIPRatio) {
        if (deactivatedAIPAllAIPRatio == Double.NaN) {
            this.deactivatedAIPAllAIPRatio = -1;
        } else {
            this.deactivatedAIPAllAIPRatio = deactivatedAIPAllAIPRatio;
        }
    }

    public List<String> getUnmatchedSEFFElements() {
        return unmatchedSEFFElements;
    }

    public int getDifferenceChangedActionsActivatedAIP() {
        return differenceChangedActionsActivatedAIP;
    }

    public void setDifferenceChangedActionsActivatedAIP(int differenceChangedActionsActivatedAIP) {
        this.differenceChangedActionsActivatedAIP = differenceChangedActionsActivatedAIP;
    }
}
