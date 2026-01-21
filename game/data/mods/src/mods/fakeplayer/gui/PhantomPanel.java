package mods.fakeplayer.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.sf.l2j.gameserver.ThreadPool;

import mods.fakeplayer.actor.FakePlayer;
import mods.fakeplayer.manager.FakePlayerManager;

public class PhantomPanel extends JFrame
{
	private static final long serialVersionUID = 1L;
	
	private static final int PAGE_SIZE = 10;
	
	private int currentPage = 0;
	
	private final JLabel totalLbl = label();
	private final JLabel combatLbl = label();
	private final JLabel idleLbl = label();
	
	private final FakeTableModel tableModel = new FakeTableModel();
	private final PaginationPanel pagination = new PaginationPanel();
	
	public PhantomPanel()
	{
		super("FakePlayer Runtime");
		List<Image> icons = new ArrayList<>();
		icons.add(new ImageIcon(".." + File.separator + "images/l2jdev_16x16.png").getImage());
		icons.add(new ImageIcon(".." + File.separator + "images/l2jdev_32x32.png").getImage());
		setIconImages(icons);
		
		setSize(600, 520);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		JPanel root = new JPanel(new BorderLayout(8, 8));
		root.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
		root.setBackground(new Color(18, 18, 18));
		
		root.add(header(), BorderLayout.NORTH);
		root.add(center(), BorderLayout.CENTER);
		root.add(footer(), BorderLayout.SOUTH);
		
		add(root);
		
		ThreadPool.scheduleAtFixedRate(this::refresh, 1000, 1000);
		setVisible(true);
	}
	
	/* ---------------- UI ---------------- */
	
	private JPanel header()
	{
		JPanel p = new JPanel(new GridLayout(1, 3, 6, 6));
		
		p.setOpaque(false);
		
		p.add(card("Total", totalLbl));
		p.add(card("Combat", combatLbl));
		p.add(card("Idle", idleLbl));
		
		return p;
	}
	
	private JPanel center()
	{
		JTable table = new JTable(tableModel);
		table.setRowHeight(22);
		
		JPanel p = new JPanel(new BorderLayout(6, 6));
		p.setOpaque(false);
		
		p.add(new JScrollPane(table), BorderLayout.CENTER);
		p.add(pagination, BorderLayout.SOUTH);
		
		return p;
	}
	
	private static JPanel footer()
	{
		JButton kill = new JButton("DESPAWN ALL");
		kill.addActionListener(e -> FakePlayerManager.getInstance().getFakePlayers().forEach(FakePlayer::deleteMe));
		
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.add(kill);
		return p;
	}
	
	/* ---------------- DATA ---------------- */
	
	private void refresh()
	{
		List<FakePlayer> all = FakePlayerManager.getInstance().getFakePlayers();
		
		int total = all.size();
		int combat = (int) all.stream().filter(FakePlayer::isInCombat).count();
		
		totalLbl.setText(String.valueOf(total));
		combatLbl.setText(String.valueOf(combat));
		idleLbl.setText(String.valueOf(total - combat));
		
		int pages = (int) Math.ceil(total / (double) PAGE_SIZE);
		
		pagination.configure(currentPage, pages, () -> {
			currentPage = pagination.getPage();
			refresh();
		});
		
		int from = currentPage * PAGE_SIZE;
		int to = Math.min(from + PAGE_SIZE, total);
		
		if (from < to)
			tableModel.setData(all.subList(from, to));
		else
			tableModel.setData(List.of());
	}
	
	private static JPanel card(String title, JLabel v)
	{
		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(new Color(28, 28, 28));
		p.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
		
		JLabel t = new JLabel(title);
		t.setForeground(Color.GRAY);
		t.setFont(new Font("Segoe UI", Font.PLAIN, 11));
		
		p.add(t, BorderLayout.NORTH);
		p.add(v, BorderLayout.CENTER);
		return p;
	}
	
	private static JLabel label()
	{
		JLabel l = new JLabel("0");
		l.setForeground(Color.WHITE);
		l.setFont(new Font("Segoe UI", Font.BOLD, 16));
		return l;
	}
}
