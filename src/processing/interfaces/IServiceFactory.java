package processing.interfaces;

import java.io.File;

public interface IServiceFactory {
	
	IInfoService getInfoService(Class<?> className, File directory);

}
