import org.jlab.clas.physics.GenericKinematicFitter;
import org.jlab.clas.physics.Particle;
import org.jlab.clas.physics.PhysicsEvent;
import org.jlab.clas.physics.RecEvent;
import org.jlab.groot.data.H1F;
import org.jlab.groot.data.H2F;
import org.jlab.groot.math.F1D;
import org.jlab.groot.fitter.DataFitter;
import org.jlab.groot.fitter.ParallelSliceFitter;
import org.jlab.groot.data.GraphErrors;
import org.jlab.groot.ui.TCanvas;
import org.jlab.groot.graphics.EmbeddedCanvas;
import org.jlab.groot.data.DataLine;
import org.jlab.io.base.DataBank;
import org.jlab.io.base.DataEvent;
import org.jlab.io.hipo.*;
import org.jlab.clas.physics.Vector3;
import org.jlab.clas.detector.DetectorResponse;
import org.jlab.clas.detector.DetectorParticle;
import org.jlab.detector.base.DetectorType;
import org.jlab.service.ec.*;
import org.jlab.geom.prim.Vector3D;
import org.jlab.clas.physics.LorentzVector;
import org.jlab.groot.group.DataGroup;
import org.jlab.groot.data.TDirectory;
import org.jlab.groot.graphics.EmbeddedCanvasTabbed;
import javax.swing.JFrame;



H2F phiVsThetaEl = new H2F("phiVsThetaEl","phiVsThetaEl",    100, -180.0, 180.0, 100, 0.0, 35.0);
phiVsThetaEl.setTitleX("#theta (deg)");
phiVsThetaEl.setTitleY("#phi (deg)");
phiVsThetaEl.setTitle("Electron");

H2F phiVsThetaPip = new H2F("phiVsThetaPip","phiVsThetaPip", 100, -180.0, 180.0, 100, 0.0, 35.0);
phiVsThetaPip.setTitleX("#theta (deg)");
phiVsThetaPip.setTitleY("#phi (deg)");
phiVsThetaPip.setTitle("Pion");



// Efficiency
DataGroup dg_efficiency = new DataGroup(1, 2);

H1F el_counts = new H1F("el_counts", "el_counts", 100, 90, 160.0);
el_counts.setTitleX("#phi (deg)");
el_counts.setTitleY("Counts");
dg_efficiency.addDataSet(el_counts,   0);

H1F el_countsCC = new H1F("el_countsCC","el_countsCC", 100, 90, 160.0);
el_countsCC.setTitleX("#phi (deg)");
el_countsCC.setTitleY("Counts");
el_countsCC.setLineColor(2);
dg_efficiency.addDataSet(el_countsCC, 0);

H1F el_countsEFF = new H1F("el_countsEFF","el_countsEFF", 100, 90, 160.0);
el_countsEFF.setTitleX("#phi (deg)");
el_countsEFF.setTitleY("Efficiency");
dg_efficiency.addDataSet(el_countsEFF, 1);


DataGroup elec_efficiency = new DataGroup(1, 2);

H1F eel_counts = new H1F("eel_counts", "eel_counts", 100, 3.5, 8);
eel_counts.setTitleX("momentum (GeV)");
eel_counts.setTitleY("Counts");
elec_efficiency.addDataSet(eel_counts,   0);

H1F eel_countsCC = new H1F("eel_countsCC","eel_countsCC", 100, 3.5, 8);
eel_countsCC.setTitleX("momentum (GeV)");
eel_countsCC.setTitleY("Counts");
eel_countsCC.setLineColor(2);
elec_efficiency.addDataSet(eel_countsCC, 0);

H1F eel_countsEFF = new H1F("eel_countsEFF","eel_countsEFF", 100, 3.5, 8);
eel_countsEFF.setTitleX("momentum (GeV)");
eel_countsEFF.setTitleY("Efficiency");
elec_efficiency.addDataSet(eel_countsEFF, 1);


DataGroup pion_efficiency = new DataGroup(1, 4);

H1F pi_neutronMM = new H1F("pi_neutronMM", "pi_neutronMM", 200, 0.2, 2.0);
pi_neutronMM.setTitleX("e t (n) missing mass ");
pi_neutronMM.setTitleY("Counts");
pion_efficiency.addDataSet(pi_neutronMM, 0);

