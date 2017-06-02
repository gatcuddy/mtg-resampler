package sampler;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextPane;

public class MTG {
	
	//19,37,207,173
	
	public static final int CROPX1 = 19;
	public static final int CROPY1 = 37;
	public static final int CROPX2 = 206;
	public static final int CROPY2 = 173;
	
	MtgWindow ww;
	
	public static final int NOACTIVITY = 0;
	public static final int PULLACTIVITY = 1;
	public static final int SETUPACTIVITY = 1;
	public static final int SAMPLEACTIVITY = 2;
	public int activity;
	
	static int newcardwidth;
	static double newscale;
	
	private int basecardheight;
	private int basecardwidth;
	
	static int poolsize;
	
	private int cindex;
	static BufferedImage[] cards;
	
	private int findex;
	static BufferedImage[] full;
	
	static int oindex;
	static Color[] colors;
	
	static BufferedImage def;
	static BufferedImage resample;
	static BufferedImage desample;
	static BufferedImage[][] ppp;
	int[][][] lit;
	double lratio;
	
	private ExecutorService exe;
	
	private String uni = System.getProperty("user.home") + "/Desktop/dump";
	
	public BufferedImage buffer(Image i){
		BufferedImage b = new BufferedImage(i.getWidth(null),i.getHeight(null),BufferedImage.TYPE_INT_RGB);
		Graphics2D g = b.createGraphics();
		g.drawImage(i, 0, 0, null);
		return b;	
	}
	
	public void setup(){
		cindex = 0;
		oindex = 0;
		findex = 0;
		getPools();
		getLit();
		ww.ready = true;
	}
	
	public void getLit(){
		int ccount  =0;
		int ls = 27;
		lit = new int[ls][ls][ls]; 
		lratio = ls/256.0;
		for(int i =0;i<poolsize;i++){
			Color cc = colors[i];
			if(lit[(int)(cc.getRed()*lratio)][(int)(cc.getGreen()*lratio)][(int)(cc.getBlue()*lratio)]==0){
				ccount++;
			}
			lit[(int)(cc.getRed()*lratio)][(int)(cc.getGreen()*lratio)][(int)(cc.getBlue()*lratio)] = i+1;
		}
		int[][][] tlit = new int[ls][ls][ls];
		while(ccount<ls*ls*ls){
			for(int r =0;r<ls;r++){
				for(int g =0;g<ls;g++){
					for(int b =0;b<ls;b++){
						tlit[r][g][b] = lit[r][g][b];
					}
				}
			}
			for(int r =0;r<ls;r++){
				for(int g =0;g<ls;g++){
					for(int b =0;b<ls;b++){
						if(lit[r][g][b] != 0){
							int sel = lit[r][g][b];
							for(int z=-1;z<=1;z++){
								for(int y=-1;y<=1;y++){
									for(int x=-1;x<=1;x++){
										if(r+z>-1&&r+z<ls&&g+y>-1&&g+y<ls&&b+x>-1&&b+x<ls&&lit[r+z][g+y][b+x]==0&&tlit[r+z][g+y][b+x]==0){
											tlit[r+z][g+y][b+x] = sel;
											ccount++;
											//System.out.println(ccount+" | "+r+" "+g+" "+b);
										}
									}
								}
							}
						}
					}
				}
			}
			for(int r =0;r<ls;r++){
				for(int g =0;g<ls;g++){
					for(int b =0;b<ls;b++){
						lit[r][g][b] = tlit[r][g][b];
					}
				}
			}
		}
	}
	
	public void addtocolors(Color c){
		colors[oindex] = c;
		oindex++;
	}
	public void addtocards(BufferedImage b){
		cards[cindex] = b;
		cindex++;
	}
	public void addtofull(BufferedImage b){
		full[findex] =b;
		findex++;
	}
	
