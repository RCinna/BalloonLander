package model;

import java.util.List;

public class PhysicEngine  {
	/** FIELDS **/
	/*
	private PhysicObject object;
	private List<ForceField> forceFields;
	private Balloon theBallon;
	*/	
	
	//initials data
	private static double glune=1.6;
	private double Fv=0; // force vente
	private double f=0; // force frottement
	private double initialFuel;//in Kg
	private double unladenWeight;//poid a vide in Kg
	private double maxThrust;//max rate of momentum transfer in kg*m/s^2
	private double thrust;//rate of momentum transfer in kg*m/s^2
	private boolean thrustOn;
	private double epsilon;//A verifier
	
	//conditions (vecteur d'etat)
	private double pos[] = new double[2];//position in meters. "position" is reserved word;
	private double velocity[] = new double[2];//velocity of LL in m/s
	
	//commande
	private double acceleration[] = new double[2];//acceleration of LL in m/s^
	
	//system evolutions
	private boolean inMotion; //If simulation on;
	private boolean crashed;//If LL crashed
	private boolean wasStoppedOnPreviousUpdate;		// True if in the last update, the simulation was considered "finished" (y <= 0)	
	//observations
	private double remainingFuel;//in Kg
	private double currentMass;//in Kg
	private double angle;	//in radians
	private int altitude;//In meters
	private long 	startTime,	// Moment du lancement du balloon
					lastUpdate, // Moment de la derniere mise à jour du systeme physique
					elapsedTimeBase;	// Time used for the base of the flight duration timer
	/*
	private int t; 	//time in seconds since start
	private int lastT;//last time (in ms) when position updated
	*/	
	

	/** CONSTRUCTOR **/
	public PhysicEngine(double position[],double vitesse[]){
		this.pos=position;
		this.velocity=vitesse;
		this.init();//init variable
	}

	/** METHODES **/

	//START OR END
	public void init(){		
		this.acceleration[0]=0;//x set initial acceleration to 0
		this.acceleration[1]=0;//y
		this.angle=0;
		this.unladenWeight=6839.0;
		this.initialFuel=816.5;
		this.remainingFuel = initialFuel;
		this.currentMass = unladenWeight + initialFuel;
		this.epsilon=4500.0/currentMass;
		this.maxThrust = 4000;  //in newtons
		this.thrust = 0;
		
		this.startTime = System.nanoTime();
		this.lastUpdate = System.nanoTime();
		this.elapsedTimeBase = 0;
		this.wasStoppedOnPreviousUpdate = false;
		
	}	
	public void reset(){
		this.acceleration[0]=0;
		this.acceleration[1]=0;
		this.pos[0]=0;
		this.pos[1]=0;
		this.velocity[0]=0;
		this.velocity[1]=0;
		this.init();
	}
	
	//when balloon start to move
	public void startMotion(){
		this.inMotion=true;
	}

	//when balloon stopped to move
	public void stopMotion(){
		this.inMotion=false;
	}	
		
	
	//WHILE RUNNING
	//this method is called at each loops to update positions and velocity of object
	//it calculs the time of flying : with current time and consider stopped time
	//to have an update of physic processus in real time
	public void update() {
		long now = System.nanoTime(); //hour of now
		long deltaNS = now - lastUpdate; //delta between now and the last update
		double delta = deltaNS / Math.pow(10, 9); //convert to second
		
		evolution(delta); //calcul new evolution
		lastUpdate = now;
		
		if (isStopped()) {
			if (! wasStoppedOnPreviousUpdate) { // Si le ballon vient de s'arreter
				wasStoppedOnPreviousUpdate = true ;
				elapsedTimeBase += getCurrentSessionDuration();	// store elapsed in the timer base
			}
		}
		else {
			if (wasStoppedOnPreviousUpdate) {	// Si le ballon etait arrete
				wasStoppedOnPreviousUpdate = false;
				startTime = System.nanoTime(); // reset start time
			}
		}
	}
	
	
	//this method calcul the new conditions of physicObject (vecteur d'etat)
	//TODO : wa have to add the removing of fuel and update weight
	public void evolution(double dt){
		double xPos=this.pos[0];
		double yPos=this.pos[1];
		double vX=this.velocity[0];
		double vY=this.velocity[1];
		
		double m=this.currentMass;
		double force=this.thrust;
		double theta = this.angle; 
		
		System.out.println("epsilon :"+ epsilon);
		//equations
		xPos = xPos + dt*vX + (0.5)*dt*dt*(acceleration[0]*epsilon-Fv/m)+f/m;
		yPos = yPos + dt*(vY) + (0.5)*dt*dt*epsilon*(acceleration[1]-glune/epsilon)+f/m;
		vX = vX + dt*(acceleration[0]*epsilon-Fv/m) + (f/m)*xPos;
		vY = vY + dt*epsilon*(acceleration[1]-glune/epsilon)+(f/m)*yPos;
		
		//updating
		this.pos[0]=xPos;
		this.pos[1]=yPos;
		this.velocity[0]=vX;
		this.velocity[1]=vY;
	}

