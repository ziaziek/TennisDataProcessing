package processing.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import data.Matches;
import logging.LoggerCustom;

import processing.interfaces.IInfoService;
import processing.interfaces.IInfoServiceEventListener;
import processing.interfaces.IMatchFoundEventListener;
import processing.exceptions.InfoServiceDirectoryException;

public class MatchesInfoService extends BaseInfoService implements IInfoService {

	/**
	 * Function will get info about matches scanning through the files in the directory. 
	 * First, it will need to discover the player's name and associate it with an id.\
	 * It will also need to associate id's of the opponents.
	 */
	@Override
	public List getInfoList() throws InfoServiceDirectoryException {
		List<Matches> retList = new ArrayList<>();
		if(dir==null){
			throw new InfoServiceDirectoryException();
		}
		if(dir.isDirectory()){
			for(File f : dir.listFiles()){
				List<String> processedList;
				try {
					processedList = Files.readAllLines(f.toPath(), Charset.forName("utf-8"));
					for(Matches m : extractMatchesInfo(processedList, null)){
						if(!alreadyIn(retList, m)){
							retList.add(m);
						}
					}
					//int idp = PlayersHelper.findByName(d, currentPlayer.getFirstName(), currentPlayer.getLastName()).get(0).getId();
				} catch (IOException e) {
					log.error(e.getLocalizedMessage(), e);
				}
			}
		}
		for(IInfoServiceEventListener s: listeners){
			s.onServiceEnded();
		}
		return retList;
	}

	@Override
	public void setDirectory(File dir) {
		this.dir = dir;
	}

	/**
	 * Retrieve info about matches from a list of strings (usually retrieved from a single file)
	 * @param t
	 * @param matchFoundListener TODO
	 * @return
	 */
	public static List<Matches> extractMatchesInfo(List<String> t, IMatchFoundEventListener matchFoundListener){
		List<Matches> list = new ArrayList<Matches>();
		int i = 0;
		for (String s : t) {
			if (s.equals(matchesMarker) && t.size() > i) {
				System.out.println(i);
				String[] matchInfo = t.get(i + 1).split(
						BaseInfoService.INFO_SEPARATOR);
				if (matchInfo.length == 3) {
					Matches m = extractMatch(matchInfo);
					if(matchFoundListener!=null){
					matchFoundListener.setProcessedMatchAndItemNumber(m, i);
				}
					if(!alreadyIn(list, m)){
					list.add(m);	
					}
				} else {
						new LoggerCustom("MatcjesInfoService").warn("Match information too short! Expected 3 parts but got "
								+ matchInfo.length);
				}
			}
			i++;
		}
		return list;
	}

	/**
	 * Check whether this kind of object is already in the list, according to the foreign key constraints
	 * @param mlist
	 * @param m
	 * @return
	 */
	private static boolean alreadyIn(List<Matches> mlist, Matches m){
		int i = 0;
		if(mlist!=null){
			while(i<mlist.size() && mlist.get(i).getDate().getTime().compareTo(m.getDate().getTime())!=0 && !mlist.get(i).getCity().equals(m.getCity())){
			i++;
		}
			return i<mlist.size() && mlist.size()>0;
		}
		return false;
		
	}
	
	public static Matches extractMatch(String[] matchInfo) {
		Matches m = new Matches();
		m.setDate(BaseInfoService.convertDateStringToCalendar(
				matchInfo[0], BaseInfoService.DATE_SEPARATOR));
		m.setCountry(matchInfo[1]);
		m.setCity(matchInfo[2]);
		return m;
	}
}
