package processing.interfaces;

import data.Matches;

public interface IMatchFoundEventListener {

	public void setProcessedMatchAndItemNumber(Matches currentMatch, int itemNumber);
}
