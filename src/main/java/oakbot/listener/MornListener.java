package oakbot.listener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import oakbot.bot.BotContext;
import oakbot.bot.ChatResponse;
import oakbot.chat.ChatMessage;

/**
 * Responds to "good morning" messages.
 * @author Michael Angstadt
 */
public class MornListener implements Listener {
	private final long timeBetweenReplies = TimeUnit.MINUTES.toMillis(10);
	private final long hesitation;
	private final String botUsername;
	private final MentionListener mentionListener;
	private final Map<Integer, Long> lastReplies = new HashMap<>();

	/**
	 * The messages that the bot will respond to, along with its responses. The
	 * longest messages must be specified first.
	 */
	//@formatter:off
	private final List<String[]> responses = Arrays.asList(
		new String[] {"good morning", "Good morning."},
		new String[] {"morning", "Morning."},
		new String[] {"morno", "morno"},
		new String[] {"morn", "morn"}
	);
	//@formatter:on

	/**
	 * @param botUsername the bot's username
	 * @param hesitation the amount of time to wait before responding (in
	 * milliseconds)
	 * @param mentionListener the mention listener
	 */
	public MornListener(String botUsername, long hesitation, MentionListener mentionListener) {
		this.botUsername = botUsername;
		this.hesitation = hesitation;
		this.mentionListener = mentionListener;
	}

	@Override
	public String name() {
		return "morn";
	}

	@Override
	public String description() {
		return "Replies to \"good morning\" messages.";
	}

	@Override
	public String helpText() {
		return description();
	}

	@Override
	public ChatResponse onMessage(ChatMessage message, BotContext context) {
		List<String> mentions = message.getContent().getMentions();
		boolean mentioned = message.getContent().isMentioned(botUsername);
		if (!mentions.isEmpty() && !mentioned) {
			/*
			 * Message isn't directed toward the bot.
			 */
			return null;
		}

		String content = removeMentionsAndPunctuation(message.getContent().getContent());
		String reply = null;
		for (String[] response : responses) {
			if (content.equalsIgnoreCase(response[0])) {
				reply = response[1];
				break;
			}
		}
		if (reply == null) {
			return null;
		}

		/*
		 * Always reply if the bot is mentioned.
		 */
		if (mentioned) {
			mentionListener.ignoreNextMessage();

			/*
			 * Wait for a moment to make it seem less robotic.
			 */
			hesitate();

			return Listener.reply(reply, message);
		}

		int roomId = message.getRoomId();
		Long lastReply = lastReplies.get(roomId);
		if (lastReply == null) {
			lastReply = 0L;
		}

		/*
		 * Do not respond if the bot was not mentioned and it responded
		 * recently.
		 */
		long now = System.currentTimeMillis();
		if (now - lastReply < timeBetweenReplies) {
			return null;
		}

		lastReplies.put(roomId, now);

		/*
		 * Wait for a moment to make it seem less robotic.
		 */
		hesitate();

		return new ChatResponse(reply);
	}

	private void hesitate() {
		try {
			Thread.sleep(hesitation);
		} catch (InterruptedException ignored) {
		}
	}

	private static String removeMentionsAndPunctuation(String message) {
		//@formatter:off
		return message
		.replaceAll("@[a-zA-Z0-9]+", "") //remove mentions
		.replaceAll("[!,\\.]", "") //remove punctuation
		.trim(); //trim for good measure
		//@formatter:on
	}
}
