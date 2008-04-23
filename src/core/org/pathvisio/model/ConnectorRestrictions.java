package org.pathvisio.model;

import java.awt.Shape;
import java.awt.geom.Point2D;

/**
 * Methods to provide restrictions for the connector path
 * @author thomas
 *
 */
public interface ConnectorRestrictions {
	public final static int SIDE_NORTH = 0;
	public final static int SIDE_EAST = 1;
	public final static int SIDE_SOUTH = 2;
	public final static int SIDE_WEST = 3;
	
	/**
	 * Check if the connector may cross this point
	 * Optionally, returns a shape that defines the boundaries of the area  around this
	 * point that the connector may not cross.
	 * This method can be used for advanced connectors that route along other objects
	 * on the drawing
	 * @return A shape that defines the boundaries of the area around this point that 
	 * the connector may not cross. Returning null is allowed for implementing classes.
	 */
	Shape mayCross(Point2D point);
	
	/**
	 * Get the side of the object to which the start of the connector connects
	 * @return The side, one of the SIDE_* constants
	 */
	int getStartSide();
	
	/**
	 * Get the side of the object to which the end of the connector connects
	 * @return The side, one of the SIDE_* constants
	 */
	int getEndSide();
	
	/**
	 * Get the start point to which the connector must connect
	 * @return
	 */
	Point2D getStartPoint();
	
	/**
	 * Get the end point to which the connector must connect
	 * @return
	 */
	Point2D getEndPoint();
}