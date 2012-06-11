package org.drugis.addis.entities.treatment;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;

import org.apache.commons.lang.math.DoubleRange;
import org.apache.commons.math3.util.Precision;

import com.jgoodies.binding.beans.BeanUtils;

public class RangeNode implements DecisionTreeNode {
	
	private static class BoundedInterval { 
		private final DoubleRange d_range;
		private final boolean d_lowerBoundIsOpen;
		private final boolean d_upperBoundIsOpen;
		private DecisionTreeNode node;
		
		public BoundedInterval(DoubleRange range, boolean lowerBoundIsOpen, boolean upperBoundIsOpen) {
			d_range = range;
			d_lowerBoundIsOpen = lowerBoundIsOpen;
			d_upperBoundIsOpen = upperBoundIsOpen;
		}

		public DoubleRange getRange() {
			return d_range;
		}

		public boolean isLowerBoundOpen() {
			return d_lowerBoundIsOpen;
		}

		public boolean isUpperBoundOpen() {
			return d_upperBoundIsOpen;
		}

		public DecisionTreeNode getNode() {
			return node;
		}

		public void setNode(DecisionTreeNode node) {
			this.node = node;
		}
	}
	
	protected final Class<?> d_beanClass;
	protected final String d_propertyName;
	private final ArrayList<BoundedInterval> d_ranges = new ArrayList<BoundedInterval>();
	
	/**
	 * Construct a RangeNode that classifies objects by the given property, which must be numeric.
	 * The specified range is subdivided according to cut-off points (to be specified using {@link #addCutOff(double, boolean)}).
	 * If the to-be-classified object is not of the given type, or the property value is not in the specified range, an exception is raised.
	 * @param beanClass Class of object to be classified.
	 * @param propertyName Property to classify on.
	 * @param lowerBound Lower bound all property values should satisfy.
	 * @param lowerBoundIsOpen True if the lower bound is open (exclusive), false if it is closed (inclusive).
	 * @param upperBound Upper bound all property values should satisfy.
	 * @param upperBoundIsOpen True if the upper bound is open (exclusive), false if it is closed (inclusive).
	 * @param child The initial child node.
	 * @throws IllegalArgumentException If the child is null.
	 */
	public RangeNode(Class<?> beanClass, String propertyName,
			double lowerBound, boolean lowerBoundIsOpen,
			double upperBound, boolean upperBoundIsOpen,
			DecisionTreeNode child) {
		if (child == null) {
			throw new IllegalArgumentException("child may not be null");
		}
		
		d_beanClass = beanClass;
		d_propertyName = propertyName;
		
		d_ranges.add(0, createInterval(lowerBound, upperBound, lowerBoundIsOpen, upperBoundIsOpen));
		d_ranges.get(0).setNode(child);
	}
	
	/**
	 * Add a cut-off value. This splits an existing range in two.
	 * The resulting ranges will be initialized with the child node of the original range.
	 * @param value The cut-off value.
	 * @param isOpenAsLowerBound True if the value should be included in the range
	 * where it is a lower bound, and excluded where it is an upper bound.
	 * @return The index of the range where this cut-off is a lower bound.
	 * @throws IllegalArgumentException If the value does not lie within the specified range for this node, 
	 * or if it is equal to an existing cut-off value.
	 */
	public int addCutOff(double value, boolean isOpenAsLowerBound) {
		int splitIdx = findNodeByValue(value);
		BoundedInterval current = d_ranges.get(splitIdx);
		
		BoundedInterval left = createInterval(current.getRange().getMinimumDouble(), value, current.isLowerBoundOpen(), !isOpenAsLowerBound);
		BoundedInterval right = createInterval(value, current.getRange().getMaximumDouble(), isOpenAsLowerBound, current.isUpperBoundOpen());
		left.setNode(d_ranges.get(splitIdx).getNode());
		right.setNode(d_ranges.get(splitIdx).getNode());

		d_ranges.remove(splitIdx);
		d_ranges.add(splitIdx, left);
		d_ranges.add(splitIdx + 1, right);
		
		return getChildCount() - 1;
	}

