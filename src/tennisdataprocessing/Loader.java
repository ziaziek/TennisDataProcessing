package tennisdataprocessing;

import java.io.File;
import processing.exceptions.InfoServiceDirectoryException;
import processing.interfaces.IServiceFactory;
import data.Games;
import data.Matches;
import data.Players;
import data.impl.FileStorage;
import data.interfaces.IDataStorage;
import database.DataDealer;
import database.IDataDealerListener;
import errors.DataDealerWriteException;
import java.io.IOException;
import java.net.URL;
import logging.LoggerCustom;
import processing.impl.DefaultInfoServiceFactory;

public class Loader implements IDataDealerListener {

	protected LoggerCustom log = null;
	private String dirName;
	protected int  numberOfProcessedItems, numberOfItemsToProcess =0;
	protected Class<?>[] classes = {Players.class, Matches.class, Games.class};
	private IServiceFactory retrievalServiceFactory = null;
	protected String playersListFile = "C:/Users/ResultsGrabber/PlayersList.txt"; //to be set by the users

    public String getPlayersListFile() {
        return playersListFile;
    }

    public void setPlayersListFile(String playersListFile) {
        this.playersListFile = playersListFile;
    }

    public String getDataOut() {
        return dataOut;
    }

    public void setDataOut(String dataOut) {
        this.dataOut = dataOut;
    }

    public String getPlayersResultsPrefix() {
        return playersResultsPrefix;
    }

    public void setPlayersResultsPrefix(String playersResultsPrefix) {
        this.playersResultsPrefix = playersResultsPrefix;
    }

    public int getPlayersTimeOut() {
        return playersTimeOut;
    }

    public void setPlayersTimeOut(int playersTimeOut) {
        this.playersTimeOut = playersTimeOut;
    }
        protected  String dataOut = "C:/Users/ResultsGrabber/dataOut.txt"; //to be set by the users
        protected String playersResultsPrefix = "C:/Users/ResultsGrabber/Results/Player_";
        public int playersTimeOut = 1000; // time out between reading players
        
	public IServiceFactory getRetrievalServiceFactory() {
		return retrievalServiceFactory;
	}

	public void setRetrievalServiceFactory(IServiceFactory retrievalServiceFactory) {
		this.retrievalServiceFactory = retrievalServiceFactory;
	}

	public Loader(String fn){
		dirName = fn;
                log = new LoggerCustom("Loader");
	}
	
	public int load(Class<?>[] classesToRun){
		DataDealer d = new DataDealer();
			d.getListeners().add(this);
			for(int i=0; i<classesToRun.length; i++){
				int nt = loadInfo(d, classesToRun[i]);
					log.info("Loaded elements: " + nt);
			}
			return numberOfProcessedItems;
	}
	
	public int load(){	
			return load(classes);
	}
	
	protected int loadInfo(DataDealer d, Class<?> c){
		numberOfProcessedItems = 0;
		try {
			if (retrievalServiceFactory != null) {
				d.Write(retrievalServiceFactory.getInfoService(c,
						new File(dirName)).getInfoList());
			}
		} catch (DataDealerWriteException | InfoServiceDirectoryException e) {
			log.error(e.getLocalizedMessage(), e);
		}
		return numberOfProcessedItems;
	}
	
	public void changeNumberOfProcessed(int inserted) {
		numberOfProcessedItems=inserted;
	}
       
        /**
         * Downloads datafrom the given location and writes the processed data to a file,
         * reasy for further processing andwriring into a database
         */
       public  void loadData(){
            IDataStorage storage = new FileStorage();
		IDataStorage storedNames = new FileStorage();
		storage.setStringConnection(dataOut);
		storedNames.setStringConnection(playersListFile);
		Grabber g = new Grabber();

		try {
			int i = 0;
			for (URL u : g.createURLList(playersListFile)) {
				System.out.println("Processing " + u.toString());
				log.info("Processing " + u.toString());
				g.processDownload(u);
				storage.setStringConnection(playersResultsPrefix
						+ i + ".txt");
				storage.Connect();
				g.writeData(storage);
				i++;
				if (Thread.currentThread() != null) {
					try {
						Thread.sleep(playersTimeOut);
						log.info("Thread stopped.");
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		log.info("Finished.");
		// g.processDownload();
		// g.writeData(storage);
        }
       
           private static void loadGames(){
        DataDealer d = new DataDealer();
		Loader l = new Loader("C:/Users/ResultsGrabber/Results");
		l.setRetrievalServiceFactory(new DefaultInfoServiceFactory());
                l.load(new Class<?>[]{Games.class});
                d.close();
    }
}