H1F pi_neutronMMC = new H1F("pi_neutronMMC", "pi_neutronMMC", 200, 0.2, 2.0);
pi_neutronMMC.setTitleX("e t (n) missing mass after cut");
pi_neutronMMC.setTitleY("Counts");
pi_neutronMMC.setLineColor(4);
pion_efficiency.addDataSet(pi_neutronMMC, 0);

H1F pi_neutronMMCA = new H1F("pi_neutronMMCA", "pi_neutronMMCA", 200, 0.2, 2.0);
pi_neutronMMCA.setTitleX("e t (n) missing mass after LTCC cut");
pi_neutronMMCA.setTitleY("Counts");
pi_neutronMMCA.setLineColor(2);
pion_efficiency.addDataSet(pi_neutronMMCA, 0);

H1F pi_counts = new H1F("pi_counts", "pi_counts", 100, 90, 150.0);
pi_counts.setTitleX("#phi (deg)");
pi_counts.setTitleY("Counts");
pion_efficiency.addDataSet(pi_counts, 1);

H1F pi_countsCC = new H1F("pi_countsCC", "pi_countsCC", 100, 90, 150.0);
pi_countsCC.setTitleX("#phi (deg)");
pi_countsCC.setTitleY("Counts");
pi_countsCC.setLineColor(2);
pion_efficiency.addDataSet(pi_countsCC, 1);

//H1F pi_counts = new H1F("pi_counts", "pi_counts", 100, 210, 270.0);
//pi_counts.setTitleX("#phi (deg)");
//pi_counts.setTitleY("Counts");
//pion_efficiency.addDataSet(pi_counts, 1);
//
//H1F pi_countsCC = new H1F("pi_countsCC", "pi_countsCC", 100, 210, 270.0);
//pi_countsCC.setTitleX("#phi (deg)");
//pi_countsCC.setTitleY("Counts");
//pi_countsCC.setLineColor(2);
//pion_efficiency.addDataSet(pi_countsCC, 1);


H1F pi_P = new H1F("pi_P", "pi_P", 9, 3.5, 8);
pi_P.setTitleX("Pion Momentum");
pi_P.setTitleY("Counts");
pion_efficiency.addDataSet(pi_P, 2);

H1F pi_PCC = new H1F("pi_PCC", "pi_PCC", 9, 3.5, 8);
pi_PCC.setTitleX("Pion Momentum after LTCC cut");
pi_PCC.setTitleY("Counts");
pi_PCC.setLineColor(2);
pion_efficiency.addDataSet(pi_PCC, 2);

H1F pi_EFF = new H1F("pi_EFF", "pi_EFF", 9, 3.5, 8);
pi_EFF.setTitleX("Pion Efficiency");
pi_EFF.setTitleY("Counts");
pi_EFF.setLineColor(1);
//pi_EFF.setMaximum(1.0)
pion_efficiency.addDataSet(pi_EFF, 3);




HipoDataSource reader = new HipoDataSource();
//reader.open("/opt/data/ltcc/mergedFall2018.hipo");
reader.open("/opt/data/ltcc/merged2019.hipo");

double ebeam = 10.2;
//double ebeam = 10.6;
int nevent = -1;
int ltccSector = 3