	public void getPools(){
		File folder = new File(uni);
		File[] files = folder.listFiles();
		poolsize = files.length;
		cards = new BufferedImage[poolsize];
		full = new BufferedImage[poolsize];
		colors = new Color[poolsize];
		for(int i=0;i<poolsize;i++){
			try{
				BufferedImage ti = ImageIO.read(files[i]);
				addtocards(ti);
				addtofull(ti);
				addtocolors(getAvg(ti));
				System.out.println("Got "+(i+1)+" out of "+poolsize);
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
	}
	
	public BufferedImage crop(BufferedImage b, int x1, int y1, int x2, int y2){
		BufferedImage nn = new BufferedImage(x2-x1,y2-y1,BufferedImage.TYPE_INT_ARGB);
		for(int y=y1;y<y2;y++){
			for(int x=x1;x<x2;x++){
				try{
					nn.setRGB(x-x1, y-y1, b.getRGB(x,y));
				}catch(Exception ex){
					nn.setRGB(x-x1, y-y1, 54645);
				}
			}
		}
		return nn;
	}	
	public BufferedImage crop(BufferedImage b){
		BufferedImage nn = new BufferedImage(CROPX2-CROPX1,CROPY2-CROPY1,BufferedImage.TYPE_INT_ARGB);
		for(int y=CROPY1;y<CROPY2;y++){
			for(int x=CROPX1;x<CROPX2;x++){
				try{
					nn.setRGB(x-CROPX1, y-CROPY1, b.getRGB(x,y));
				}catch(Exception ex){
					nn.setRGB(x-CROPX1, y-CROPY1, 54645);
				}
			}
		}
		return nn;
	}
	
	public Color getAvg(BufferedImage b){
		LinkedList<Color> cs = new LinkedList<Color>();
		for(int y=0;y<b.getHeight();y+=3){
			for(int x=0;x<b.getWidth();x+=3){
				cs.add(new Color(b.getRGB(x, y)));
			}
		}
		int[] t = new int[3];
		for(int i=0;i<cs.size();i++){
			t[0] += cs.get(i).getRed();
			t[1] += cs.get(i).getGreen();
			t[2] += cs.get(i).getBlue();
		}
		return new Color(t[0]/cs.size(),t[1]/cs.size(),t[2]/cs.size());
	}
	
	public BufferedImage scale(BufferedImage b, int iw, int ih){
		BufferedImage bb = new BufferedImage(iw,ih,BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bb.createGraphics();
		g.drawImage(b, 0, 0, bb.getWidth(), bb.getHeight(),null);
		return bb;
	}
	
	public void foundln(int www, int hhh, int ind, int x, int y){
		System.out.println("Found! - "+ind+" - "+Math.round(((double)(y*www)+(double)(x%www))/(double)(hhh*www)*100)+"%");
	}
	
	public void scaleall(int pich, int picw){
		BufferedImage[] angel = new BufferedImage[poolsize];
		for(int i=0;i<poolsize;i++){
			angel[i] = scale(full[i],picw,pich);
		}
		cards = angel.clone();
		cindex = 0;
	}
	
	//resample(resample,Integer.parseInt(csi.getText()),(int)(Integer.parseInt(csi.getText())*(double)full[0].getHeight()/full[0].getWidth()),Integer.parseInt(wis.getText()));
	public void supersample(){
		resample(resample);
	}
	
	public void resample(BufferedImage s, int across, double scl){
		scaleall((int)(basecardheight*scl),(int)(basecardwidth*scl));
		
		int hhh = (int)((double)s.getHeight()/(double)basecardheight*scl);
		int www = (int)((double)s.getWidth()/(double)basecardwidth*scl);
		
		hhh = (int)((double)scl*(double)s.getHeight()/s.getWidth()*(double)picw/pich);
		www = (int)(scl);
		
		BufferedImage sampp = new BufferedImage(www,hhh,BufferedImage.TYPE_INT_RGB);
		ppp = new BufferedImage[hhh][www];
		desample = new BufferedImage(www*picw,hhh*pich,BufferedImage.TYPE_INT_ARGB);
		Graphics g = desample.createGraphics();
		for(int y=0;y<hhh;y++){
			for(int x=0;x<www;x++){
				double wiw = ((double)s.getWidth()/(double)www);
				double hih = ((double)s.getHeight()/(double)hhh);
				sampp.setRGB(x, y, getAvg(crop(s,(int)(x*wiw),(int)(y*hih),(int)(x*wiw+wiw),(int)(y*hih+hih))).getRGB());
				Color tc = new Color(sampp.getRGB(x, y));
				int vari = lit[(int)(tc.getRed()*lratio)][(int)(tc.getGreen()*lratio)][(int)(tc.getBlue()*lratio)]-1;
				BufferedImage tb = cards[vari];
				ppp[y][x] = tb;
				g.drawImage(cards[vari], x*tb.getWidth(), y*tb.getHeight(), tb.getWidth(), tb.getHeight(), null);
				//if(!rep){   REP HAS BEEN RIPPED
				//	pool.remove(vari);
				//	cpool.remove(vari);
				//}
				//foundln(www, hhh, vari, x, y);
			}
		}
		System.out.println("SAMPLED");
		File f;
		int c=0;
		while(true){
			f = new File(System.getProperty("user.home") + "/Desktop/sampled/" + "samp"+c+".png");
			if(!f.exists()){
				break;
			}else{
				c++;
			}
		}
		try {
			ImageIO.write(desample, "PNG", f);
		}catch (IOException ex){}
		System.out.println("DONE");
	}
	
	public boolean pull(){
		BufferedImage now;
		int numpass = (int)(Math.random()*420617+1);
		now = getCard(numpass);
		if(same(now, def)){
			return false;
		}else{
			File f = new File(uni + "\\" + numpass + ".png");
			try {
				ImageIO.write(now, "PNG", f);
			} catch (IOException ex){
				ex.printStackTrace();
			}
			return true;
		}
	}
	
	public void clear(){
		File f = new File(uni);
		File[] clist = f.listFiles();
		for(int i=0;i<clist.length;i++){
			System.out.println("Deleted "+clist[i].getName()+"!");
			clist[i].delete();
		}
	}
	
	public MTG(){
		File df = new File(uni);
		if (!df.exists()){
			df.mkdir();
		}
		df = new File(System.getProperty("user.home") + "/Desktop/sampled"); //YASS MAOIN
		if (!df.exists()){
			df.mkdir();
		}
		ww = new MtgWindow();
		try{ def = ImageIO.read(getClass().getResource("Image.jpg"));} catch (IOException e) {}
		def = crop(def);
		setup();
		
		exe = Executors.newFixedThreadPool(2);
		exe.execute(new GameLoop());
		exe.execute(new PaintLoop());
	}
	
	public class GameLoop implements Runnable{
		public void run(){
			while(true){
				if(activity==NOACTIVITY){
					
				}else if(activity==SETUPACTIVITY){
					setup();
					activity = NOACTIVITY;
				}else if(activity==PULLACTIVITY){
					superpull();
					activity = NOACTIVITY;
				}else if(activity==SAMPLEACTIVITY){
					resample();
					activity = NOACTIVITY;
				}
			}
		}
	}
	
	public class PaintLoop implements Runnable{
		public void run(){
			while(true){
				ww.gopaint();
			}
		}
	}
	
	public String genHex(int l){
		String hchars = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM";
		String m = "";
		for(int i=0;i<l;i++){
			m += hchars.charAt((int)(Math.random()*hchars.length()));
		}
		return m;
	}
	
	public BufferedImage getCard(int index){
		BufferedImage image = null;
		try{
			image = ImageIO.read(new URL("http://gatherer.wizards.com/Handlers/Image.ashx?multiverseid="+index+"&type=card"));
		}catch(Exception ex){}
		try{
			image = crop(image);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return image;
	}
	
	public boolean same(BufferedImage b0, BufferedImage b1){
		boolean r=true;
		for(int y=0;y<b0.getHeight();y+=60){
			for(int x=0;x<b0.getWidth();x+=60){
				try{
					if(b0.getRGB(x, y) != b1.getRGB(x, y)){
						r=false;
					}
				}catch(Exception e){}
			}
		}
		return r;
	}
	
	//FOR TESTING PURPOSES ONLY
	public BufferedImage samp(BufferedImage b, int d){
		BufferedImage samp = new BufferedImage(b.getWidth(),b.getHeight(),BufferedImage.TYPE_INT_ARGB);
		for(int y=0;y<b.getHeight();y+=d){
			for(int x=0;x<b.getWidth();x+=d){
				samp.setRGB(x, y, b.getRGB(x,y));
			}
		}
		return samp;
	}

	public static void main(String[] args){
		new MTG();
	}
}