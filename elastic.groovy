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

double ebeam = 6.53536;

// define histos
        double maxW = ebeam*0.6+0.6;
        double maxQ2 = 1;
        if(ebeam>6) {
            maxW  = 3.5;
            maxQ2 = 3;
        }
  // General
        H2F hi_rec_q2w = new H2F("hi_rec_q2w","hi_rec_q2w",100, 0.6, ebeam*0.6+0.6, 100, 0.0, maxQ2); 
        hi_rec_q2w.setTitleX("W (GeV)");
        hi_rec_q2w.setTitleY("Q2 (GeV2)");
        H1F hi_rec_w = new H1F("hi_rec_w","hi_rec_w",250, 0.6, maxW); 
        hi_rec_w.setTitleX("W (GeV)");
        hi_rec_w.setTitleY("Counts");
        F1D f1_w = new F1D("f1_w", "[amp]*gaus(x,[mean],[sigma]) + [a] + [b]*x", 0.8, 1.2);
        f1_w.setParameter(0, 0);
        f1_w.setParameter(1, 1);
        f1_w.setParameter(2, 0.2);
        f1_w.setLineWidth(2);
        f1_w.setLineColor(2);
        f1_w.setOptStat("1111");
        H2F hi_rec_w_phi = new H2F("hi_rec_w_phi","hi_rec_w_phi",100, -180.0, 180.0, 250, 0.6, maxW); 
        hi_rec_w_phi.setTitleX("#phi (deg)");
        hi_rec_w_phi.setTitleY("W (GeV)");
        H2F hi_rec_el = new H2F("hi_rec_el","hi_rec_el",100, 0.5, ebeam+0.5, 100, 0.0, 35.0);
        hi_rec_el.setTitleX("p (GeV)");
        hi_rec_el.setTitleY("#theta (deg)");
        hi_rec_el.setTitle("Electron");
        F1D f1_el = new F1D("f1_el", "2*(180/3.14)*atan(sqrt(0.93832*([e0]-x)/2/[e0]/x))", ebeam*0.75, ebeam*0.99);
        f1_el.setParameter(0, ebeam);
        H2F hi_rec_pr = new H2F("hi_rec_pr","hi_rec_pr",100, 0.1, ebeam/2, 100, 30.0, 90.0);
        hi_rec_pr.setTitleX("p (GeV)");
        hi_rec_pr.setTitleY("#theta (deg)");
        hi_rec_pr.setTitle("Proton");       
        F1D f1_pr = new F1D("f1_pr", "(180/3.14)*acos(([e0]*[e0]+x*x-pow(([e0]+0.93832-sqrt(x*x+0.9382*0.9382)),2))/2/[e0]/x)", ebeam*0.1, ebeam*0.5);
        f1_pr.setParameter(0, ebeam);
        H2F hi_phi = new H2F("hi_phi", "hi_phi", 200, -180, 180, 200, -180, 180);   
        hi_phi.setTitleX("El #phi (deg)");
        hi_phi.setTitleY("Pr #phi (deg)");