	/**
	 * The number of children equals the number of sub-ranges, which is the number of cut-offs + 1.
	 * @return The number of children (sub-ranges).
	 */
	public int getChildCount() {
		return d_ranges.size();
	}
	
	/**
	 * Set the child node for the index-th range.
	 * @param index Index of the range.
	 * @param node Desired child node.
	 * @throws IndexOutOfBoundsException If index < 0 or index >= {@link #getChildCount()}.
	 */
	public void setChildNode(int index, DecisionTreeNode node) {
		d_ranges.get(index).setNode(node);
	}
	
	/**
	 * Get the child node for the index-th range.
	 * @param index Index of the range.
	 * @return The child at that index.
	 */
	public DecisionTreeNode getChildNode(int index) {
		return d_ranges.get(index).getNode();
	}
	
	/**
	 * Get the lower bound of the index-th range.
	 * @param index Index of the range.
	 * @return The lower bound.
	 */
	public double getRangeLowerBound(int index) {
		return d_ranges.get(index).getRange().getMinimumDouble();
	}
	
	/**
	 * Get whether the lower bound of the index-th range is open (exclusive).
	 * @param index Index of the range.
	 * @return True if the lower bound is open (exclusive), false if it is close (inclusive).
	 */
	public boolean isRangeLowerBoundOpen(int index) {
		return d_ranges.get(index).isLowerBoundOpen();
	}

	/**
	 * Get the upper bound of the index-th range.
	 * @param index Index of the range.
	 * @return The upper bound.
	 */
	public double getRangeUpperBound(int index) {
		return d_ranges.get(index).getRange().getMaximumDouble();
	}
	
	/**
	 * Get whether the upper bound of the index-th range is open (exclusive).
	 * @param index Index of the range.
	 * @return True if the upper bound is open (exclusive), false if it is close (inclusive).
	 */
	public boolean isRangeUpperBoundOpen(int index) {
		return d_ranges.get(index).isUpperBoundOpen();
	}

	/**
	 * Classify the given object according to range-subdivision of the relevant property.
	 * @param object Object to classify.
	 * @return The relevant child node.
	 * @throws IllegalArugmentException if the object is not of the expected class, or if the property is
	 * not numeric, or if the property value is not within the range specified for this node.
	 */
	public DecisionTreeNode decide(Object object) {
		try { 
			if(!d_beanClass.isInstance(object)) {
				throw new IllegalArgumentException("Object not of the valid type " + d_beanClass.getName() + "  was: " + object.getClass().getName());
			}
			PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(d_beanClass, d_propertyName);
			try { 
				Double doseValue = (Double)BeanUtils.getValue(object, propertyDescriptor);		
				return getNodeByValue(doseValue);
			} catch (ClassCastException e) {
				throw new IllegalArgumentException("Property was not numeric. but: " + BeanUtils.getValue(object, propertyDescriptor).getClass());
			}  

		} catch (IntrospectionException e) {
			e.printStackTrace();
		} 
		throw new IllegalStateException("Could not decide the fate of " + object.toString());
	}
	
	public boolean isLeaf() {
		return false;
	}

	protected DecisionTreeNode getNodeByValue(Double doseValue) {
		int idx = findNodeByValue(doseValue);
		if(idx == -1) throw new IllegalArgumentException("Value " + doseValue + " not within allowed range");
		return getChildNode(idx);
	}
	
	private int findNodeByValue(Double value) {
		for(int i = 0; i < d_ranges.size(); ++i) { 
			if(d_ranges.get(i).getRange().containsDouble(value)) return i;
		}
		return -1;
	}

	private BoundedInterval createInterval(double lowerBound, double upperBound, boolean isOpenLowerBound, boolean isOpenUpperBound) { 
		 DoubleRange range = new DoubleRange(
				 lowerBound + (!isOpenLowerBound ? Precision.EPSILON : 0), 
				 upperBound	- (!isOpenUpperBound ? Precision.EPSILON : 0));
		 return new BoundedInterval(range, isOpenLowerBound, isOpenUpperBound);
	}
	
}
