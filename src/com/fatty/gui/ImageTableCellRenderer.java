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
	//����ͼ��Ŀ�Ⱥ͸߶�
	final int ICON_WIDTH = 23;
	final int ICON_HEIGHT = 21;
	
	public Component getTableCellRendererComponent(JTable table, Object value, 
		boolean isSelected, boolean hasFocus, int row, int column)
	{
		coverPath = (String)value;
		//����ѡ��״̬�»��Ʊ߿�
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
	
	//��дpaint������������Ƹõ�Ԫ������
	public void paint(Graphics g)
	{
		drawImage(g , new ImageIcon(coverPath).getImage()); 
	}
	//����ͼ��ķ���
	private void drawImage(Graphics g , Image image)
	{
		g.drawImage(image, (getWidth() - ICON_WIDTH ) / 2 
			, (getHeight() - ICON_HEIGHT) / 2 , null);
	}
}
