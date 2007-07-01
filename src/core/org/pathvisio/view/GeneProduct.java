// PathVisio,
// a tool for data visualization and analysis using Biological Pathways
// Copyright 2006-2007 BiGCaT Bioinformatics
//
// Licensed under the Apache License, Version 2.0 (the "License"); 
// you may not use this file except in compliance with the License. 
// You may obtain a copy of the License at 
// 
// http://www.apache.org/licenses/LICENSE-2.0 
//  
// Unless required by applicable law or agreed to in writing, software 
// distributed under the License is distributed on an "AS IS" BASIS, 
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
// See the License for the specific language governing permissions and 
// limitations under the License.
//
package org.pathvisio.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;

import org.pathvisio.data.DataSources;
import org.pathvisio.model.PathwayElement;

/**
 * This class implements a geneproduct and 
 * provides methods to resize and draw it.
 */
public class GeneProduct extends GraphicsShape
{
	private static final long serialVersionUID = 1L;
	public static final Color INITIAL_FILL_COLOR = Color.WHITE;
	
	//note: not the same as color!
	Color fillColor = INITIAL_FILL_COLOR;
		
	public GeneProduct (VPathway canvas, PathwayElement o) {
		super(canvas, o);		
		setHandleLocation();
	}
		
	public int getDrawingOrder() {
		return VPathway.DRAW_ORDER_GENEPRODUCT;
	}
	
	/**
	 * @deprecated get this info from PathwayElement directly
	 */
	public String getID() 
	{
		//Looks like the wrong way around, but in gpml the ID is attribute 'Name'
		//NOTE: maybe change this in gpml?
		return gdata.getGeneID();
	}
		
	/**
	 * Looks up the systemcode for this gene in Pathway.sysName2Code
	 * @return	The system code or an empty string if the system is not found
	 * 
	 * @deprecated use PathwayElement.getSystemCode()
	 */
	public String getSystemCode()
	{
		String systemCode = "";
		if(DataSources.sysName2Code.containsKey(gdata.getDataSource())) 
			systemCode = DataSources.sysName2Code.get(gdata.getDataSource());
		return systemCode;
	}
			
	/**
	 * Calculate the font size adjusted to the canvas zoom factor.
	 */
	private int getVFontSize()
	{
		return (int)(vFromM (gdata.getMFontSize()));
	}

	public void doDraw(Graphics2D g)
	{
		//Color
		if(isSelected()) {
			g.setColor(selectColor);
		} else {
			g.setColor(gdata.getColor());
		}
		
		//Gene box
		g.setStroke(new BasicStroke());
		
		Rectangle area = new Rectangle(
				getVLeft(), getVTop(), getVWidth(), getVHeight());
		
		g.draw(area);
		
		//Label
		//Don't draw label outside gene box
		g.setClip ( area.x - 1, area.y - 1, area.width + 1, area.height + 1);
	
		g.setFont(new Font(gdata.getFontName(), getVFontStyle(), getVFontSize()));
		
		String label = gdata.getTextLabel();
		TextLayout tl = new TextLayout(label, g.getFont(), g.getFontRenderContext());
		Rectangle2D tb = tl.getBounds();
		tl.draw(g, 	area.x + (int)(area.width / 2) - (int)(tb.getWidth() / 2), 
					area.y + (int)(area.height / 2) + (int)(tb.getHeight() / 2));
		
		drawHighlight(g);
	}
	
	public void drawHighlight(Graphics2D g)
	{
		if(isHighlighted())
		{
			Color hc = getHighlightColor();
			g.setColor(new Color (hc.getRed(), hc.getGreen(), hc.getBlue(), 128));
			g.setStroke (new BasicStroke (HIGHLIGHT_STROKE_WIDTH));
			g.drawRect(getVLeft(), getVTop(), getVWidth(), getVHeight());
		}
	}
}
