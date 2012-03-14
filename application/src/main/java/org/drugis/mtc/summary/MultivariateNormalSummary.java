package org.drugis.mtc.summary;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.correlation.StorelessCovariance;
import org.drugis.common.beans.AbstractObservable;
import org.drugis.mtc.MCMCResults;
import org.drugis.mtc.MCMCResultsEvent;
import org.drugis.mtc.MCMCResultsListener;
import org.drugis.mtc.Parameter;

public class MultivariateNormalSummary extends AbstractObservable implements MCMCResultsListener, Summary {

	private static final Mean s_mean = new Mean();

	public static final String PROPERTY_MEAN_VECTOR = "meanVector"; 
	public static final String PROPERTY_COVARIANCE_MATRIX = "covarianceMatrix";

	private final MCMCResults d_results;
	private final Parameter[] d_parameters;
	private double[][] d_covMatrix;
	private double[] d_means;
	private boolean d_isDefined = false;

	public MultivariateNormalSummary(MCMCResults results, Parameter[] parameters) {
		d_results = results;
		d_parameters = parameters;
		d_means = new double[getParameters().length];
		d_covMatrix = new double[getParameters().length][getParameters().length]; 
		calculateResults();
		d_results.addResultsListener(this);
	}


	public Parameter[] getParameters() {
		return d_parameters;
	}

	@Override
	public boolean getDefined() {
		return d_isDefined;
	}
	@Override
	public void resultsEvent(MCMCResultsEvent event) {
		calculateResults();
	}
	
	public double[] getMeanVector() {
		return d_means;
	}
	
	public double[][] getCovarianceMatrix() {
		return d_covMatrix;	
	}

	private boolean isReady() {
		return d_results.getNumberOfSamples() >= 4;
	}

	private void calculateResults() {
		if (!isReady()) {
			return;
		}
		List<List<Double>> sampleCache = new ArrayList<List<Double>>();
		for (int i = 0; i < getParameters().length; ++i) {
			List<Double> samples = SummaryUtil.getAllChainsLastHalfSamples(d_results, getParameters()[i]);
			sampleCache.add(samples);
			d_means[i] = SummaryUtil.evaluate(s_mean, samples);
		}
		StorelessCovariance cov = new StorelessCovariance(getParameters().length);
		double[] rowData = new double[getParameters().length];
		for (int row = 0; row < sampleCache.get(0).size(); ++row) {
			for (int col = 0; col < getParameters().length; ++col) {
				rowData[col] = sampleCache.get(col).get(row);
			}
			cov.increment(rowData);
		}
		d_covMatrix = cov.getData();
		d_isDefined = true;
		firePropertyChange(PROPERTY_DEFINED, null, d_isDefined);
		firePropertyChange(PROPERTY_MEAN_VECTOR, null, d_means);
		firePropertyChange(PROPERTY_COVARIANCE_MATRIX, null, d_covMatrix);
	}
}
