import org.jlab.io.base.DataEvent;
import org.jlab.io.base.DataBank;
import org.jlab.io.hipo.HipoDataSource;
import org.jlab.io.hipo.HipoDataSync;

public abstract class MyFilter {

    protected String usage = "MyFilter [-d outDir] filename1 filename2 ...";

    protected List <String> inputFilenames=new ArrayList<>();

    protected Map <String,List<String>> options=new HashMap<>();

    protected String outDirectory = ".";
    protected String outPrefix = "filter_"; 

    private int nEventsKept=0;
    private int nEventsProcessed=0;

    private HipoDataSource reader = new HipoDataSource();
    private HipoDataSync writer = null;

    public final void run(String[] args) {
        this.readArgs(args);
        this.init();
        this.processEvents();
        this.end();
    }

    public String getOutputFilename(String inputFilename) {
        String[] parts = inputFilename.split("/");
        String inputBasename = parts[parts.length-1];
        return this.outDirectory+"/"+this.outPrefix + inputBasename;
    }

    private final void processEvents() {

        for (String inFilename : inputFilenames) {

            final String outFilename = this.getOutputFilename(inFilename);
            
            System.out.println("Reading "+inFilename+" ...");
            System.out.println("Writing "+outFilename+" ...");

            reader.open(inFilename);

            int ii=0;
            while (reader.hasEvent()) {
                DataEvent event = reader.gotoEvent(ii);
                if (this.processEvent(event)) {
                    if (writer==null) {
                        writer = reader.createWriter();
                        writer.open(outFilename);
                    }
                    writer.writeEvent(event);
                    nEventsKept++;
                }
                nEventsProcessed++;
                ii++;
            }
            if (writer!=null) {
                writer.close();
            }
            writer=null;
            reader.close();
        }
    }

    protected final void end() {
        System.out.println("# Processed Events:  "+nEventsProcessed);
        System.out.println("# Events Kept:       "+nEventsKept);
    }

    private final void readArgs(String[] args) {
        if (args.length<1) {
            System.err.println(usage);
            System.exit(1);
        }
        for (int ii=0; ii<args.length; ii++) {
            // pick out options:
            if (args[ii].startsWith("-")) {
                if (!this.options.containsKey(args[ii])) {
                    this.options.put(args[ii],new ArrayList<String>());
                }
                if (ii+1 >= args.length) {
                    System.err.println(usage);
                    System.exit(1);
                }
                this.options.get(args[ii]).add(args[ii+1]);
                ii++;
                continue;
            }
            // everything else is an input file:
            else {
                this.inputFilenames.add(args[ii]);
            }
        }
        if (this.options.containsKey("-d")) {
            if (this.options.get("-d").size()==1) {
                this.outDirectory = this.options.get("-d").get(0);
            }
            else {
                System.err.println(usage);
                System.exit(1);
            }
        }
    }

    public abstract void init();
    public abstract boolean processEvent(DataEvent event);
}