//        hi_dphi.setOptStat("1110");
        H2F hi_dphi = new H2F("hi_dphi", "hi_dphi", 200, -180, 180, 200, -10, 10);   
        hi_dphi.setTitleX("Pr #phi (deg)");
        hi_dphi.setTitleY("#Delta#phi (deg)");
        H2F hi_dpr = new H2F("hi_dpr", "hi_dpr", 200, -180, 180, 200, -10, 10);   
        hi_dpr.setTitleX("Pr #phi (deg)");
        hi_dpr.setTitleY("#Delta p (GeV)");
        DataGroup dg_general = new DataGroup(3,2);
        dg_general.addDataSet(hi_rec_q2w,   0);
        dg_general.addDataSet(hi_rec_w,     1);
        dg_general.addDataSet(f1_w,         1);
        dg_general.addDataSet(hi_rec_w_phi, 2);
        dg_general.addDataSet(hi_rec_el,    3);
        dg_general.addDataSet(f1_el,        3);
        dg_general.addDataSet(hi_rec_pr,    4);  
        dg_general.addDataSet(f1_pr,        4);
        dg_general.addDataSet(hi_phi,       5);  
        dg_general.addDataSet(hi_dphi,      6);  
        dg_general.addDataSet(hi_dpr,       6);  
        // W
        DataGroup dg_w = new DataGroup(2,3);
        for(int sector=1; sector <= 6; sector++) {
            H1F hi_rec_w_sec = new H1F("hi_rec_w_" + sector, "hi_rec_w_" + sector, 250, 0.6, maxW);  
            hi_rec_w_sec.setTitleX("W (GeV)");
            hi_rec_w_sec.setTitleY("Counts");
            hi_rec_w_sec.setTitle("Sector " + sector);
            F1D f1_w_sec = new F1D("f1_w_" + sector, "[amp]*gaus(x,[mean],[sigma])", 0.8, 1.2);
            f1_w_sec.setParameter(0, 0);
            f1_w_sec.setParameter(1, 1);
            f1_w_sec.setParameter(2, 0.2);
            f1_w_sec.setLineWidth(2);
            f1_w_sec.setLineColor(2);
            f1_w_sec.setOptStat("1111");
            dg_w.addDataSet(hi_rec_w_sec, sector-1);
            dg_w.addDataSet(f1_w_sec    , sector-1);
        }  
        // Proton
        DataGroup dg_proton = new DataGroup(2,3);
        for(int sector=1; sector <= 6; sector++) {
            H2F hi_rec_pr_sec = new H2F("hi_rec_pr_" + sector, "hi_rec_pr_" + sector, 100, 0.1, ebeam/2, 100, 30.0, 90.0);
            hi_rec_pr_sec.setTitleX("p (GeV)");
            hi_rec_pr_sec.setTitleY("#theta (deg)");
            hi_rec_pr_sec.setTitle("Sector " + sector); 
            dg_proton.addDataSet(hi_rec_pr_sec, sector-1);  
            dg_proton.addDataSet(f1_pr, sector-1);  
        }
        // Electron
        DataGroup dg_electron = new DataGroup(2,3);
        for(int sector=1; sector <= 6; sector++) {
             H2F hi_rec_de_theta_sec = new H2F("hi_rec_de_theta_" + sector, "hi_rec_de_theta_" + sector, 100, -0.2, 0.2, 100, 5, 20);  
            hi_rec_de_theta_sec.setTitleX("#Delta p (GeV)");
            hi_rec_de_theta_sec.setTitleY("#theta (deg)");
            hi_rec_de_theta_sec.setTitle("Sector " + sector);
            dg_electron.addDataSet(hi_rec_de_theta_sec, sector-1);
        }
        // Phi
        DataGroup dg_phi = new DataGroup(2,3);
        for(int sector=1; sector <= 6; sector++) {
            H1F hi_dphi_sec = new H1F("hi_dphi_" + sector, "hi_dphi_" + sector, 100, 140.0, 220.0);  
            hi_dphi_sec.setTitleX("#Delta#phi (deg)");
            hi_dphi_sec.setTitleY("Counts");
            hi_dphi_sec.setTitle("Sector " + sector);
            F1D f1_dphi_sec = new F1D("f1_dphi_" + sector, "[amp]*gaus(x,[mean],[sigma])", 0.8, 1.2);
            f1_dphi_sec.setParameter(0, 0);
            f1_dphi_sec.setParameter(1, 1);
            f1_dphi_sec.setParameter(2, 0.2);
            f1_dphi_sec.setLineWidth(2);
            f1_dphi_sec.setLineColor(2);
            f1_dphi_sec.setOptStat("1111");
            dg_phi.addDataSet(hi_dphi_sec, sector-1);
            dg_phi.addDataSet(f1_dphi_sec, sector-1);
        }
        // Beam
        DataGroup dg_beam = new DataGroup(2,3);
        for(int sector=1; sector <= 6; sector++) {
            H1F hi_beam_sec = new H1F("hi_beam_" + sector, "hi_beam_" + sector, 100, ebeam*0.75, ebeam*1.2);  
            hi_beam_sec.setTitleX("Beam Energy (GeV)");
            hi_beam_sec.setTitleY("Counts");
            hi_beam_sec.setTitle("Sector " + sector);
            F1D f1_beam_sec = new F1D("f1_beam_" + sector, "[amp]*gaus(x,[mean],[sigma])", ebeam*0.9, ebeam*1.1);
            f1_beam_sec.setParameter(0, 0);
            f1_beam_sec.setParameter(1, 2.1);
            f1_beam_sec.setParameter(2, 0.2);
            f1_beam_sec.setLineWidth(2);
            f1_beam_sec.setLineColor(2);
            f1_beam_sec.setOptStat("1111");
            dg_beam.addDataSet(hi_beam_sec, sector-1);
            dg_beam.addDataSet(f1_beam_sec, sector-1);
        }
        // Efficiency
        DataGroup dg_efficiency = new DataGroup(1,3);
        H1F hi_eff1 = new H1F("hi_eff1","hi_eff1",100, -180, 180); 
        hi_eff1.setTitleX("#phi (deg)");
        hi_eff1.setTitleY("Counts");
        H1F hi_eff2 = new H1F("hi_eff2","hi_eff2",100, -180, 180); 
        hi_eff2.setTitleX("#phi (deg)");
        hi_eff2.setTitleY("Counts");
        H1F hi_eff = new H1F("hi_eff","hi_eff",100, -180, 180); 
        hi_eff.setTitleX("#phi (deg)");
        hi_eff.setTitleY("Counts");
	dg_efficiency.addDataSet(hi_eff1,0);
	dg_efficiency.addDataSet(hi_eff2,1);
	dg_efficiency.addDataSet(hi_eff,2);


