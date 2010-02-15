package org.drugis.addis.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.drugis.addis.ExampleData;
import org.drugis.addis.entities.Domain;
import org.drugis.addis.entities.DomainImpl;
import org.drugis.addis.entities.Drug;
import org.drugis.addis.entities.Endpoint;
import org.drugis.addis.entities.Indication;
import org.drugis.addis.presentation.AbstractListHolder;
import org.drugis.addis.presentation.StudyGraphPresentation;
import org.drugis.addis.presentation.UnmodifiableHolder;
import org.drugis.addis.presentation.StudyGraphPresentation.Edge;
import org.drugis.addis.presentation.StudyGraphPresentation.Vertex;
import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.VertexView;
import org.jgrapht.event.GraphEdgeChangeEvent;
import org.jgrapht.event.GraphListener;
import org.jgrapht.event.GraphVertexChangeEvent;
import org.jgrapht.ext.JGraphModelAdapter;

import com.jgraph.components.labels.CellConstants;
import com.jgraph.components.labels.MultiLineVertexRenderer;
import com.jgraph.components.labels.MultiLineVertexView;
import com.jgraph.layout.JGraphFacade;
import com.jgraph.layout.graph.JGraphSimpleLayout;

@SuppressWarnings("serial")
public class TestStudyGraph extends JFrame {
	private StudyGraphPresentation d_pm;
	
	@SuppressWarnings("unchecked")
	private JGraphModelAdapter d_model;
	
	public TestStudyGraph(StudyGraphPresentation pm) {
		super("Test for Drug/Study graph");
		d_pm = pm;
		
//		
//		d_model = new JGraphModelAdapter<Vertex, Edge>(d_pm); // if the pm changes, a new model adapter must be made!!
//
//		// create attribute map defining cell view behaviour
//		AttributeMap vertexAttributes = new AttributeMap();
//		CellConstants.setVertexShape(vertexAttributes, MultiLineVertexRenderer.SHAPE_CIRCLE);
//		CellConstants.setBackground(vertexAttributes, Color.WHITE);
//		CellConstants.setForeground(vertexAttributes, Color.BLACK);
//		CellConstants.setBorder(vertexAttributes, BorderFactory.createLineBorder(Color.BLACK, 2));
//		d_model.setDefaultVertexAttributes(vertexAttributes);
//		
//		// the graph layout cache 
//		GraphLayoutCache layoutCache = new GraphLayoutCache(d_model, new MyFactory());
//		jgraph = new JGraph(d_model, layoutCache);
//		jgraph.setAntiAliased(true);
////		jgraph.setAutoResizeGraph(true);
//		jgraph.setPreferredSize(new Dimension(450, 450));
//		jgraph.setSize(new Dimension(450, 450));
//		jgraph.setEnabled(false);
//		jgraph.setEditable(false);
//		getContentPane().add(jgraph);
//		
		layoutGraph();
	
		d_pm.addGraphListener(new GraphListener<Vertex, Edge>() {
			public void vertexRemoved(GraphVertexChangeEvent<Vertex> e) {
				System.out.println("Vertex removed");
				layoutGraph();
			}
			public void vertexAdded(GraphVertexChangeEvent<Vertex> e) {
				System.out.println("Vertex added");
				layoutGraph();
			}
			public void edgeRemoved(GraphEdgeChangeEvent<Vertex, Edge> e) {
				System.out.println("Edge removed");
				layoutGraph();
			}
			public void edgeAdded(GraphEdgeChangeEvent<Vertex, Edge> e) {
				System.out.println("Edge added");
				layoutGraph();
			}
		}); 
	}
	
	JGraph jgraph;