while(reader.hasEvent() == true && nevent<1000000000)
{
	DataEvent event = reader.getNextEvent();
	nevent++;
	if(nevent%100000 == 0) System.out.println("Analyzed " + nevent/1000000 + "M events");

	DataBank recEvent  = null;
	DataBank recPart   = null;
	DataBank recScint  = null;
	DataBank recCC     = null;
	DataBank recTracks = null;
	DataBank recTraj   = null;

	if(event.hasBank("REC::Event"))        recEvent = event.getBank("REC::Event");
	if(event.hasBank("REC::Particle"))      recPart = event.getBank("REC::Particle");
	if(event.hasBank("REC::Scintillator")) recScint = event.getBank("REC::Scintillator");
	if(event.hasBank("REC::Cherenkov"))       recCC = event.getBank("REC::Cherenkov");
	if(event.hasBank("REC::Track"))       recTracks = event.getBank("REC::Track");
	if(event.hasBank("REC::Traj"))          recTraj = event.getBank("REC::Traj");

	Particle recEl       = null;
	Particle recPositive = null;

   LorentzVector neutronMM   = null;

	if(recPart != null && recTracks != null ) {

		// looping over particles
		for(int pRow = 0; pRow < recPart.rows(); pRow++) {

			// good elettron: first entry and trigger status  < -2000
			if(recPart.getInt("pid", pRow)==11 && recEl==null && recPart.getShort("status", pRow)<= -2000 && pRow == 0 ) {
				recEl = new Particle(
											recPart.getInt("pid",  pRow),
											recPart.getFloat("px", pRow),
											recPart.getFloat("py", pRow),
											recPart.getFloat("pz", pRow),
											recPart.getFloat("vx", pRow),
											recPart.getFloat("vy", pRow),
											recPart.getFloat("vz", pRow));

				for(int j=0; j<recTracks.rows(); j++) {
					 if(recTracks.getShort("pindex", j)==pRow) recEl.setProperty("sector", (double) recTracks.getByte("sector", j));
				}

				int secEl = (int) recEl.getProperty("sector");

				if ( Math.toDegrees(recEl.theta()) > 15
					 && Math.toDegrees(recEl.theta()) < 35
					 && ( (Math.toDegrees(recEl.phi()) > 112 && Math.toDegrees(recEl.phi()) < 124 )
					 || (Math.toDegrees(recEl.phi()) > 132 && Math.toDegrees(recEl.phi()) < 142 ) )
					 && secEl == ltccSector
					 && recEl.p() > 1) {

					dg_efficiency.getH1F("el_counts").fill(Math.toDegrees(recEl.phi()));


					if ( Math.toDegrees(recEl.phi()) > 110 && Math.toDegrees(recEl.phi()) < 145 ) {
						elec_efficiency.getH1F("eel_counts").fill(recEl.p());
					}

					if(recCC != null) {
						for(int j=0; j<recCC.rows(); j++) {
							if(recCC.getShort("pindex", j)==pRow) {
								// System.out.println("recCC nphe: " + recCC.getInt("detector", j) + " " + recCC.getFloat("nphe", j));
								if (recCC.getInt("detector", j) == 16 ) {
									dg_efficiency.getH1F("el_countsCC").fill( Math.toDegrees(recEl.phi()));

									if ( Math.toDegrees(recEl.phi()) > 110 && Math.toDegrees(recEl.phi()) < 145 ) {
										elec_efficiency.getH1F("eel_countsCC").fill(recEl.p());
									}




								}
							}
						}
					}


				}
			}
			// all positive tracks
			else if(recPart.getInt("pid", pRow) == 211 && recPositive==null && Math.abs(recPart.getShort("status", pRow))>=2000 ) {
//				else if(recPart.getInt("charge", pRow) == 1 && recPositive==null && Math.abs(recPart.getShort("status", pRow))>=2000 ) {
				//System.out.println("pid: " + recPart.getInt("pid", pRow));

				// candidate positive
				recPositive = new Particle(
													211,
													recPart.getFloat("px", pRow),
													recPart.getFloat("py", pRow),
													recPart.getFloat("pz", pRow),
													recPart.getFloat("vx", pRow),
													recPart.getFloat("vy", pRow),
													recPart.getFloat("vz", pRow));

				for(int j=0; j<recTracks.rows(); j++) {
					 if(recTracks.getShort("pindex", j)==pRow) recPositive.setProperty("sector", (double) recTracks.getByte("sector", j));
				}


				if(recEl != null) {
					int secPositive = (int) recPositive.getProperty("sector");

					if (   Math.toDegrees(recPositive.theta()) > 10
						 && Math.toDegrees(recPositive.theta()) < 30
						 && secPositive == ltccSector
						 && recPositive.p() > 3.5
						 && ( (Math.toDegrees(recPositive.phi()) > 94 && Math.toDegrees(recPositive.phi()) < 108 )
						 || (Math.toDegrees(recPositive.phi()) > 120 && Math.toDegrees(recPositive.phi()) < 132 ) )
//						 && ( (Math.toDegrees(recPositive.phi()) > 94 && Math.toDegrees(recPositive.phi()) < 108 )
//						  || (Math.toDegrees(recPositive.phi()) > 120 && Math.toDegrees(recPositive.phi()) < 132 ) )
						 && Math.toDegrees(recEl.theta()) > 15
						 && Math.toDegrees(recEl.theta()) < 35
						 && recEl.p() > 0.5
						 && recEl.p() < 8) {

						neutronMM = new LorentzVector(0.0, 0.0, ebeam, 0.9383 + ebeam);
						neutronMM.sub(recEl.vector());
						neutronMM.sub(recPositive.vector());

						pion_efficiency.getH1F("pi_neutronMM").fill(neutronMM.mass());


						if (neutronMM.mass() > 0.9 && neutronMM.mass() < 1.05 ) {

							pion_efficiency.getH1F("pi_neutronMMC").fill(neutronMM.mass());


							pion_efficiency.getH1F("pi_P").fill(recPositive.p());
							pion_efficiency.getH1F("pi_counts").fill(Math.toDegrees(recPositive.phi()));

							if(recCC != null) {
								for(int j=0; j<recCC.rows(); j++) {
									if(recCC.getShort("pindex", j)==pRow) {
										if (recCC.getInt("detector", j) == 16 ) {
											pion_efficiency.getH1F("pi_PCC").fill(recPositive.p());
											pion_efficiency.getH1F("pi_countsCC").fill(Math.toDegrees(recPositive.phi()));
											pion_efficiency.getH1F("pi_neutronMMCA").fill(neutronMM.mass());



										}
									}
								}
							}
						}
					}
				}
			}
		}
	}
}


