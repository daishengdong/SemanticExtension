package com.fatty.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.fatty.book.Book;
import com.fatty.book.BookReader;
import com.fatty.image.Img;
import com.fatty.ontology.construction.OntologyConstructor;
import com.fatty.ontology.reasoning.MyReasoner;
import com.fatty.ontology.reasoning.OntologyReasoning;
import com.fatty.searcher.Searcher;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;

	private Vector<String> columnNames = new Vector<String>(Arrays.asList(new String[] {"封面" , "标识" , "书名" , "作者" , "类别", "简介"})); 

	private static String hintText = "输入要查询的关键词";

	private List<Book> bookList = new ArrayList<Book>();
	private List<Book> resultList = new ArrayList<Book>();

	private JPanel rightPane = new JPanel(new BorderLayout());
	private JPanel leftPane = new JPanel(new BorderLayout());

	private JScrollPane result_tb_pane = new JScrollPane();
	private JScrollPane book_tb_pane = new JScrollPane();

	private JTextField tfKeywords = null;
	private JButton btnSearch = null;

	private JCheckBox cbOntologyReasoning = null;
	private JCheckBox cbLocalCo_occurrence = null;
	private JCheckBox cbUsingSynonym = null;
	private JCheckBox cbUsingAntonym = null;

	private JTable tbResults = null;
	private JTable tbBooks = null;

	// 推理机
	MyReasoner reasoner = new MyReasoner();

	public void centered(Container container) {  
        Toolkit toolkit = Toolkit.getDefaultToolkit();  
        Dimension screenSize = toolkit.getScreenSize();  
        int w = container.getWidth();  
        int h = container.getHeight();  
        container.setBounds((screenSize.width - w) / 2,  
                (screenSize.height - h) / 2, w, h);  
    }

	public JTable createTable(List<Book> list) {
		Vector<Vector<String>> books = new Vector<Vector<String>>();
		Vector<String> row;

		for (int i = 0; i < list.size(); ++i) {
			row = new Vector<String>();
			row.add(list.get(i).getCover());
			row.add(list.get(i).getId().toString());
			row.add(list.get(i).getTitle());
			row.add(list.get(i).getAuthor());
			row.add(list.get(i).getCategory());
			row.add(list.get(i).getInfo());
			books.add(row);
		}

		// JTable table = new JTable(new DefaultTableModel(books, columnNames));
		JTable table = new JTable(new MyTableModel(books, columnNames));
		TableColumn coverColumn = table.getColumnModel().getColumn(0);
		//对第 1 列采用自定义的单元格绘制器
		coverColumn.setCellRenderer(new ImageTableCellRenderer());
		
        table.setRowSelectionAllowed(true);
        table.setSelectionBackground(Color.lightGray);
        table.setSelectionForeground(Color.white);
        table.setGridColor(Color.black);
        table.setShowGrid(true);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(true);
		table.setRowHeight(25);
        table.setBackground(Color.white);
		
        table.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2){
	            	JTable src = (JTable)e.getSource();
            		int index = src.getSelectedRow();
                	if (src == tbBooks) {
                		Img.show(bookList.get(index).getCover());
                	} else if (src == tbResults) {
                		Img.show(resultList.get(index).getCover());
                	}
                }
            }
        });
		return table;
	}

	public void showBooksInfo() {
		tbBooks = createTable(bookList);
		tbBooks.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		book_tb_pane.getViewport().add(tbBooks);
		leftPane.add(book_tb_pane, BorderLayout.CENTER);
	}

	public void showResultInfo() {
		tbResults = createTable(resultList);
		tbResults.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
		result_tb_pane.getViewport().add(tbResults);
		rightPane.add(result_tb_pane, BorderLayout.CENTER);
	}

	private void initUI() {
		book_tb_pane.setBackground(Color.WHITE);
		result_tb_pane.setBackground(Color.WHITE);
		leftPane.setBackground(Color.WHITE);
		rightPane.setBackground(Color.WHITE);

		tfKeywords = new JTextField();
		tfKeywords.setOpaque(true);
		tfKeywords.setPreferredSize(new Dimension(400, 26));

		tfKeywords.addFocusListener(new FocusListener() {
			public void focusGained(FocusEvent e) {
				JTextField src = (JTextField)e.getSource();
				if(src.getText().equals(hintText)){
					src.setText("");
				}
			}

			@Override
			public void focusLost(FocusEvent e) {
				JTextField src = (JTextField)e.getSource();
				if(src.getText().equals("")){
					src.setText(hintText);
				}
			}
		});

		btnSearch = new JButton("搜索");
		btnSearch.setOpaque(true);
		btnSearch.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		btnSearch.setFocusable(false);
		btnSearch.setPreferredSize(new Dimension(80, 26));

		btnSearch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String keywords = tfKeywords.getText().trim();
				if (keywords.equals(hintText)) {
					JOptionPane.showConfirmDialog(null, hintText);
					return;
				}
				// 名字虽然叫使用 类型，但其实就是 本体
				boolean usingCategory = cbOntologyReasoning.isSelected();
				boolean usingSynonym = cbUsingSynonym.isSelected();
				boolean usingAntonym = cbUsingAntonym.isSelected();
				List<String> reasoningList = OntologyReasoning.reasoning(keywords, usingCategory, usingSynonym, usingAntonym, reasoner);
				StringBuilder sb = new StringBuilder(keywords);
				for (String word : reasoningList) {
					sb.append(word);
				}
				resultList = Searcher.search(sb.toString());  
				showResultInfo();
			}
		});

		JPanel tool_pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tool_pane.add(tfKeywords);
		tool_pane.add(btnSearch);
		tool_pane.setBackground(Color.WHITE);
		tool_pane.setMinimumSize(tool_pane.getPreferredSize());

		cbOntologyReasoning = new JCheckBox("本体推理");
		cbLocalCo_occurrence = new JCheckBox("局部共现");
		cbUsingSynonym = new JCheckBox("同义词库");
		cbUsingAntonym = new JCheckBox("反义词库");
		cbOntologyReasoning.setFocusable(false);
		cbLocalCo_occurrence.setFocusable(false);
		cbUsingSynonym.setFocusable(false);
		cbUsingAntonym.setFocusable(false);
		cbOntologyReasoning.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cbLocalCo_occurrence.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cbUsingSynonym.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cbUsingAntonym.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		cbOntologyReasoning.setBackground(Color.WHITE);
		cbLocalCo_occurrence.setBackground(Color.WHITE);
		cbUsingSynonym.setBackground(Color.WHITE);
		cbUsingAntonym.setBackground(Color.WHITE);

		JPanel option_pane = new JPanel(new FlowLayout(FlowLayout.LEFT));
		option_pane.add(cbOntologyReasoning);
		option_pane.add(cbLocalCo_occurrence);
		option_pane.add(cbUsingSynonym);
		option_pane.add(cbUsingAntonym);
		option_pane.setBackground(Color.WHITE);
		option_pane.setMinimumSize(option_pane.getPreferredSize());

		JPanel navigate_pane = new JPanel(new GridLayout(2, 1));
		navigate_pane.add(tool_pane);
		navigate_pane.add(option_pane);
		rightPane.add(navigate_pane, BorderLayout.PAGE_START);

		showBooksInfo();
		showResultInfo();

		JSplitPane jsp = new JSplitPane();
		jsp.add(leftPane, JSplitPane.LEFT);
		jsp.add(rightPane, JSplitPane.RIGHT);
		add(jsp);
	}

	public MainFrame() {
		/** set LookAndFeel : SystemLookAndFeel */
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		setTitle("主界面");
		bookList = BookReader.loadBooks();
		Searcher.createIndexFile(bookList);
		initUI();
        centered(this);

		// 构建本体
		OntologyConstructor.construct();
        // 初始化推理机
		reasoner.getInfModel("data/books.rules", "data/books.owl");

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}

	class MyTableModel extends  DefaultTableModel {
		private static final long serialVersionUID = 1L;
		public MyTableModel(Vector data,Vector columns){
			super(data,columns);
		}
		public   boolean   isCellEditable(int   row,int   column){
			//设置是否可编辑
			return false;
		}
		public Class<?> getColumnClass(int columnIndex) {
			return Object.class;
		}
	}
}