HipoDataSource reader = new HipoDataSource();
//reader.open("/Users/devita/run_005990.fulltorus_alignment.hipo");
//reader.open("/Users/devita/cvt_5990.hipo");
reader.open("/Users/devita/cvt/out_clas_005983.0.6.3.1.hipo");
//reader.open("/Users/devita/cvt/out_clas_005983.0.CVTAlignment-hipo4.hipo");

int nevent = -1;

while(reader.hasEvent() == true && nevent<80000000)
{
   DataEvent event = reader.getNextEvent();
   nevent++;
   if(nevent%10000 == 0) System.out.println("Analyzed " + nevent + " events");
//   event.show();

   DataBank recEvent  = null;
   DataBank recPart   = null;
   DataBank recScint  = null;
   DataBank recCal    = null;
   DataBank recFT     = null;
   DataBank recTracks = null;
   DataBank recTraj   = null;

   if(event.hasBank("REC::Event"))                recEvent = event.getBank("REC::Event");
   if(event.hasBank("REC::Particle"))              recPart = event.getBank("REC::Particle");
   if(event.hasBank("REC::Scintillator"))         recScint = event.getBank("REC::Scintillator");
   if(event.hasBank("REC::Calorimeter"))            recCal = event.getBank("REC::Calorimeter");
   if(event.hasBank("REC::ForwardTagger"))           recFT = event.getBank("REC::ForwardTagger");
   if(event.hasBank("REC::Track"))               recTracks = event.getBank("REC::Track");
   if(event.hasBank("REC::Traj"))                  recTraj = event.getBank("REC::Traj");

   Particle recEl = null;
   Particle recPr = null;
   LorentzVector virtualPhoton  = null;
   LorentzVector hadronSystem   = null;
   LorentzVector virtualPhotonP = null;
   LorentzVector hadronSystemP  = null;
   if(event.hasBank("REC::Particle")==true && event.hasBank("REC::Track")){
      DataBank  bank  = event.getBank("REC::Particle");
      DataBank  track = event.getBank("REC::Track");
      int rows = bank.rows();
//      if(rows>=20) bank.show();
      for(int loop = 0; loop < rows; loop++){
//      	  int charge = (int) bank.getByte("charge", loop);
//          if(rows>=20) System.out.println(bank.getInt("pid", loop) + " " + charge);
          if(bank.getInt("pid", loop)==11 && recEl==null && Math.abs(bank.getShort("status", loop))>=2000) {
             recEl = new Particle(
                                  bank.getInt("pid", loop),
                                  bank.getFloat("px", loop),
                                  bank.getFloat("py", loop),
                                  bank.getFloat("pz", loop),
                                  bank.getFloat("vx", loop),
                                  bank.getFloat("vy", loop),
                                  bank.getFloat("vz", loop));
             for(int j=0; j<track.rows(); j++) {
                 if(track.getShort("pindex", j)==loop) recEl.setProperty("sector", (double) track.getByte("sector", j));
             }
          }
          else if(bank.getInt("charge", loop)==1 && recPr==null && Math.abs(bank.getShort("status", loop))>=2000) {
             recPr = new Particle(
                                  2212,
                                  bank.getFloat("px", loop),
                                  bank.getFloat("py", loop),
                                  bank.getFloat("pz", loop),
                                  bank.getFloat("vx", loop),
                                  bank.getFloat("vy", loop),
                                  bank.getFloat("vz", loop));
          }
       }
       if(recEl != null) {
          virtualPhoton = new LorentzVector(0.0, 0.0, ebeam, ebeam);
          virtualPhoton.sub(recEl.vector());
          hadronSystem = new LorentzVector(0.0, 0.0, ebeam, 0.9383+ebeam);
          hadronSystem.sub(recEl.vector());
          int secEl = (int) recEl.getProperty("sector");
          double phEl = Math.toDegrees(recEl.phi());
          if(Math.toDegrees(recEl.theta())>0){
             dg_general.getH2F("hi_rec_q2w").fill(hadronSystem.mass(),-virtualPhoton.mass2());
             dg_general.getH1F("hi_rec_w").fill(hadronSystem.mass());
             dg_general.getH2F("hi_rec_w_phi").fill(Math.toDegrees(recEl.phi()), hadronSystem.mass());
             dg_general.getH2F("hi_rec_el").fill(recEl.p(),Math.toDegrees(recEl.theta()));
             dg_w.getH1F("hi_rec_w_" + secEl).fill(hadronSystem.mass());
          }
          if(hadronSystem.mass()<1.1) {
             dg_efficiency.getH1F("hi_eff1").fill(phEl);
             if(recPr != null) {
                double phPr = Math.toDegrees(recPr.phi()); 
                if(phPr < phEl) phPr +=360;
                dg_general.getH2F("hi_rec_pr").fill(recPr.p(),Math.toDegrees(recPr.theta()));
                dg_general.getH2F("hi_phi").fill(Math.toDegrees(recEl.phi()),Math.toDegrees(recPr.phi()));
	        dg_general.getH2F("hi_dphi").fill(Math.toDegrees(recPr.phi()),phPr-phEl-180);
//	        dg_general.getH2F("hi_dpr").fill(Math.toDegrees(recPr.phi()),recPr.p()-dg_general.getF1D("f1_pr").eval(recPr.theta()));             		
                dg_phi.getH1F("hi_dphi_" + secEl).fill(phPr-phEl);
                if(recPr != null && Math.abs(phPr-phEl-180)<10) {
                   dg_proton.getH2F("hi_rec_pr_" + secEl).fill(recPr.p(),Math.toDegrees(recPr.theta()));
		   dg_electron.getH2F("hi_rec_de_theta_" + secEl).fill(recEl.p()-(ebeam+0.93832-recPr.e()),Math.toDegrees(recEl.theta()));
                   dg_beam.getH1F("hi_beam_" + secEl).fill((-0.93832+recPr.e()+recEl.p()));
                   dg_efficiency.getH1F("hi_eff2").fill(phEl);
                }
             }
          }
       }
    }
}

