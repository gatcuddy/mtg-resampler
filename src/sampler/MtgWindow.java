package sampler;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferStrategy;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class MtgWindow extends JFrame{
	private static final long serialVersionUID = -44669440408101826L;
	
	double scale = 0.3;
	JPanel continent;
	Canvas rr;
	BufferStrategy bu;
	JButton sbu;
	JTextArea inp;
	JLabel wisl;
	JTextField wis;
	JLabel csil;
	JTextField csi;
	JButton gets;
	JLabel pnl;
	JTextField pn;
	JButton pb;
	JButton clr;
	JButton sv;
	JLabel pch;
	Insets insets;
	
	public MtgWindow(){
		setResizable(false);
		this.setIconImage(MTG.def);
		setTitle("MTG Resample");
		setSize(800,500);
		setVisible(true);
		addMouseWheelListener(new MouseWheelListener(){
			public void mouseWheelMoved(MouseWheelEvent e) {
				scale *= (double)((e.getWheelRotation()*-1)+22)/(double)22;
			}
		});
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		continent = new JPanel();
		continent.setLayout(null);
		add(continent);
		insets = continent.getInsets();
		sbu = new JButton();
		continent.add(sbu);
		sbu.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				MTG.activity = MTG.SAMPLEACTIVITY;
			}
		});
		rr = new Canvas();
		continent.add(rr);
		rr.createBufferStrategy(2);
		bu = rr.getBufferStrategy();
		inp = new JTextArea();
		inp.setEditable(true);
		inp.setLineWrap(true);
		inp.setAutoscrolls(true);
		continent.add(inp);
		wisl = new JLabel("Cards Across");
		continent.add(wisl);
		wis = new JTextField();
		wis.setText("100");
		continent.add(wis);
		csil = new JLabel("Card width");
		continent.add(csil);
		csi = new JTextField();
		csi.setText("30");
		continent.add(csi);
		pnl = new JLabel("Pull amount");
		continent.add(pnl);
		pn = new JTextField();
		pn.setText("100");
		continent.add(pn);
		pb = new JButton("Pull");
		pb.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				MTG.activity = MTG.PULLACTIVITY;
			}
		});
		continent.add(pb);
		clr = new JButton("Clear");
		clr.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				MTG.activity = MTG.CLEARACTIVITY;
			}
		});
		continent.add(clr);
		sv = new JButton("Save");
		sv.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				MTG.activity = MTG.SAVEACTIVITY;
			}
		});
		continent.add(sv);
		pch = new JLabel("Pool size:");
		continent.add(pch);
		insets = continent.getInsets();
		continent.addComponentListener(new ComponentListener(){
			public void componentHidden(ComponentEvent arg0) {
				rebound();
			}
			public void componentMoved(ComponentEvent arg0) {
				rebound();
			}
			public void componentResized(ComponentEvent arg0) {
				rebound();
			}
			public void componentShown(ComponentEvent arg0) {
				rebound();
			}
		});
		rr.setBounds(insets.left+200,0,getWidth(),getHeight());
		sbu.setBounds(insets.left+10,insets.top+10,180,30);
		inp.setBounds(insets.left+10,insets.top+50,180,80);
		wisl.setBounds(insets.left+10,insets.top+140,180,30);
		wis.setBounds(insets.left+100,insets.top+140,90,30);
		csil.setBounds(insets.left+10,insets.top+180,180,30);
		csi.setBounds(insets.left+100,insets.top+180,90,30);
		pnl.setBounds(insets.left+10,insets.top+220,90,30);
		pn.setBounds(insets.left+100,insets.top+220,90,30);
		pb.setBounds(insets.left+10,insets.top+260,180,30);
		clr.setBounds(insets.left+10,insets.top+300,180,30);
		sv.setBounds(insets.left+10,insets.top+340,180,30);
		pch.setBounds(insets.left+10,insets.top+380,180,30);
	}
	
	public void rebound(){
		rr.setBounds(insets.left+200,0,getWidth(),getHeight());
	}
	
	public void gopaint(){
		
		sbu.setEnabled(MTG.activity==MTG.NOACTIVITY && MTG.poolsize>0);
		if(MTG.activity==MTG.NOACTIVITY){
			sbu.setText("Sample!");
		}else if(MTG.activity==MTG.PULLACTIVITY){
			sbu.setText("Pulling "+MTG.pulload+" out of "+MTG.pullamm);
		}else if(MTG.activity==MTG.CLEARACTIVITY){
			sbu.setText("Clearing...");
		}else if(MTG.activity==MTG.SAMPLEACTIVITY){
			sbu.setText("Sampling...");
		}else if(MTG.activity==MTG.SETUPACTIVITY){
			sbu.setText("loading "+MTG.pulload+" out of "+MTG.poolsize);
		}else if(MTG.activity==MTG.ERRORACTIVITY){
			sbu.setText("Error");
		}else if(MTG.activity==MTG.SAVEACTIVITY){
			sbu.setText("Saving...");
		}
		pb.setEnabled(MTG.activity==MTG.NOACTIVITY);
		clr.setEnabled(MTG.activity==MTG.NOACTIVITY && MTG.poolsize>0);
		sv.setEnabled(MTG.activity==MTG.NOACTIVITY && MTG.desample !=null);
		pch.setText("Pool size: "+MTG.poolsize);
		
		Graphics2D g = (Graphics2D)bu.getDrawGraphics();
		do{
			try {
				g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		        g.setColor(Color.DARK_GRAY);
			    g.fillRect(0, 0, rr.getWidth(), rr.getHeight());
			    
				    for(int y=0;y<27;y++){
						for(int x=0;x<27;x++){
							try{
								g.drawImage(MTG.cards[MTG.lit[Integer.parseInt(csi.getText())][y][x]-1], (int)Math.round(x*MTG.basecardwidth*0.1), (int)Math.round(y*MTG.basecardwidth*0.1), (int)Math.round(MTG.basecardwidth*0.1), (int)Math.round(MTG.basecardwidth*0.1), null);
							}catch(Exception ex){
							}
						}
					}
			    
				if(MTG.desample != null && MTG.desample != null){
					try{
						g.drawImage(MTG.resample,0,0,(int)(MTG.desample.getWidth()*scale),(int)(MTG.desample.getHeight()*scale),null);
						g.drawImage(MTG.desample,0,0,(int)(MTG.desample.getWidth()*scale),(int)(MTG.desample.getHeight()*scale),null);
					}catch(Exception ex){}
				}
			}finally{
				g.dispose();
			}
			bu.show();
		}while(bu.contentsLost());
	}

}
