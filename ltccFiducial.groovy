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


DataGroup dg_fiducial = new DataGroup(2, 3);

H2F phiVsThetaEl = new H2F("phiVsThetaEl","phiVsThetaEl",    200, 5.0, 35.0, 200, 90, 160.0);
phiVsThetaEl.setTitleX("#theta (deg)");
phiVsThetaEl.setTitleY("#phi (deg)");
phiVsThetaEl.setTitle("Electron");
dg_fiducial.addDataSet(phiVsThetaEl, 0);

H2F phiVsThetaPip = new H2F("phiVsThetaPip","phiVsThetaPip", 100, 5.0, 35.0, 100, 90, 160.0);
phiVsThetaPip.setTitleX("#theta (deg)");
phiVsThetaPip.setTitleY("#phi (deg)");
phiVsThetaPip.setTitle("Pion");
dg_fiducial.addDataSet(phiVsThetaPip, 1);

H2F phiVsThetaElC = new H2F("phiVsThetaElC","phiVsThetaElC",    100, 5.0, 35.0, 100, 90, 160.0);
phiVsThetaElC.setTitleX("#theta (deg)");
phiVsThetaElC.setTitleY("#phi (deg)");
phiVsThetaElC.setTitle("Electron");
dg_fiducial.addDataSet(phiVsThetaElC, 2);

H2F phiVsThetaPipC = new H2F("phiVsThetaPipC","phiVsThetaPipC", 100, 5.0, 35.0, 100, 90, 160.0);
phiVsThetaPipC.setTitleX("#theta (deg)");
phiVsThetaPipC.setTitleY("#phi (deg)");
phiVsThetaPipC.setTitle("Pion");
dg_fiducial.addDataSet(phiVsThetaPipC, 3);

H2F XYEl = new H2F("XYEl","XYEl",    300, -350, 350, 300, -400, 400);
XYEl.setTitleX("X");
XYEl.setTitleY("Y");
XYEl.setTitle("Electron");
dg_fiducial.addDataSet(XYEl, 4);

H2F XYPion = new H2F("XYPion","XYPion", 300, 0, 400, 300, 0, 400);
XYPion.setTitleX("X");
XYPion.setTitleY("Y");
XYPion.setTitle("Pion");
dg_fiducial.addDataSet(XYPion, 5);


HipoDataSource reader = new HipoDataSource();
reader.open("/opt/data/ltcc/merged2019.hipo");

int nevent = -1;
int ltccSector = 5

while(reader.hasEvent() == true && nevent<1000000)
{
	DataEvent event = reader.getNextEvent();
	nevent++;
	if(nevent%100000 == 0) System.out.println("Analyzed " + nevent/1000000 + "M events");

	DataBank recEvent  = null;
	DataBank recPart   = null;
	DataBank recCC     = null;
	DataBank recTracks = null;
	DataBank recTraj   = null;

	if(event.hasBank("REC::Event"))        recEvent = event.getBank("REC::Event");
	if(event.hasBank("REC::Particle"))      recPart = event.getBank("REC::Particle");
	if(event.hasBank("REC::Cherenkov"))       recCC = event.getBank("REC::Cherenkov");
	if(event.hasBank("REC::Track"))       recTracks = event.getBank("REC::Track");
	if(event.hasBank("REC::Traj"))          recTraj = event.getBank("REC::Traj");

	Particle recEl       = null;
	Particle recPositive = null;

	if(recPart != null && recTracks != null && recTraj != null ) {

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
					if(recTracks.getShort("pindex", j)==pRow) {
						recEl.setProperty("sector", (double) recTracks.getByte("sector", j));
					}
				}


				int secEl = (int) recEl.getProperty("sector");


				if ( secEl == ltccSector) {
					dg_fiducial.getH2F("phiVsThetaEl").fill(Math.toDegrees(recEl.theta()), Math.toDegrees(recEl.phi()));
				}

				for(int j=0; j<recTraj.rows(); j++) {
					if(recTraj.getShort("pindex", j)==pRow) {

						int detector = recTraj.getByte("detector", j);
						int layer    = recTraj.getByte("layer", j);

						if (detector == 12 && layer == 2) {
							double x = recTraj.getFloat("x", j);
							double y = recTraj.getFloat("y", j);

							dg_fiducial.getH2F("XYEl").fill(x, y);

							recEl.setProperty("fiducial", electronFiducial(x, y));

						}
					}

				}


			}

			// all positive tracks
			else if(recPart.getInt("pid", pRow) == 211 && recPositive==null && Math.abs(recPart.getShort("status", pRow))>=2000 ) {
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

				// requiring electron
				if(recEl != null) {

		//			System.out.println("fiducial: " + recEl.getProperty("fiducial"));
					int electronFiducial = recEl.getProperty("fiducial");

					int secPositive = (int) recPositive.getProperty("sector");

					if(recCC != null) {
						for(int jcc=0; jcc<recCC.rows(); jcc++) {
							if(recCC.getShort("pindex", jcc)==pRow) {

								if (recCC.getInt("detector", jcc) == 16 ) {

									if (secPositive == ltccSector && electronFiducial > 0) {
										dg_fiducial.getH2F("phiVsThetaPip").fill(Math.toDegrees(recPositive.theta()), Math.toDegrees(recPositive.phi()));
									}

									for(int j=0; j<recTraj.rows(); j++) {
										if(recTraj.getShort("pindex", j)==pRow) {

											int detector = recTraj.getByte("detector", j);
											int layer    = recTraj.getByte("layer", j);

											if (detector == 12 && layer == 2) {
												double x = recTraj.getFloat("x", j);
												double y = recTraj.getFloat("y", j);

												dg_fiducial.getH2F("XYPion").fill(x, y);


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
}



EmbeddedCanvasTabbed myCanvas = new EmbeddedCanvasTabbed("Fiducial");

myCanvas.getCanvas("Fiducial").setAxisFontSize(32);
myCanvas.getCanvas("Fiducial").setAxisTitleSize(32);
myCanvas.getCanvas("Fiducial").draw(dg_fiducial);


JFrame frame = new JFrame("ltcc");
frame.setSize(1600, 1000);
frame.add(myCanvas);
frame.setLocationRelativeTo(null);
frame.setVisible(true);

int electronFiducial(double x, double y) {

	double phi = Math.acos(Math.sqrt(x*x + y*y));

//	System.out.println("x: " + x + ",  y: " + y, " phi: " + phi);


	return 2;
}

