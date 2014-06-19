package com.fatty.gui;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.TableCellRenderer;

public class ImageTableCellRenderer extends JPanel implements TableCellRenderer {
	private static final long serialVersionUID = 1L;
	private String coverPath;
	//定义图标的宽度和高度
	final int ICON_WIDTH = 23;
	final int ICON_HEIGHT = 21;
	
	public Component getTableCellRendererComponent(JTable table, Object value, 
		boolean isSelected, boolean hasFocus, int row, int column)
	{
		coverPath = (String)value;
		//设置选中状态下绘制边框
		if (hasFocus)
		{
			setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
		}
		else
		{
			setBorder(null);
		}
		return this;
	}
	
	//重写paint方法，负责绘制该单元格内容
	public void paint(Graphics g)
	{
		drawImage(g , new ImageIcon(coverPath).getImage()); 
	}
	//绘制图标的方法
	private void drawImage(Graphics g , Image image)
	{
		g.drawImage(image, (getWidth() - ICON_WIDTH ) / 2 
			, (getHeight() - ICON_HEIGHT) / 2 , null);
	}
}