H1F he1  = pion_efficiency.getH1F("pi_P");
H1F he2  = pion_efficiency.getH1F("pi_PCC");
H1F he3  = pion_efficiency.getH1F("pi_EFF");
for(int i=0; i<he1.getDataSize(0); i++) {
    double e1 = he1.getBinContent(i);
    double e2 = he2.getBinContent(i);
	System.out.println("e1: " + e1 + ", e2: " + e2);
	if (e2 > 0) {
		double e3 = e2/e1;
		he3.setBinContent(i, e3/0.94);
		he3.setBinError(i, e3*Math.sqrt(e3*(1-e3)/e1));
	}
}

H1F ee1  = dg_efficiency.getH1F("el_counts");
H1F ee2  = dg_efficiency.getH1F("el_countsCC");
H1F ee3  = dg_efficiency.getH1F("el_countsEFF");
for(int i=0; i<ee1.getDataSize(0); i++) {
    double e1 = ee1.getBinContent(i);
    double e2 = ee2.getBinContent(i);
	System.out.println("e1: " + e1 + ", e2: " + e2);
	if (e2 > 0) {
		double e3 = e2/e1;
		ee3.setBinContent(i, e3);
		ee3.setBinError(i, e3*Math.sqrt(e3*(1-e3)/e1));
	}
}

H1F pe1  = elec_efficiency.getH1F("eel_counts");
H1F pe2  = elec_efficiency.getH1F("eel_countsCC");
H1F pe3  = elec_efficiency.getH1F("eel_countsEFF");
for(int i=0; i<ee1.getDataSize(0); i++) {
    double e1 = pe1.getBinContent(i);
    double e2 = pe2.getBinContent(i);
	System.out.println("e1: " + e1 + ", e2: " + e2);
	if (e2 > 0) {
		double e3 = e2/e1;
		pe3.setBinContent(i, e3);
		pe3.setBinError(i, e3*Math.sqrt(e3*(1-e3)/e1));
	}
}




EmbeddedCanvasTabbed myCanvas = new EmbeddedCanvasTabbed("Electron", "ElEff", "Pion", "EFF");

//myCanvas.getCanvas("Electron").divide(3,2);
myCanvas.getCanvas("Electron").setAxisFontSize(32);
myCanvas.getCanvas("Electron").setAxisTitleSize(32);
myCanvas.getCanvas("Electron").draw(dg_efficiency);

myCanvas.getCanvas("Pion").setAxisFontSize(32);
myCanvas.getCanvas("Pion").setAxisTitleSize(32);
myCanvas.getCanvas("Pion").draw(pion_efficiency);

myCanvas.getCanvas("EFF").setAxisFontSize(32);
myCanvas.getCanvas("EFF").setAxisTitleSize(32);
myCanvas.getCanvas("EFF").draw(pion_efficiency.getH1F("pi_EFF"), "E");

myCanvas.getCanvas("ElEff").setAxisFontSize(32);
myCanvas.getCanvas("ElEff").setAxisTitleSize(32);
myCanvas.getCanvas("ElEff").draw(elec_efficiency);


JFrame frame = new JFrame("ltcc");
frame.setSize(1600, 1000);
frame.add(myCanvas);
frame.setLocationRelativeTo(null);
frame.setVisible(true);

