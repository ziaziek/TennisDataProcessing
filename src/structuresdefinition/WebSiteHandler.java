package structuresdefinition;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import logging.LogPc;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import data.Games;
import data.GamesExtendedHelper;
import data.GamesResults;
import data.Matches;
import data.MatchesExt;
import data.Players;
import data.PlayersHelper;
import data.interfaces.ISample;
import logging.LoggerCustom;

public class WebSiteHandler {

	protected Document doc;
        protected LoggerCustom log = new LoggerCustom("WebSiteHandler");
	protected String classContaining = null;
	protected String classActivityInfo = null;
	protected String classActivityTableAlt = null;
	protected String idPlayerBioInfoList = null;
        protected String playerBioInfoRank = null;

    public String getPlayerBioInfoRank() {
        return playerBioInfoRank;
    }

    public void setPlayerBioInfoRank(String playerBioInfoRank) {
        this.playerBioInfoRank = playerBioInfoRank;
    }
	protected String ageIdentifier = "Age:";
	protected String birthPlaceIdentifier = "Birthplace:";
	
	public String getAgeIdentifier() {
		return ageIdentifier;
	}

	public void setAgeIdentifier(String ageIdentifier) {
		this.ageIdentifier = ageIdentifier;
	}

	public String getBirthPlaceIdentifier() {
		return birthPlaceIdentifier;
	}

	public void setBirthPlaceIdentifier(String birthPlaceIdentifier) {
		this.birthPlaceIdentifier = birthPlaceIdentifier;
	}

	public String getIdPlayerBioInfoList() {
		return idPlayerBioInfoList;
	}

	public void setIdPlayerBioInfoList(String idPlayerBioInfoList) {
		this.idPlayerBioInfoList = idPlayerBioInfoList;
	}

	public String getClassContaining() {
		return classContaining;
	}

	public void setClassContaining(String classContaining) {
		this.classContaining = classContaining;
	}

	public String getClassActivityInfo() {
		return classActivityInfo;
	}

	public void setClassActivityInfo(String classActivityInfo) {
		this.classActivityInfo = classActivityInfo;
	}

	public String getClassActivityTableAlt() {
		return classActivityTableAlt;
	}

	public void setClassActivityTableAlt(String classActivityTableAlt) {
		this.classActivityTableAlt = classActivityTableAlt;
	}

	public WebSiteHandler(String fileName) {
		try {
			doc = Jsoup.parse(new File(fileName), "utf-8");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.warn(e.getMessage(), e);
		}
	}

	public WebSiteHandler(URL url) {
		try {
			doc = Jsoup.parse(url, 3500);
//                        BufferedWriter bf = Files.newBufferedWriter(Paths.get("dataIn.txt"), Charset.defaultCharset(), StandardOpenOption.CREATE);
//                        bf.write(doc.body().html(), 0, doc.body().html().length());
//                        
//                        bf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogPc.Pclog.warn(e.getMessage(), e);
		}
	}