DataLine lineV = new DataLine(-15,-15,15,15);
                lineV.setLineColor(2);
                lineV.setLineWidth(2);
                lineV.setArrowSizeOrigin(0);
                lineV.setArrowSizeEnd(0);
                lineV.setArrowAngle(25);
DataLine lineT = new DataLine(-10,-10/29.97,10,10/29.97);
                lineT.setLineColor(2);
                lineT.setLineWidth(2);
                lineT.setArrowSizeOrigin(0);
                lineT.setArrowSizeEnd(0);
                lineT.setArrowAngle(25);


fitW(dg_general.getH1F("hi_rec_w"), dg_general.getF1D("f1_w"));
for(int sector=1; sector <= 6; sector++) {
    fitW(dg_w.getH1F("hi_rec_w_" + sector), dg_w.getF1D("f1_w_" + sector));
    fitPhi(dg_phi.getH1F("hi_dphi_" + sector), dg_phi.getF1D("f1_dphi_" + sector));
    fitEbeam(dg_beam.getH1F("hi_beam_" + sector), dg_beam.getF1D("f1_beam_" + sector));
}


H2F h2 = dg_general.getH2F("hi_dphi");
GraphErrors meanX = new GraphErrors();
meanX.reset();
ArrayList<H1F> hslice = h2.getSlicesX();
for(int i=0; i<hslice.size(); i++) {
    double  x = h2.getXAxis().getBinCenter(i);
    double ex = 0;
    double  y = hslice.get(i).getRMS();
    double ey = 0;
    double mean  = hslice.get(i).getDataX(hslice.get(i).getMaximumBin());
    double amp   = hslice.get(i).getBinContent(hslice.get(i).getMaximumBin());
    double sigma = hslice.get(i).getRMS();
    F1D f1 = new F1D("f1_dpdhi_phi","[amp]*gaus(x,[mean],[sigma])", -10.0, 10.0);
    f1.setParameter(0, amp);
    f1.setParameter(1, mean);
    f1.setParameter(2, 0.5);
    DataFitter.fit(f1, hslice.get(i), "Q"); //No options uses error for sigma 
    if(amp>10) meanX.addPoint(x, f1.getParameter(1), ex, f1.parameter(1).error());
}

