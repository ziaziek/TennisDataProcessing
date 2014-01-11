package processing.impl;

import java.io.File;

import data.Games;
import data.Matches;
import data.Players;

import processing.interfaces.IInfoService;
import processing.interfaces.IServiceFactory;

public class DefaultInfoServiceFactory implements IServiceFactory {

	@Override
	public IInfoService getInfoService(Class<?> className, File directory) {
		IInfoService retService = null;
		if(className.equals(Players.class)){
			retService = new PlayersInfoService();
		} else if(className.equals(Matches.class)){
			retService = new MatchesInfoService();
		} else if (className.equals(Games.class)){
			retService = new GamesInfoService();
		}
		if(retService!=null){
			retService.setDirectory(directory);
		}
		return retService;
	}

}
