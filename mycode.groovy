//---- imports for HIPO4 library
import org.jlab.jnp.hipo4.io.*;
import org.jlab.jnp.hipo4.data.*;

//---- imports for GROOT library
import org.jlab.groot.data.*;
import org.jlab.groot.graphics.*;
import org.jlab.groot.ui.TCanvas;


//---- imports for PHYSICS library
import org.jlab.jnp.physics.*;
import org.jlab.jnp.reader.*;


// input file is argument
String  inputFile = args[0];
HipoReader reader = new HipoReader(); // Create a reader obejct
reader.open(inputFile);

Event     event = new Event();
Bank  particles = new Bank(reader.getSchemaFactory().getSchema("REC::Particle"));
double beamEnergy = 10.6;
LorentzVector vTarget = new LorentzVector(0.0, 0.0, 0.0, 0.938);


EventFilter  ePositiveT = new EventFilter("11:X+");

reader.getEvent(event,0); // Reads the first event and resets to the begining of the file
int filterCounter = 0;
int eventCounter  = 0;



// organize dirs
TDirectory  dir = new TDirectory();

dir.mkdir("/Kinematics");
dir.cd("/Kinematics");
H1F  hq2          = new H1F("hq2",          200, 0.1 , 8.0);
H1F  hw           = new H1F("hw",           200, 0.1 , 4.5);
H1F  hNeutronMass = new H1F("hNeutronMass", 100, -0.5, 4.0);
H2F  q2WMass      = new H2F("q2WMass",      200, 0.5, 4.8, 200, 0.1, 8.0);
hq2.setTitleX("Q^2 [GeV/c^2]");
hw.setTitleX("W [GeV]");
hNeutronMass.setTitleX("e pi^+ (n) missing mass[GeV]");
q2WMass.setTitleX("W [GeV]");
q2WMass.setTitleY("Q^2 [GeV/c^2]");

dir.addDataSet(hq2);
dir.addDataSet(hw);
dir.addDataSet(hNeutronMass);
dir.addDataSet(q2WMass);

dir.mkdir("/Ltcc");
dir.cd("/Ltcc");
H1F  neutronMM  = new H1F("neutronMM", 100, 0.05, 2.0);
dir.addDataSet(neutronMM);

TCanvas kinCanvas = new TCanvas("kinCanvas", 1400, 1000);
kinCanvas.divide(2, 2);
kinCanvas.cd(0);
kinCanvas.draw(hq2);
kinCanvas.cd(1);
kinCanvas.draw(hw);
kinCanvas.cd(2);
kinCanvas.draw(hNeutronMass);
kinCanvas.cd(3);
kinCanvas.draw(q2WMass);

//TCanvas ltccPidCanvas = new TCanvas("ltccPidCanvas", 1400, 1000);
//ltccPidCanvas.divide(1, 1);
//ltccPidCanvas.cd(0);
//ltccPidCanvas.draw(neutronMM);



// TODO:
// electron ID (see s.f.)
// electron fid. cut
// pion id
// pion fid. cut
// efficiency

// CHECKS:
// 1. Check existing train efficiency
// 2. Check our train

while(reader.hasNext()==true) {

	reader.nextEvent(event); // read the event object
	event.read(particles);   // read particles bank from the event



	
	// Data manages creates a physics event with beam energy
	// and from particles bank for reconstructed particles info
	PhysicsEvent physEvent = DataManager.getPhysicsEvent(beamEnergy, particles);

	// check if event passes the filter
	if(ePositiveT.isValid(physEvent)==true) {
		filterCounter++;

		Particle pq2     = physEvent.getParticle("[b]-[e-]");
		Particle x       = physEvent.getParticle("[b]+[t]-[e-]");
		Particle neutron = physEvent.getParticle("[b]+[t]-[e-]-[pi+]");


		hq2.fill(-pq2.mass2());
		hw.fill(x.mass());
		if (neutron.mass() > 0.1) {
			// need to add momentum condition here for pion
			hNeutronMass.fill(neutron.mass());
		}
		q2WMass.fill(x.mass(), -pq2.mass2());

	}
	eventCounter++;
}

System.out.println("analyzed " + eventCounter + " events. # passed filter = " + filterCounter);


//dir.writeFile("myAnalysis.hipo")