H1F he1  = dg_efficiency.getH1F("hi_eff1");
H1F he2  = dg_efficiency.getH1F("hi_eff2");
H1F he3  = dg_efficiency.getH1F("hi_eff");
for(int i=0; i<he1.getDataSize(0); i++) {
    double e1 = he1.getBinContent(i);
    double e2 = he2.getBinContent(i);
    double e3 = e2/e1;
    he3.setBinContent(i,e3);
    he3.setBinError(i,e3*Math.sqrt(e3*(1-e3)/e1));
}    

EmbeddedCanvasTabbed myCanvas = new EmbeddedCanvasTabbed("Efficiency","Offset", "Beam", "Phi", "Electron", "Proton", "W", "General");

myCanvas.getCanvas("General").divide(3,2);
myCanvas.getCanvas("General").setGridX(false);
myCanvas.getCanvas("General").setGridY(false);
myCanvas.getCanvas("General").setAxisFontSize(18);
myCanvas.getCanvas("General").setAxisTitleSize(24);
myCanvas.getCanvas("General").draw(dg_general);
myCanvas.getCanvas("General").getPad(0).getAxisZ().setLog(true);
myCanvas.getCanvas("General").getPad(2).getAxisZ().setLog(true);
myCanvas.getCanvas("General").getPad(3).getAxisZ().setLog(true);
myCanvas.getCanvas("General").getPad(4).getAxisZ().setLog(true);
myCanvas.getCanvas("General").getPad(5).getAxisZ().setLog(true);

