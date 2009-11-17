package org.drugis.addis.presentation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.RelativeEffect.AxisType;
import org.drugis.addis.plot.BinnedScale;
import org.drugis.addis.plot.ForestPlot;
import org.drugis.addis.plot.IdentityScale;
import org.drugis.addis.plot.LinearScale;
import org.drugis.addis.plot.LogScale;
import org.drugis.common.Interval;


public class ForestPlotPresentation {

	private List<RelativeEffect<?>> d_relEffects;
	private BinnedScale d_scale;
	private double d_max = 0.0;
	private AxisType d_scaleType;
	

	@SuppressWarnings("unchecked")
	public ForestPlotPresentation(List<RelativeEffect<?>> relEffects) throws IllegalArgumentException {
		//Checks for consistent list of Relative Effects
		if (relEffects.isEmpty())
			throw new IllegalArgumentException("List of Relative Effects is Empty upon Constructing a ForestPlotPresentation.");
		
		Endpoint uniqueE = relEffects.get(0).getEndpoint(); 
		Drug base = relEffects.get(0).getBaseline().getPatientGroup().getDrug();
		Drug subject = relEffects.get(0).getSubject().getPatientGroup().getDrug();
		Class<RelativeEffect<?>> a = (Class<RelativeEffect<?>>) relEffects.get(0).getClass();
		for(RelativeEffect<?> r : relEffects) {
			if (!uniqueE.equals(r.getEndpoint()))
				throw new IllegalArgumentException("Relative Effects do not have same Endpoints.");
			if (!base.equals(r.getBaseline().getPatientGroup().getDrug()))
				throw new IllegalArgumentException("Relative Effects do not have same Drugs.");
			if (!subject.equals(r.getSubject().getPatientGroup().getDrug()))
				throw new IllegalArgumentException("Relative Effects do not have same Drugs.");
			if (!r.getClass().equals(a))
				throw new IllegalArgumentException("Relative Effects of different Type.");
		}
		
		for (RelativeEffect<?> i : relEffects) {
			d_max = Math.max(i.getSampleSize(), d_max);
		}
		
		d_relEffects = relEffects;
		if (relEffects.get(0).getAxisType() == AxisType.LINEAR) {
			d_scaleType = AxisType.LINEAR;
			d_scale = new BinnedScale(new LinearScale(getRange()), 1, ForestPlot.BARWIDTH);
		}
		if (relEffects.get(0).getAxisType() == AxisType.LOGARITHMIC) {
			d_scaleType = AxisType.LOGARITHMIC;
			d_scale = new BinnedScale(new LogScale(getRange()), 1, ForestPlot.BARWIDTH);
		}
	}
	
	public int getNumRelativeEffects() {
		return d_relEffects.size();
	}
	
	public RelativeEffect<?> getRelativeEffectAt(int i) {
		return d_relEffects.get(i);
	}
	
	public BinnedScale getScale() {
		return d_scale;
	}
	
	public AxisType getScaleType() {
		return d_scaleType;
	}

	public Interval<Double> getRange() {
		double min = Double.MAX_VALUE;
		double max = Double.MIN_VALUE;
		
		for (int i = 0; i < d_relEffects.size(); ++i) {
			double lowerBound = d_relEffects.get(i).getConfidenceInterval().getLowerBound();
			min = (lowerBound < min) ? lowerBound : min;
			double upperBound = d_relEffects.get(i).getConfidenceInterval().getUpperBound();
			max = (upperBound > max) ? upperBound : max;
		}
		
		if (d_scaleType == AxisType.LINEAR)
			return niceIntervalLinear(min,max);
		if (d_scaleType == AxisType.LOGARITHMIC)
			return niceIntervalLog(min, max);
		
		return new Interval<Double>(min, max);
	}
	
	public Drug getLowValueFavorsDrug() {
		return d_relEffects.get(0).getBaseline().getPatientGroup().getDrug();
	}
	
	public Drug getHighValueFavorsDrug() {
		return d_relEffects.get(0).getSubject().getPatientGroup().getDrug();
	}
	
	public String getStudyLabelAt(int i) {
		return d_relEffects.get(i).getBaseline().getPatientGroup().getStudy().toString();
	}
	
	Interval<Double> niceIntervalLog(double min, double max) {
		double lowersign = Math.floor(anylog(min, 2));
		double uppersign = Math.ceil(anylog(max, 2));
		
		double minM = Math.pow(2,lowersign);
		double maxM = Math.pow(2, uppersign);
		
		return new Interval<Double>(Math.min(0.5, minM), Math.max(2, maxM));	
	}
	
	private Interval<Double> niceIntervalLinear(double min, double max) {
		int sign = getSignificanceLevel(min, max);

		double minM = Math.floor(min / Math.pow(10, sign)) * Math.pow(10, sign);
		double maxM = Math.ceil(max / Math.pow(10, sign)) * Math.pow(10, sign);

		double smallest = Math.pow(10, sign);

		return new Interval<Double>(Math.min(-smallest, minM), Math.max(smallest, maxM));
	}

	private int getSignificanceLevel(double min, double max) {
		int signMax = (int) Math.floor(Math.log10(Math.abs(max)));
		int signMin = (int) Math.floor(Math.log10(Math.abs(min)));
		
		int sign = Math.max(signMax, signMin);
		return sign;
	}
	
	private double anylog(double x, double base) {
		return Math.log(x) / Math.log(base);
	}

	public String getCIlabelAt(int i) {
		RelativeEffect<?> e = d_relEffects.get(i);
		return formatNumber2D(e.getRelativeEffect()) + " (" + formatNumber2D(e.getConfidenceInterval().getLowerBound()) 
									 + ", " + formatNumber2D(e.getConfidenceInterval().getUpperBound()) + ")";
	}
	
	public List<Integer> getTicks() {
		Interval<Double> range = getRange();
		ArrayList<Integer> tickList = new ArrayList<Integer>();
		tickList.add(d_scale.getBin(range.getLowerBound()).bin);
		tickList.add(d_scale.getBin(d_scaleType == AxisType.LOGARITHMIC ? 1 : 0).bin);
		tickList.add(d_scale.getBin(range.getUpperBound()).bin);
		return tickList;
	}
	
	private String formatNumber2D(double x) {
		DecimalFormat df = new DecimalFormat("###0.00");
		return df.format(x);
	}

	public List<String> getTickVals() {
		Interval<Double> range = getRange();
		ArrayList<String> tickVals = new ArrayList<String>();
		DecimalFormat df = new DecimalFormat("####.####");
		tickVals.add(df.format(range.getLowerBound()));
		tickVals.add(d_scaleType == AxisType.LOGARITHMIC ? df.format(1D) : df.format(0D));
		tickVals.add(df.format(range.getUpperBound()));
		return tickVals;
	}
	
	private double getWeightAt(int index) {
		return (double) (d_relEffects.get(index).getSampleSize()) / d_max;
	}

	public int getDiamondSize(int index) {
		double weight = getWeightAt(index);
		BinnedScale tempbin = new BinnedScale(new IdentityScale(), 1, 10);
		return tempbin.getBin(weight).bin * 2 + 1;
	}
}