	//if the balloon (physicObject) has a y-position null  : isStopped
	//(6 represents the height of balloon because landmark is on the top of it)
	public boolean isStopped() {
		return getPosition()[1] - 6 <= 0;
	}

	//TODO
	public void crash(){
		this.inMotion=false;
		System.out.println("Crash de Lunar Lander");
		
	}


	// Returns the current flight time of the ballon in nanoseconds. 
	//If the ballon is landed, the result will be incorrect.
	private long getCurrentSessionDuration() {
		return System.nanoTime() - startTime;
	}
	
	// Returns the total time of flight of  the ballon in nanoseconds.
	public long getElapsedTime() {
		if (isStopped()) {
			return elapsedTimeBase;
		}
		return getCurrentSessionDuration() + elapsedTimeBase;
	}
	
	public void resetSpeedVector() {
		velocity[0] = 0;
		velocity[1] = 0;
	}	
	

	
	/** GETTERS AND SETTERS **/
	public double getFuelInitial(){
		return this.initialFuel;
	}
	public void setRemainingFuel(int fuel){
		this.remainingFuel=fuel;
	}
	public void setVelocity(double[] velocity){
		this.velocity=velocity;
	}
	public double[] getVelocity(){
		return velocity;
	}
	public double[] getPosition(){
		return pos;
	}
	public void setPosition(double[] pos){
		this.pos=pos;
	}
	public void setPosition(double x,double y){
		this.pos[0]=x;
		this.pos[1]=y;
	}
	public void setAcceleration(double[] acceleration){
		this.acceleration=acceleration;
	}
	public void setAcceleration(double ax,double ay){
		this.acceleration[0]=ax;
		this.acceleration[1]=ay;
	}
	public double[] getAcceleration(){
		return acceleration;
	}
/*	public int getLastT() {
		return lastT;
	}

	public void setLastT(int lastT) {
		this.lastT = lastT;
	}
	*/
	public double getAngle() {
		return angle;
	}
	public void setAngle(double angle) {
		this.angle = angle;
	}
	public boolean isInMotion() {
		return inMotion;
	}
	public void setInMotion(boolean inMotion) {
		this.inMotion = inMotion;
	}
	public int getAltitude() {
		return altitude;
	}
	public void setAltitude(int altitude) {
		this.altitude = altitude;
	}
	public double getInitialFuel() {
		return initialFuel;
	}
	public void setInitialFuel(int initialFuel) {
		this.initialFuel = initialFuel;
	}
	public double getCurrentMass() {
		return currentMass;
	}
	public void setCurrentMass(int currentMass) {
		this.currentMass = currentMass;
	}
	public static double getGlune() {
		return glune;
	}
	public static void setGlune(double glune) {
		PhysicEngine.glune = glune;
	}
	public double getRemainingFuel() {
		return remainingFuel;
	}
	

/** DISPLAY METHOD **/
	public String toString(){
		String positionX=Double.toString(pos[0]);
		String positionY=Double.toString(pos[1]);
		String vitesseX=Double.toString(velocity[0]);
		String vitesseY=Double.toString(velocity[1]);
		String result="Position X :" + positionX + " Position Y :" + positionY +"\n" +
				"Vitesse X :" + vitesseX + " Vitesse Y :" + vitesseY + "\n";
		return result;
	}

}