myCanvas.getCanvas("W").divide(3, 2);
myCanvas.getCanvas("W").setGridX(false);
myCanvas.getCanvas("W").setGridY(false);
myCanvas.getCanvas("W").setAxisFontSize(18);
myCanvas.getCanvas("W").setAxisTitleSize(24);
myCanvas.getCanvas("W").draw(dg_w);

myCanvas.getCanvas("Proton").divide(3, 2);
myCanvas.getCanvas("Proton").setGridX(false);
myCanvas.getCanvas("Proton").setGridY(false);
myCanvas.getCanvas("Proton").setAxisFontSize(18);
myCanvas.getCanvas("Proton").setAxisTitleSize(24);
myCanvas.getCanvas("Proton").draw(dg_proton);
for(int sector=1; sector <= 6; sector++) {
    myCanvas.getCanvas("Proton").getPad(sector-1).getAxisZ().setLog(true);
}

myCanvas.getCanvas("Electron").divide(3, 2);
myCanvas.getCanvas("Electron").setGridX(false);
myCanvas.getCanvas("Electron").setGridY(false);
myCanvas.getCanvas("Electron").setAxisFontSize(18);
myCanvas.getCanvas("Electron").setAxisTitleSize(24);
for(int sector=1; sector <= 6; sector++) {
    myCanvas.getCanvas("Electron").getPad(sector-1).getAxisZ().setLog(true);
}

myCanvas.getCanvas("Phi").divide(3, 2);
myCanvas.getCanvas("Phi").setGridX(false);
myCanvas.getCanvas("Phi").setGridY(false);
myCanvas.getCanvas("Phi").setAxisFontSize(18);
myCanvas.getCanvas("Phi").setAxisTitleSize(24);
myCanvas.getCanvas("Phi").draw(dg_phi);

myCanvas.getCanvas("Beam").divide(3, 2);
myCanvas.getCanvas("Beam").setGridX(false);
myCanvas.getCanvas("Beam").setGridY(false);
myCanvas.getCanvas("Beam").setAxisFontSize(18);
myCanvas.getCanvas("Beam").setAxisTitleSize(24);
myCanvas.getCanvas("Beam").draw(dg_beam);

myCanvas.getCanvas("Offset").divide(1,2);
myCanvas.getCanvas("Offset").setGridX(false);
myCanvas.getCanvas("Offset").setGridY(false);
myCanvas.getCanvas("Offset").setAxisFontSize(18);
myCanvas.getCanvas("Offset").setAxisTitleSize(24);
myCanvas.getCanvas("Offset").draw(dg_general.getH2F("hi_dphi"));
myCanvas.getCanvas("Offset").getPad(0).getAxisZ().setLog(true);
myCanvas.getCanvas("Offset").cd(1);
myCanvas.getCanvas("Offset").draw(meanX);
        TDirectory dir = new TDirectory();
	dir.mkdir("/graph");
        dir.cd("/graph");
        dir.addDataSet(meanX);
        dir.writeFile("graph.hipo");
        TDirectory dir2 = new TDirectory();
        dir2.readFile("graphNew.hipo");
        System.out.println(dir2.getDirectoryList());
        dir2.cd("graph/");
        dir2.pwd();
	GraphErrors meanC = dir2.getObject("graph/", "graphErrors");
//	meanC.setMarkerColor(2);
//	meanC.setMarkerSize(3);
//myCanvas.getCanvas("Offset").draw(meanC, "same");

myCanvas.getCanvas("Efficiency").divide(1,3);
myCanvas.getCanvas("Efficiency").setGridX(false);
myCanvas.getCanvas("Efficiency").setGridY(false);
myCanvas.getCanvas("Efficiency").setAxisFontSize(18);
myCanvas.getCanvas("Efficiency").setAxisTitleSize(24);
myCanvas.getCanvas("Efficiency").draw(dg_efficiency);



