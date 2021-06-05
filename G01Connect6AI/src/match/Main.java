package match;

import core.match.GameEvent;
import g04.Group04.AI;

public class Main {

    public static void main(String[] args) {

    	GameEvent oucChampion = new GameEvent("OUCup2021");

		oucChampion.addPlayer(new AI());
    	oucChampion.addPlayer(new AI());
		//oucChampion.addPlayer(new g04.player.AI());
		//oucChampion.addPlayer(new baseline.player.AI());
		//oucChampion.addPlayer(new s17020031086.player.AI()); 	//杨国鑫
    	//oucChampion.addPlayer(new s17020031094.player.AI());	//岳志浩
//    	//oucChampion.addPlayer(new g17020031102.player.AI());	//张璇
		//oucChampion.addPlayer(new s17020032009.player.AI());	//李超然
		//oucChampion.addPlayer(new s17020032011.player.AI());	//潘星宇
    	oucChampion.arrangeMatches(1);
    	
    	oucChampion.run();
    	
    	oucChampion.showResults();
    }
}
