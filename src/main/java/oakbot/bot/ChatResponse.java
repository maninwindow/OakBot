package oakbot.bot;

import oakbot.chat.SplitStrategy;

/**
 * A message to send in response to a command.
 * @author Michael Angstadt
 */
public class ChatResponse {
	private final String message;
	private final SplitStrategy splitStrategy;
	private final boolean bypassFilters;

	public ChatResponse(CharSequence message) {
		this(message, SplitStrategy.NONE);
	}

	public ChatResponse(CharSequence message, SplitStrategy splitStrategy) {
		this(message, splitStrategy, false);
	}

	public ChatResponse(CharSequence message, SplitStrategy splitStrategy, boolean bypassFilters) {
		this.message = message.toString();
		this.splitStrategy = splitStrategy;
		this.bypassFilters = bypassFilters;
	}

	public String getMessage() {
		return message;
	}

	public SplitStrategy getSplitStrategy() {
		return splitStrategy;
	}

	public boolean isBypassFilters() {
		return bypassFilters;
	}
}