	public void layoutGraph() {
		d_model = new JGraphModelAdapter<Vertex, Edge>(d_pm); // if the pm changes, a new model adapter must be made!!	
		
		// create attribute map defining cell view behaviour
		AttributeMap vertexAttributes = new AttributeMap();
		CellConstants.setVertexShape(vertexAttributes, MultiLineVertexRenderer.SHAPE_CIRCLE);
		CellConstants.setBackground(vertexAttributes, Color.WHITE);
		CellConstants.setForeground(vertexAttributes, Color.BLACK);
		CellConstants.setBorder(vertexAttributes, BorderFactory.createLineBorder(Color.BLACK, 2));
		d_model.setDefaultVertexAttributes(vertexAttributes);
		
		// the graph layout cache 
		GraphLayoutCache layoutCache = new GraphLayoutCache(d_model, new MyFactory());
		if(jgraph != null){
			System.out.println("jgraph not null");
			remove(jgraph);
		}
		jgraph = new JGraph(d_model, layoutCache);
		jgraph.setAntiAliased(true);
//		jgraph.setAutoResizeGraph(true);
//		jgraph.setPreferredSize(new Dimension(450, 450));
//		jgraph.setSize(new Dimension(450, 450));
		jgraph.setEnabled(false);
		jgraph.setEditable(false);
		getContentPane().add(jgraph);
		
		System.out.println(jgraph.getSize());
		final JGraphSimpleLayout layout = new JGraphSimpleLayout(JGraphSimpleLayout.TYPE_CIRCLE);
		final JGraphFacade facade = new JGraphFacade(jgraph);
		layout.run(facade);
		
		// Obtain a map of the resulting attribute changes from the facade
		Map nested = facade.createNestedMap(true, true);
		// Apply the results to the actual graph
		jgraph.getGraphLayoutCache().edit(nested);
		jgraph.repaint();
	}

	public static void main(String[] args) {
		Domain domain = new DomainImpl();
		ExampleData.initDefaultData(domain);
		final ArrayList<Drug> d_drugs = new ArrayList<Drug>();
		d_drugs.add(ExampleData.buildDrugFluoxetine());
		d_drugs.add(ExampleData.buildDrugParoxetine());
		d_drugs.add(ExampleData.buildDrugSertraline());
		MutableDrugListHolder drugs = new MutableDrugListHolder(new ArrayList<Drug>());
		//MutableDrugListHolder drugs = new MutableDrugListHolder(d_drugs);
		StudyGraphPresentation pm =
			new StudyGraphPresentation(new UnmodifiableHolder<Indication>(ExampleData.buildIndicationDepression()),
				new UnmodifiableHolder<Endpoint>(ExampleData.buildEndpointHamd()),
					drugs, domain);
		

		TestStudyGraph frame = new TestStudyGraph(pm);
		frame.setSize(500, 500);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();	
		frame.setVisible(true);
//		drugs.setValue(d_drugs);
		
//		jgraph.setEnabled(false);
//		drugs.setValue(d_drugs);
		frame.getContentPane().add(frame.jgraph);
		drugs.setValue(d_drugs);

//		((TestStudyGraph)frame).jgraph.getGraphLayoutCache().reload();
//		((TestStudyGraph)frame).jgraph.repaint();
//		((TestStudyGraph)frame).layoutGraph();
	}
	
	public class MyFactory extends DefaultCellViewFactory {
		protected VertexView createVertexView(Object cell) {
			if (cell instanceof DefaultGraphCell) {
				MultiLineVertexView multiLineVertexView = new MultiLineVertexView(cell);
				AttributeMap map = new AttributeMap(d_model.getDefaultVertexAttributes());
				multiLineVertexView.setAttributes(map);
				return multiLineVertexView;
			}
			return super.createVertexView(cell);
		}
	}
	
	public static class MutableDrugListHolder extends AbstractListHolder<Drug> {
		private List<Drug> d_drugs;
		
		public MutableDrugListHolder(List<Drug> drugs) {
			d_drugs = new ArrayList<Drug>(drugs);
		}

		@Override
		public List<Drug> getValue() {
			return d_drugs;
		}
		
		@SuppressWarnings("unchecked")
		@Override 
		public void setValue(Object o) {
			setValue((List<Drug>)o);
		}
		
		public void setValue(List<Drug> drugs) {
			List<Drug> oldValue = d_drugs;
			d_drugs = new ArrayList<Drug>(drugs);
			fireValueChange(oldValue, d_drugs);
		}
		
	}
}
