package tests;

import data.Games;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import processing.impl.DefaultInfoServiceFactory;

import data.Matches;
import data.Players;
import database.DataDealer;
import org.junit.Ignore;
import tennisdataprocessing.Loader;

public class LoaderTests {

	class LoaderStub extends Loader {

		public LoaderStub(String fn) {
			super(fn);
			// TODO Auto-generated constructor stub
		}
		
		
		public int getNumberOfItemsToProcess(){
			return super.numberOfItemsToProcess;
		}
	}
	
	DataDealer d = null;
	LoaderStub l = null;
	@Before
	public void setUp() throws Exception {
		d = new DataDealer();
		l = new LoaderStub("C:/Users/ResultsGrabber/Results");
		l.setRetrievalServiceFactory(new DefaultInfoServiceFactory());
	}

	@After
	public void tearDown() throws Exception {
		d.close();
	}

	@Ignore
	@Test
	public void playersLoadingTest(){ 	
		assertTrue(l.load(new Class<?>[] {Players.class})>0);
	}
@Ignore
	@Test
	public void matchesLoaderTest(){
		
		assertTrue(l.load(new Class<?>[] {Matches.class})>0);
	}

    @Test
    public void gamesLoadTEst() {
        assertTrue(l.load(new Class<?>[]{Games.class}) > 0);
    }
}
