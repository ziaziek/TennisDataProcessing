package tests;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.jsoup.*;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import data.GamesExtendedHelper;
import data.GamesResults;
import data.Matches;
import data.MatchesExt;
import data.Players;
import data.PlayersHelper;
import data.interfaces.ISample;
import structuresdefinition.WebSiteHandler;
import tennisdataprocessing.Grabber;

public class grabberjsouptests {

	class WebSiteHandlerTester extends WebSiteHandler{

		public WebSiteHandlerTester(String fileName) {
			super(fileName);
		}
		public Players getPlayerInfo(){
			return super.parsePlayerInfo();
		}
		

		
//		public List<Games> getGamesInfo(){
//			return super.parseMatch(element)
//		}
	}
	
	
	private WebSiteHandlerTester wt;
	private Grabber g;
	private Document doc;
	private String fn = "e:\\dataIn.txt";
	private String classSearched = "commonProfileContainer";
	
	@Before
	public void setUp() throws Exception {
		g = new Grabber();
		doc = Jsoup.parse(new File(fn), "utf-8");
		wt = new WebSiteHandlerTester(fn);
		wt.setClassContaining("commonProfileContainer");
		wt.setClassActivityInfo("bioPlayActivityInfo");
		wt.setClassActivityTableAlt("bioTableAlt");
		wt.setIdPlayerBioInfoList("playerBioInfoList");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void parseTest() {
		assertNotNull(doc);
		assertTrue(doc.getAllElements().size()>0);
		
	}
	
	@Test
	public void givenElementExists(){
		Elements elsa = doc.getElementsByClass(classSearched);
		assertTrue(elsa.size()>0);
		assertTrue(elsa.select(".bioTableWrap").size()>0);
	}
	
	@Test
	public void playerParsingTest(){
		
		
		Players p = wt.getPlayerInfo();
		assertNotNull(p);
		assertEquals("Spain", p.getCountry());
		assertEquals("Rafael", p.getFirstName());
		assertEquals("Nadal", p.getLastName());
		assertEquals(1986, p.getBirthday().get(Calendar.YEAR));
		assertEquals(5, p.getBirthday().get(Calendar.MONTH));
		assertEquals(3, p.getBirthday().get(Calendar.DAY_OF_MONTH));
		System.out.println(PlayersHelper.toString(p));
	}
	
	@Test
	public void matchesParseTest(){
		List<ISample> gret = wt.run();
		assertNotNull(gret);
		assertTrue(gret.size()>0);
		assertTrue(gret.get(0) instanceof MatchesExt);
		Matches m = (Matches)(gret.get(0));
		assertEquals("Barcelona", m.getCity());
		assertEquals("Spain", m.getCountry());
		assertEquals(22, m.getDate().get(Calendar.DAY_OF_MONTH));
		assertEquals(3, m.getDate().get(Calendar.MONTH));
		assertEquals(2013, m.getDate().get(Calendar.YEAR));
	}
	
	@Test
	public void gamesParseTest(){
		List<ISample> gret = wt.run();
		assertNotNull(gret);
		assertTrue(gret.size()>0);
		assertTrue(gret.get(0) instanceof MatchesExt);
		GamesExtendedHelper gameOne = (GamesExtendedHelper)(((MatchesExt)gret.get(0)).getMatchGames().get(0));
		assertEquals("Carlos Berlocq", gameOne.getOponentName().split(" ")[0]);
		assertEquals(GamesResults.WIN, gameOne.getResult());
		assertEquals(3, gameOne.getAvgPointDiff(), 0.01);
	}

}
