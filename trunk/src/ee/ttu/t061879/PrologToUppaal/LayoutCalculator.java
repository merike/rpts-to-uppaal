package ee.ttu.t061879.PrologToUppaal;

import java.awt.Graphics;
import java.awt.Point;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class LayoutCalculator {
	private boolean X = true;
	private boolean Y = false;
	private double mass = 1.0;
	private double equilibriumDistance = 200.0;
	private	double springConstant = 0.1;
	private double damping = 0.7;
	private double timeStep = 0.4;
	private int drawingSize = 600;
	private String layout = "";
	
	public LayoutCalculator(String layout) {
		this.layout = layout;
	}
	
	public void calculatePositions(Template t){
		if(this.layout.equalsIgnoreCase("force")) calculateForcePositions(t);
		else if(this.layout.equalsIgnoreCase("dot")) calculateDotPositions(t);
	}
	
	private void calculateDotPositions(Template t){
		try{
			File dotSource = new File(t.getName().getText() + ".dot");
			
			String dotInput = "digraph " + t.getName().getText() + "{\n";
			HashMap<String, ArrayList<String>> pairs = t.getUniqueSourceTargetPairs();
			Set<String> srcKeys = pairs.keySet();
			for(String src : srcKeys){
				ArrayList<String> targets = pairs.get(src);
				for(String target : targets){
					dotInput += src + " -> " + target + " ";
					// force dot to leave space for labels, exact label isn't used
					// but takes roughly the same amount of space
					// TODO possibly use real labels
					dotInput += "[label=\"tingimustele\\nomistustele\"];\n";
				}
			}
			dotInput += "}\n";
			MyFileReaderWriter.writeStringToFile(dotInput, dotSource.getAbsolutePath());
			
			File dotOut = new File(dotSource.getAbsolutePath().replace(".dot", "_out.dot"));
			
			Process p = Runtime.getRuntime().exec("dot -o" + 
					dotOut.getAbsolutePath() + " " + dotSource.getAbsolutePath());
					
			String line;
			BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
			while ((line = input.readLine()) != null) {
				System.out.println(line);
			}
			input.close();

//			System.err.println(p.waitFor());
			
			HashMap<String, Point> positions = new HashMap<String, Point>();
			HashMap<String, Point> labelPositions = new HashMap<String, Point>();
			HashMap<String, ArrayList<Point>> edges = new HashMap<String, ArrayList<Point>>();
			
			String out = MyFileReaderWriter.readFileToString(dotOut.getAbsolutePath());
			int start = 0;
			while(out.length() > 0){
				// remove line breaks from multi-line statements escaped by \
				out = out.trim().replace("\\\n", "");
				if(out.startsWith("digraph", start)){
//					System.err.println("digraph");
					out = out.substring(out.indexOf("\n", start) + 1);
				}
				else if(out.startsWith("node", start)){
//					System.err.println("node");
					out = out.substring(out.indexOf("\n", start) + 1);
				}
				else if(out.startsWith("graph", start)){
//					System.err.println("graph");
					out = out.substring(out.indexOf("\n", start) + 1);
				}
				// edge
				else if(out.indexOf(";\n", start) != -1
						&& out.substring(start, out.indexOf(";\n", start)).contains("->")){
//					System.err.println("edge");
					
					// parse locations for nails
					String edge = out.substring(0, out.indexOf(";\n"));
					int pos1 = edge.indexOf("->");
					String src = edge.substring(0, pos1).trim();
					int pos2 = edge.indexOf(" [");
					String dst = edge.substring(pos1 + 2, pos2).trim();
					int pos3 = edge.indexOf("pos=\"") + 7;
					int pos4 = edge.indexOf("\"", pos3 + 5);
					String edgePoints[] = edge.substring(pos3, pos4)
						.split(" ");
					ArrayList<Point> ps = new ArrayList<Point>();
					for(String point : edgePoints){
						ps.add(new Point(
								// dot's y-coordinate is reversed compared to Uppaal
								Integer.parseInt(point.substring(0, point.indexOf(","))),
								-Integer.parseInt(point.substring(point.indexOf(",") + 1))
						));
					}
					edges.put(src + "," + dst, ps);
					
					// parse locations for labels
					int pos5 = edge.indexOf("lp=\"") + 4;
					int pos6 = edge.indexOf("\"", pos5 + 1);
					System.err.println("label position " + edge.substring(pos5, pos6));
					String label = edge.substring(pos5, pos6);
					labelPositions.put(src + "," + dst, (new Point(
							// dot's y-coordinate is reversed compared to Uppaal
							Integer.parseInt(label.substring(0, label.indexOf(","))),
							-Integer.parseInt(label.substring(label.indexOf(",") + 1))
					)));
					
					
					out = out.substring(out.indexOf(";\n", start) + 2);
				}
				else if(out.startsWith("}")){
//					System.err.println("end");
					out = out.substring(1);
				}
				else{
//					System.err.println("node");
					String node = out.substring(0, out.indexOf("\n"));
					int pos1 = node.indexOf(" [");
					String name = node.substring(0, pos1);
					int pos2 = node.indexOf("pos=\"") + 5;
					int pos3 = node.indexOf("\"", pos2 + 5);
					String x = node.substring(pos2, node.indexOf(",")),
						y = node.substring(node.indexOf(",") + 1, pos3);
					// dot's y-coordinate is reversed compared to Uppaal
					Point point = new Point(Integer.parseInt(x), -Integer.parseInt(y));
					positions.put(name, point);
					
					out = out.substring(out.indexOf("\n", start) + 1);
				}
			}
			t.updateDotPositions(new Object[]{positions, labelPositions}, edges);
		}
		catch(IOException e){System.err.println(e.getMessage());}
//		catch(InterruptedException e){};
	}
	
	private void calculateForcePositions(Template t){
		HashMap<String, ArrayList<String>> c = t.getSourceTargetPairs();
		
		Set<String> keys = c.keySet();
//		System.err.println(keys.size());
		
		HashMap<String, Point> positions = new HashMap<String, Point>();
		
		// initial velocities
		HashMap<String, Double> velocitiesX = new HashMap<String, Double>();
		HashMap<String, Double> velocitiesY = new HashMap<String, Double>();
		for(String key : keys){
			velocitiesX.put(key, 0.0);
			velocitiesY.put(key, 0.0);
		}
		
		// initial positions
		Random r = new Random();
		for(String key : keys){
			Point p = new Point();
			p.x = Math.abs(r.nextInt() % (int)(drawingSize / 4.0)) 
				+ (int)(drawingSize * 0.5);
			p.y = Math.abs(r.nextInt() % (int)(drawingSize / 4.0)) 
				+ (int)(drawingSize * 0.5);
			positions.put(key, p);
		}
		
		GraphFrame g = new GraphFrame(positions, c);
//		g.refresh();
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		
		double totalKineticEnergy;
		for(int i = 0; i < 500; i++){
			int minX = Integer.MAX_VALUE, minY = Integer.MAX_VALUE,
				maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE;
			
			totalKineticEnergy = 0;
			
			// for each node
			for(String key : keys){
				double forceCX = 0.0;
				double forceCY = 0.0;
				double forceHX = 0.0;
				double forceHY = 0.0;
				
				Point p1 = positions.get(key);
//				System.err.println("calculating for " + key);
				
				// for each other node
				Set<String> otherKeys = c.keySet();
				for(String otherKey : otherKeys){
					Point p2 = positions.get(otherKey);
						
					// Coulomb_repulsion
					double tmpX = coulombRepulsion(X, p2, p1);
					double tmpY = coulombRepulsion(Y, p2, p1);
//					System.err.println("coulomb for " + key + " by " + otherKey + ": " + tmpX + ":" + tmpY);
//					try{Thread.sleep(1000);}catch(InterruptedException e){}
					forceCX += tmpX;
					forceCY += tmpY;
				}
				
				// for each connected node
				for(String otherKey : otherKeys){
					Point p2 = positions.get(otherKey);
					
					if(c.get(key).contains(otherKey)){
					
						// Hooke's attraction
						double tmpX = hookesAttraction(X, p2, p1);
						double tmpY = hookesAttraction(Y, p2, p1);
//						System.err.println("hookes for " + key + " by " + otherKey + ": " + tmpX + ":" + tmpY);
//						try{Thread.sleep(1000);}catch(InterruptedException e){}
						forceHX += tmpX;
						forceHY += tmpY;
					}
				}
				
				// update velocity
				velocitiesX.put(key, 
						(velocitiesX.remove(key) + timeStep * (forceCX + forceHX)) * damping);
//				if(Math.abs(forceCX) > Math.abs(forceHX)){
//					System.err.println("Coulon's stronger for X");
//				}
//				else{
//					System.err.println("Coulon's weaker for X");
//				}
				velocitiesY.put(key, 
						(velocitiesY.remove(key) + timeStep * (forceCY + forceHY)) * damping);
//				if(Math.abs(forceCY) > Math.abs(forceHY)){
//					System.err.println("Coulon's stronger for Y");
//				}
//				else{
//					System.err.println("Coulon's weaker for Y");
//				}
				
				// update position
//				double updateX = timeStep * velocitiesX.get(key);
//				double updateY = timeStep * velocitiesY.get(key);
//				System.err.println("update " + updateX + ":" + updateY);
				
				p1.x += timeStep * velocitiesX.get(key);
				p1.y += timeStep * velocitiesY.get(key);
//				System.err.println("new position for " + key + ": " + p1.x + " " + p1.y);
				
				if(p1.x < minX) minX = p1.x;
				if(p1.x > maxX) maxX = p1.x;
				if(p1.y < minY) minY = p1.y;
				if(p1.y > maxY) maxY = p1.y;
				
				// update total kinetic energy
				double velocity = Math.sqrt(Math.pow(velocitiesX.get(key), 2)
									+ Math.pow(velocitiesY.get(key), 2));
				totalKineticEnergy += mass * Math.pow(velocity, 2);
			}
			
			int xAdjust = 0 - minX;
			int yAdjust = 0 - minY;
			
			double xScaleAdjust = ((double)drawingSize) / (maxX - minX);
			double yScaleAdjust = ((double)drawingSize) / (maxY - minY);
			
			System.err.println("total energy " + totalKineticEnergy);
			
			try{Thread.sleep(1);}catch(InterruptedException e){}
			g.refresh(xAdjust, yAdjust, xScaleAdjust, yScaleAdjust);
			
		}
		try{Thread.sleep(1000);}catch(InterruptedException e){}
		g.dispose();
		t.updateForcePositions(positions);
		
		return;
	}
	
	/**
	 * F = m1*m2 / r^2
	 * 
	 * m1 and m2 are set equal internally
	 * 
	 * @param direction true for X, false for Y
	 * @return
	 */
	private double coulombRepulsion(boolean direction, Point p1, Point p2){
//		System.err.println(p1 + " " + p2);
		if(p1.x == p2.x && p1.y == p2.y) return 0.0;
		
		double squareDistance = Math.pow((p2.x - p1.x), 2)
									+ Math.pow((p2.y - p1.y), 2);
		double force = mass * mass / squareDistance;
//		System.err.println("coulomb " + force);
		
		if(p1.x == p2.x || p1.y == p2.y) return force;
		
		double angle = Math.atan((double)(p2.y - p1.y)/(p2.x - p1.x));
		
		if(getQuarter(p1, p2) == 3) angle += Math.PI;
		if(getQuarter(p1, p2) == 2) angle -= Math.PI;
		
		if(direction == X){
			return Math.cos(angle) * force;
		}
		else{
			return Math.sin(angle) * force;
		}
	}
	
	/**
	 * F = -kx
	 * 
	 * k and equilibrium distance are set internally
	 * 
	 * @param direction true for X, false for Y
	 * @return
	 */
	private double hookesAttraction(boolean direction, Point p1, Point p2){
		if(p1.x == p2.x && p1.y == p2.y) return 0.0;
		
		double distance = Math.sqrt(Math.pow((p2.x - p1.x), 2)
									+ Math.pow((p2.y - p1.y), 2));
		double x = equilibriumDistance - distance;
//		if(x > 0){
//			System.err.println("push apart " + x);
//		} else{
//			System.err.println("pull closer " + x);
//		}
		double force = springConstant * x;
//		System.err.println("hooke " + force);
		
		if(p1.x == p2.x || p1.y == p2.y) return force;
		
		double angle = Math.atan((double)(p2.y - p1.y)/(p2.x - p1.x));
		
		if(getQuarter(p1, p2) == 3) angle += Math.PI;
		if(getQuarter(p1, p2) == 2) angle -= Math.PI;
		
		if(direction == X){
			return Math.cos(angle) * force;
		}
		else{
			return Math.sin(angle) * force;
		}
	}
	

	public int getQuarter(Point p1, Point p2){
		boolean x, y;
		
		x = ((p2.x - p1.x) > 0);
		y = ((p2.y - p1.y) > 0);
		
		if(x == true ){
			if(y == true) return 1;
			else return 4;
		}
		else{
			if(y == true) return 2;
			else return 3;
		}
	}
	
	/**
	 * @author  Merike Sell
	 */
	class GraphFrame extends JFrame{
		/**
		 * @uml.property  name="d"
		 * @uml.associationEnd  
		 */
		private DrawPane d;
		
		public GraphFrame(HashMap<String, Point> points,
							HashMap<String, ArrayList<String>> c) {

			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			d = new DrawPane(points, c);
			d.setSize(drawingSize, drawingSize);
			this.getContentPane().add(d);
			
			this.setSize(drawingSize + 50, drawingSize + 50);
			this.setVisible(true);
		}
		
		public void refresh(int xAdj, int yAdj, double xScAdj, double yScAdj){
			d.refresh(xAdj, yAdj, xScAdj, yScAdj);
		}
		
		class DrawPane extends JPanel{
			HashMap<String, Point> p;
			HashMap<String, ArrayList<String>> e;
			
			int xAdj = 0;
			int yAdj = 0;
			double xScAdj = 1.0;
			double yScAdj = 1.0;
			
			public DrawPane(HashMap<String, Point> points,
							HashMap<String, ArrayList<String>> c) {
				this.p = points;
				this.e = c;
			}
			
			protected void refresh(int xAdj, int yAdj, double xScAdj, double yScAdj) {
				this.xAdj = xAdj;
				this.yAdj = yAdj;
				this.xScAdj = xScAdj;
				this.yScAdj = yScAdj;
				this.repaint();
			}
			
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				
				Set<String> s = p.keySet();
//				System.err.println(s.size() + " points");

				for(String key : s){
					Point point = p.get(key);
//					Point drawPoint = new Point(
//							(int)((point.x + xAdj)*xScAdj), (int)((point.y + yAdj)*yScAdj));
//					p.remove(key);
//					p.put(key, drawPoint);
										
//					g.drawOval(drawPoint.x, drawPoint.y, 3, 3);
//					g.drawString(key, drawPoint.x, drawPoint.y);
					g.drawOval(point.x, point.y, 3, 3);
					g.drawString(key, point.x, point.y);
					
					// edges
					ArrayList<String> edges = e.get(key);
					for(String str : edges){
						Point p2 = p.get(str);
//						Point drawPoint2 = new Point(
//								(int)((p2.x + xAdj)*xScAdj), (int)((p2.y + yAdj)*yScAdj));
//						p.remove(str);
//						p.put(str, drawPoint2);
						
						if(p2 == null) 
							System.err.println("What the..?");
						
//						System.err.println(drawPoint.x + " " + drawPoint.y + " " +
//								drawPoint2.x + " " +  drawPoint2.y);
						
//						g.drawLine(drawPoint.x, drawPoint.y,
//									drawPoint2.x, drawPoint2.y);
						
						g.drawLine(point.x, point.y,
								p2.x, p2.y);
					}
				}
			} // end paint
			
		}
	}
}