JFrame frame = new JFrame("Residuals");
frame.setSize(1600, 1000);
frame.add(myCanvas);
frame.setLocationRelativeTo(null);
frame.setVisible(true);




    void fitW(H1F hiw,F1D f1w) {

        // get histogram maximum in the rane 0.8-1.2
        int i1=hiw.getXaxis().getBin(0.8);
        int i2=hiw.getXaxis().getBin(1.25);
        double hiMax=0;
        int    imax=i1;
        for(int i=i1; i<=i2; i++) {
            if(hiMax<hiw.getBinContent(i)) {
                imax=i;
                hiMax=hiw.getBinContent(i);
            }
        }           
        double mean = hiw.getDataX(imax); //hiw.getDataX(hiw.getMaximumBin());
        double amp  = hiMax;//hiw.getBinContent(hiw.getMaximumBin());
        double sigma = 0.05;
        f1w.setParameter(0, amp);
        f1w.setParameter(1, mean);
        f1w.setParameter(2, sigma);
        double rmax = mean + 1.0 * Math.abs(sigma);
        double rmin = mean - 2.0 * Math.abs(sigma);
        f1w.setRange(rmin, rmax);
        DataFitter.fit(f1w, hiw, "Q"); //No options uses error for sigma 
        hiw.setFunction(null);
        mean = f1w.getParameter(1);
        sigma = f1w.getParameter(2);
        rmax = mean + 1.0 * Math.abs(sigma);
        rmin = mean - 2.0 * Math.abs(sigma);
        f1w.setRange(rmin, rmax);
//        System.out.println(mean + " " + sigma + " " + rmin + " " + rmax);
        DataFitter.fit(f1w, hiw, "Q"); //No options uses error for sigma 
        hiw.setFunction(null);
    }

    void fitPhi(H1F hiw,F1D f1w) {

        double mean = hiw.getDataX(hiw.getMaximumBin());
        double amp = hiw.getBinContent(hiw.getMaximumBin());
        double sigma = 3;
        f1w.setParameter(0, amp);
        f1w.setParameter(1, mean);
        f1w.setParameter(2, sigma);
        double rmax = mean + 2.0 * Math.abs(sigma);
        double rmin = mean - 2.0 * Math.abs(sigma);
        f1w.setRange(rmin, rmax);
        DataFitter.fit(f1w, hiw, "Q"); //No options uses error for sigma 
        hiw.setFunction(null);
        mean = f1w.getParameter(1);
        sigma = f1w.getParameter(2);
        rmax = mean + 2.0 * Math.abs(sigma);
        rmin = mean - 2.0 * Math.abs(sigma);
        f1w.setRange(rmin, rmax);
//        System.out.println(mean + " " + sigma + " " + rmin + " " + rmax);
        DataFitter.fit(f1w, hiw, "Q"); //No options uses error for sigma 
        hiw.setFunction(null);
    }
    void fitEbeam(H1F hiw,F1D f1w) {

        double mean = hiw.getDataX(hiw.getMaximumBin());
        double amp = hiw.getBinContent(hiw.getMaximumBin());
        double sigma = 0.04;
        f1w.setParameter(0, amp);
        f1w.setParameter(1, mean);
        f1w.setParameter(2, sigma);
        double rmax = mean + 2.0 * Math.abs(sigma);
        double rmin = mean - 2.0 * Math.abs(sigma);
        f1w.setRange(rmin, rmax);
        DataFitter.fit(f1w, hiw, "Q"); //No options uses error for sigma 
        hiw.setFunction(null);
        mean = f1w.getParameter(1);
        sigma = f1w.getParameter(2);
        rmax = mean + 2.0 * Math.abs(sigma);
        rmin = mean - 1.5 * Math.abs(sigma);
        f1w.setRange(rmin, rmax);
//        System.out.println(mean + " " + sigma + " " + rmin + " " + rmax);
        DataFitter.fit(f1w, hiw, "Q"); //No options uses error for sigma 
        hiw.setFunction(null);
    }