	public List<ISample> run() {
		LogPc.Pclog.info("WebSiteHandler running...");
		List<ISample> retList = new ArrayList<>();
		if (doc != null) {
			Elements elArray = doc.getElementsByClass(classContaining);
			Element e = null;
			Elements e1 = null;
			Players currentPlayer = parsePlayerInfo();
			retList.add(currentPlayer);
			for (Iterator<Element> iterator = elArray.iterator(); iterator
					.hasNext();) {

				e = iterator.next();
				if ((e1 = e.getElementsByClass(classActivityInfo)) != null
						&& !e1.isEmpty()) {
					MatchesExt s = null;
					if ((s = parseMatch(e1.get(0))) != null) {
						for (ISample smp : parseResults(
								e.getElementsByTag("tr"), s, currentPlayer)) {
							s.getMatchGames().add((Games) smp);
						}
						retList.add(s);
					}
				}
			}
		} else {
			LogPc.Pclog.warn("Site Not found!");
		}
		LogPc.Pclog.info("WebSiteHandler run finished.");
		return retList;
	}


        
	protected Players parsePlayerInfo() {
		Players p = new Players();
		Element info = doc.getElementById(idPlayerBioInfoList);
		String[] titInfo = doc.title().split(" ");
                if(titInfo.length>=2){
                   p.setFirstName(titInfo[0]);
		   p.setLastName(titInfo[1]); 
                } else {
                    LogPc.Pclog.warn("Player could not be saved. title:"+doc.title());
                    return null;
                }
		
		Elements bioInfoElements = info.getElementsByTag("li");
		for (Element el : bioInfoElements) {
			if (el.text().contains(ageIdentifier)) {
				String[] ageInfo = el.text().split(" ");
				p.setBirthday(retrieveDateOfString(ageInfo[ageInfo.length - 1]
						.replace("(", "").replace(")", "")));
			}
			if (el.text().contains(birthPlaceIdentifier)) {
				String[] bioInfo = el.text().split(" ");
				p.setCountry(bioInfo[bioInfo.length - 1]);

			}

		}
                
		return p;
	}
		

	protected List<ISample> parseResults(Elements gamesElements,
			Matches whatMatch, Players thePlayer) { // gamesElements is a
													// collection of games
													// within the match
		Element e = null;
		List<ISample> gret = new ArrayList<>();
		for (Iterator<Element> iter = gamesElements.iterator(); iter.hasNext();) {
			if (!(e = iter.next()).hasClass("bioTableHead")) { // ommit
																// bioTableHead
																// row as a
																// header, no
														// useful
																// information
				// find td's within the row
				Elements tds = e.getElementsByTag("td");
				if (tds != null && !tds.isEmpty() && tds.size() > 1
						&& !tds.get(1).text().contains("Bye")) {
					// TODO: cope with &nbsp; signs that are not recognize
					// properly in tests
					String oppon = tds.get(1).text();
					if (oppon.indexOf("(") > -1) {
						oppon = oppon.substring(0,
								tds.get(1).text().indexOf("(") - 1);
					}
					String result = tds.get(3).text();
					GamesExtendedHelper g = new GamesExtendedHelper();
                                        g.setOponentRank(Integer.parseInt(tds.get(2).text()));
					g.setResult(GamesResults.parse(result));
					g.setIdMatches(whatMatch.getId());
					g.setAvgPointDiff(GamesExtendedHelper
							.translateAverageDiffInPoints(result));
					g.setOponentName(oppon);
					g.setAge(PlayersHelper.calculateAge(thePlayer,
							whatMatch.getDate()));
					gret.add(g);
				}
			}
		}
		return gret;

	}

	protected MatchesExt parseMatch(Element element) {
		String str = element.text();
		String[] strarr = str.split(";"); // here we'll get an array of City,
											// Country, Date, other info
		MatchesExt match = new MatchesExt();
		Date ret = null;
		if (strarr.length > 3) {
			String strarrD = strarr[1].substring(1);
			if (strarrD.length() >= 10) {
				match.setDate(retrieveDateOfString(strarrD));
			}

		}

		match.setCity(strarr[0].split(",")[0].replace(" ", ""));
		match.setCountry(strarr[0].split(",")[1].substring(1));
		return match;

	}

	protected Calendar retrieveDateOfString(String strarrD) {
		Calendar c = null;
		try {
			int[] dateParams = { Integer.parseInt(strarrD.substring(0, 2)),
					Integer.parseInt(strarrD.substring(3, 5)),
					Integer.parseInt(strarrD.substring(6)) };

			c = Calendar.getInstance();
			c.set(dateParams[2], dateParams[1] - 1, dateParams[0]);
		} catch (NumberFormatException ex) {
			LogPc.Pclog.error(ex.getMessage(), ex);
		}

		return c;
	}

}
