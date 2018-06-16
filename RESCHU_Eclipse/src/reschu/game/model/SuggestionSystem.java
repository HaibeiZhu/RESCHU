package reschu.game.model;

import reschu.game.controller.Reschu;

public class SuggestionSystem {
	private Reschu _reschu;
	private Game _game;
	
	// the suggestion / decision support system will be enabled
	// only in those scenarios with Guidance
	public SuggestionSystem(Reschu reschu, Game game) {
		_reschu = reschu;
		_game = game;
	}
}