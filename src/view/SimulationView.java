package view;

/**
 * This Class represents the graphic interface of the game, and the evolution of the physicProcessus
 */
import model.PhysicEngine;
import processing.core.*;



public class SimulationView extends PApplet {

	/** FIELDS **/
	PGraphics pg; // Init Graphic
	double pos[] = { 45, 51 };// Init pos of ballooning
	double vel[] = { 1, -1 };// Init velocity of ballooning
	int pixPerMeterY = 8;// Constante pour convertir pixel en metre pour les Y
	int pixPerMeterX = 15;// Constante pour convertir pixel en metre pour les X
	PImage img, ballon;
	PFont myFont;

	PhysicEngine engine = new PhysicEngine(pos, vel); // moteur physique (equation)

	
	/** METHODES **/
	// Init the program
	public void setup() {
		size(1280, 720);
		pg = createGraphics(400, 200);
		img = loadImage("/home/cinna/workspace/LunarLander/chalet.jpg");
		ballon = loadImage("/home/cinna/workspace/LunarLander/ballon.png");
		myFont = createFont(
				"/home/cinna/workspace/LunarLander/Gudea-Regular.ttf", 15);

	}

	//Trunc the double to keep only 2 decimals (after comma)
	public static double round(double a) {
		return round(a, 2);
	}
	public static double round(double a, int decimals) {
		double dec = Math.pow(10, decimals);
		return Math.round(a * dec) / dec;
	}
	
	/** AFFICHAGE **/
	// display a circle which represent the ballooning
	public void afficherPoint() {
		ellipse((float) (pixPerMeterX * engine.getPosition()[0]),
				(float) (0.97 * 720 - pixPerMeterY * engine.getPosition()[1]),
				20, 20);

	}

	// display the ballooning
	public void dessinerMontgolfiere(double x, double y) {
		smooth();
		image(ballon, (float) x, (float) y, 70, 70);

	}

	// display information about Lunar Lander on the screen
	public void displayText() {
		textFont(myFont);
		textSize(12);

		//text we want to  display
		String 	posX = "Pos x : " 		+ round(engine.getPosition()[0]),
				posY = "Pos y :" 		+ round(engine.getPosition()[1]),
				vitesseX = "vitesse X : " + round(engine.getVelocity()[0]),
				vitesseY = "vitesse Y : " + round(engine.getVelocity()[1]),
				accelerationX = "accelarationX :" + round(engine.getAcceleration()[0]),
				accelerationY = "accelarationY :" + round(engine.getAcceleration()[1]),
				altitude = "altitude :" + round(engine.getAltitude());

		//time of flying (converted into seconds)
		int s = (int) (engine.getElapsedTime() / Math.pow(10, 9));
		String elapsedTime = "temps de vol :"	+ String.format("%d:%02d:%02d", 
																s / 3600, (s % 3600) / 60,	(s % 60));

		//display text
		fill(0, 0, 0);
		text(posX, 20, 150);
		text(posY, 20, 170);
		text(vitesseX, 20, 190);
		text(vitesseY, 20, 210);
		text(accelerationX, 20, 230);
		text(accelerationY, 20, 250);
		text(altitude, 20, 270);
		text(elapsedTime, 20, 290);
	}


	// display a landmark on the window
	public void afficherRepere() {
		rect(10, height - 15, width - 50, 2);// Axe x
		rect(10, height - 15, 2, -(height - 50));// Axe y
		triangle((width - 50), (height - 15) + 10, (width - 50) + 15,
				height - 15, (width - 50), (height - 15) - 10);// triangle x
		triangle(10 - 4, -(height - 50), 10, -(height - 50) + 10, 10 + 4,
				-(height - 50));
		triangle(10 - 10, 75, 10, 40, 10 + 10, 75);
	}

	/** MAIN- EXECUTABLE **/
	// Update the motion
	public void draw() {
		
		//display elements
		background(0);
		image(img, 0, 0);
		displayText();
		afficherRepere();
		
		//control the limits (when balloon is out off landmark = has landed = isStropped)
		if (! engine.isStopped()) { //si a cet instant le ballon n'est pas stoppe
			engine.update(); //on met a jour l'evolution
			
			if (engine.isStopped()) { //si apres il est stoppe, on met les vitesses a 0
				System.out.println("FINI !");
				engine.resetSpeedVector();
			}
			System.out.println(engine.getAcceleration()[0]);
		}
		
		//display elements
		fill(0, 12);
		rect(0, 0, width, height);
		fill(255);
		noStroke();

		dessinerMontgolfiere(pixPerMeterX * engine.getPosition()[0],
				(0.97 * 720 - pixPerMeterY * engine.getPosition()[1]));
		// afficherPoint();
		pg.beginDraw();

		pg.noFill();
		pg.stroke(0);
		pg.endDraw();

		image(pg, 120, 60);
	
	}

	
	
	/** MANUAL COMMAND : EVENTS OF KEYBOARD**/
	// Detect if a key is pressed or not
	public void keyPressed() {
		
		double step = 1; //step up the accelaration depending on the pressed key 
		
		if (key == CODED) {
			if (keyCode == LEFT) {
				/*
				 * System.out.println("left"); double
				 * x=pixPerMeterX*engine.getPosition()[0];
				 * engine.setPosition(49,51);
				 */
				engine.setAcceleration(engine.getAcceleration()[0] - step,
						engine.getAcceleration()[1]);
			}
			if (keyCode == RIGHT) {
				System.out.println("right");
				engine.setAcceleration(engine.getAcceleration()[0] + step,
						engine.getAcceleration()[1]);
			}
			if (keyCode == UP) {
				System.out.println("up");
				// engine.setAcceleration(1,0);
				engine.setAcceleration(engine.getAcceleration()[0],
						engine.getAcceleration()[1] + step);
			}
			if (keyCode == DOWN) {
				System.out.println("Down");
				engine.setAcceleration(engine.getAcceleration()[0],
						engine.getAcceleration()[1] - step);
			}
		}
	}
}