package org.drugis.addis.gui.builder;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import org.drugis.addis.entities.MeanDifference;
import org.drugis.addis.entities.OddsRatio;
import org.drugis.addis.entities.RelativeEffect;
import org.drugis.addis.entities.RiskDifference;
import org.drugis.addis.entities.RiskRatio;
import org.drugis.addis.entities.StandardisedMeanDifference;
import org.drugis.addis.entities.metaanalysis.RandomEffectsMetaAnalysis;
import org.drugis.addis.gui.GUIFactory;
import org.drugis.addis.gui.Main;
import org.drugis.addis.gui.components.RelativeEffectCanvas;
import org.drugis.addis.presentation.RandomEffectsMetaAnalysisPresentation;
import org.drugis.common.gui.ViewBuilder;

import com.jgoodies.binding.adapter.BasicComponentFactory;
import com.jgoodies.forms.builder.PanelBuilder;
import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;

public class RandomEffectsMetaAnalysisView implements ViewBuilder {
	
	private RandomEffectsMetaAnalysisPresentation d_pm;
	private Main d_parent;
	private boolean d_overView;

	public RandomEffectsMetaAnalysisView(RandomEffectsMetaAnalysisPresentation pm, Main parent, boolean overView) {
		d_pm = pm;
		d_parent = parent;
		d_overView = overView;
	}

	public JComponent buildPanel() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		builder.setDefaultDialogBorder();
		
		CellConstraints cc =  new CellConstraints();		

		if (!d_overView) {
			builder.addSeparator("Meta-analysis", cc.xy(1, 1));
			builder.add(GUIFactory.createCollapsiblePanel(buildOverviewPart()), cc.xy(1, 3));

			builder.addSeparator("Included studies", cc.xy(1, 5));
			builder.add(GUIFactory.createCollapsiblePanel(buildStudiesPart()), cc.xy(1, 7));
		}

		switch (d_pm.getAnalysisType()) {
		case RATE:
			buildRatePlotsPart(builder, cc);
			break;
		case CONTINUOUS:
			buildContinuousPlotsPart(builder, cc);
			break;
		default:
			throw new RuntimeException("Unexpected case: " +
					d_pm.getAnalysisType() + " is not a supported type of endpoint");
		}

		return builder.getPanel();
	}

	private void buildContinuousPlotsPart(PanelBuilder builder,
			CellConstraints cc) {
		builder.addSeparator("Mean difference", cc.xy(1, 9));
		
		if (d_overView) {
			builder.add(buildRelativeEffectPart(MeanDifference.class), cc.xy(1, 11));			
		} else {
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(MeanDifference.class)), cc.xy(1, 11));
		}
		
		if (!d_overView) {
			builder.addSeparator("Standardised mean difference", cc.xy(1, 17));
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(StandardisedMeanDifference.class)), cc.xy(1, 19));
		}
	}

	private void buildRatePlotsPart(PanelBuilder builder, CellConstraints cc) {
		builder.addSeparator("Odds ratio", cc.xy(1, 9));
		if (d_overView) {
			builder.add(buildRelativeEffectPart(OddsRatio.class), cc.xy(1, 11));			
		} else {
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(OddsRatio.class)), cc.xy(1, 11));
		}
		
		if (!d_overView) {
			builder.addSeparator("Risk ratio", cc.xy(1, 13));
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(RiskRatio.class)), cc.xy(1, 15));
		
			builder.addSeparator("Risk difference", cc.xy(1, 17));
			builder.add(GUIFactory.createCollapsiblePanel(buildRelativeEffectPart(RiskDifference.class)), cc.xy(1, 19));
		}
	}

	private JComponent buildRelativeEffectPart(Class<? extends RelativeEffect<?>> type) {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		RelativeEffectCanvas canvas = new RelativeEffectCanvas(d_pm.getForestPlotPresentation(type));
		builder.add(canvas, cc.xy(1, 1));
		builder.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.black, 1), BorderFactory.createEmptyBorder(1, 1, 1, 1)));
		builder.setBackground(Color.white);
		
		return builder.getPanel();	
	}

	private JComponent buildStudiesPart() {
		FormLayout layout = new FormLayout(
				"pref:grow:fill",
				"p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
				
		builder.add(GUIFactory.buildStudyPanel(d_pm, d_parent), cc.xy(1, 1));
		
		return builder.getPanel();
	}

	private JComponent buildOverviewPart() {
		FormLayout layout = new FormLayout(
				"pref, 3dlu, pref:grow:fill",
				"p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p, 3dlu, p");
		
		PanelBuilder builder = new PanelBuilder(layout);
		CellConstraints cc =  new CellConstraints();
		
		builder.addLabel("ID:", cc.xy(1, 1));
		builder.add(BasicComponentFactory.createLabel(d_pm.getModel(RandomEffectsMetaAnalysis.PROPERTY_NAME)),
				cc.xy(3, 1));
		
		builder.addLabel("Type:", cc.xy(1, 3));
		builder.addLabel("DerSimonian-Laird random effects inverse variance model", cc.xy(3, 3));
				
		builder.addLabel("Indication:", cc.xy(1, 5));
		builder.add(BasicComponentFactory.createLabel(d_pm.getIndicationModel().getLabelModel()),
				cc.xy(3, 5));

		builder.addLabel("Endpoint:", cc.xy(1, 7));
		builder.add(BasicComponentFactory.createLabel(d_pm.getOutcomeMeasureModel().getLabelModel()),
				cc.xy(3, 7));
		
		builder.addLabel("First drug:", cc.xy(1, 9));
		builder.add(BasicComponentFactory.createLabel(d_pm.getFirstDrugModel().getLabelModel()),
				cc.xy(3, 9));
		
		builder.addLabel("Second drug:", cc.xy(1,11));
		builder.add(BasicComponentFactory.createLabel(d_pm.getSecondDrugModel().getLabelModel()),
				cc.xy(3,11));
		
		return builder.getPanel();
	}

}
