package mods.fakeplayer.gui;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import mods.fakeplayer.actor.FakePlayer;

public class FakeTableModel extends AbstractTableModel
{
	private static final long serialVersionUID = 1L;
	
	private static final String[] COLUMNS =
	{
		"Name",
		"State"
	};
	
	private final List<FakePlayer> data = new ArrayList<>();
	
	public void setData(List<FakePlayer> list)
	{
		data.clear();
		data.addAll(list);
		fireTableDataChanged();
	}
	
	@Override
	public int getRowCount()
	{
		return data.size();
	}
	
	@Override
	public int getColumnCount()
	{
		return COLUMNS.length;
	}
	
	@Override
	public String getColumnName(int col)
	{
		return COLUMNS[col];
	}
	
	@Override
	public Object getValueAt(int row, int col)
	{
		FakePlayer fp = data.get(row);
		
		switch (col)
		{
			case 0:
				return fp.getName();
			case 1:
				return fp.getCurrentAction();
			
		}
		return "";
	}
	
	@Override
	public boolean isCellEditable(int r, int c)
	{
		return false;
	}
}
