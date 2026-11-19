package ivandle;

import java.util.Random;
import java.util.Scanner;
import java.util.Locale;

//This class contains the entire IVANDLE game logic (menu + gameplay + UI output).
public class GameMain {

	public static void main(String[] args) {
		Scanner scan = new Scanner(System.in);
		Random rand = new Random();

		// for statistics (counts games only during the program runs
		int gamesPlayed = 0;
		int gamesWon = 0;
		int gamesLost = 0;

		// start screen
		clearScreen();
		printTitleBig();

		int language = chooseLanguage(scan);

		// main menu loop. Depending on the input for "choice" calls the corresponding
		// method
		boolean menu = true;
		while (menu) {
			clearScreen();
			printTitleSmall();

			int choice = menuChoice(scan);

			// option 1 to start a new round by picking a random word
			if (choice == 1) {
				String answer = pickRandomWord(language, rand);

				// System.out.println("DEBUG: random word = " + answer); //debugging line for
				// test purposes. Uncomment it to see the word before playing

				gamesPlayed++;

				// playRound returns 1-win -1=lose
				int result = playRound(scan, answer, language);

				if (result == 1)
					gamesWon++;
				else if (result == -1)
					gamesLost++;
			}
			// option 2 show help/rules screen
			else if (choice == 2) {
				showHelp(scan);
			}

			// option 3 change language
			else if (choice == 3) {
				language = chooseLanguage(scan);
			}
			// option 4 to see the statistics
			else if (choice == 4) {
				showStats(gamesPlayed, gamesWon, gamesLost, scan);
			}

			// option 0 quit program
			else if (choice == 0) {
				menu = false;
			}
		}
		System.out.println(Config_WRDL.CYAN + "It was nice to play with you. See you next time!" + Config_WRDL.RESET);

		scan.close();
	}// main close(this type of // are for me to not get lost)

	// this method prints the menu and forces the user to enter a valid option
	// It keeps asking until input is correct

	static int menuChoice(Scanner scan) {
		while (true) {
			printMenuBox();

			String input = scan.nextLine().trim();// trim so that 1-4(space) also works

			if (input.equals("1"))
				return 1;
			if (input.equals("2"))
				return 2;
			if (input.equals("3"))
				return 3;
			if (input.equals("4"))
				return 4;
			if (input.equals("0") || input.equalsIgnoreCase("quit"))
				return 0;

			// Invalid menu input => shows error and let user try again
			System.out.println(Config_WRDL.RED + "Do not know what you have tried, but it was wrong, so TRY AGAIN!"
					+ Config_WRDL.RESET);
			pressEnter(scan);
		}
	}

	// Help screen explains rules + colours + commands
	static void showHelp(Scanner scan) {
		clearScreen();
		printTitleSmall();

		System.out.println("Rules:");
		System.out.println(
				"- Guess the " + Config_WRDL.WORD_LEN + "-letter word in " + Config_WRDL.MAX_TRIES + " tries -");
		System.out.println("- After each guess you see colors: -");
		System.out.println(" " + Config_WRDL.GREEN + " A " + Config_WRDL.RESET + " correct letter, correct place");
		System.out.println(" " + Config_WRDL.YELLOW + " A " + Config_WRDL.RESET + " correct letter, wrong place");
		System.out.println(" " + Config_WRDL.GRAY + " A " + Config_WRDL.RESET + " wrong letter, wrong place");
		System.out.println(
				"- Command: type 'quit' during a round to exit the round. Type 'hint' to open one letter (hints per round: "
						+ Config_WRDL.HINTS_ROUND + ").");
		System.out.println();

		pressEnter(scan);
	}

	// requests a player to enter something to continue. Basically a pause to let us
	// read the system message before it clears
	static void pressEnter(Scanner scan) {
		System.out.println("Press Enter...");
		scan.nextLine();
	}

	// just prints many empty lines to "clear" terminal
	static void clearScreen() {
		for (int i = 0; i < 25; i++)
			System.out.println();
	}

	// lets the player choose language for gameplay (word list + alphabet
	// validation)
	static int chooseLanguage(Scanner scan) {
		while (true) {
			printLanguageBox();

			String input = scan.nextLine().trim();

			if (input.equals("1"))
				return 1;
			if (input.equals("2"))
				return 2;
			if (input.equals("3"))
				return 3;

			System.out.println(Config_WRDL.RED + "Do not know what you have tried, but it was wrong, so TRY AGAIN!"
					+ Config_WRDL.RESET);
			pressEnter(scan);
			clearScreen();
		} // while close
	}// cL close

	// picks a random word from the correct dictionary based on language
	static String pickRandomWord(int language, Random rand) {
		String[] words = getWordsByLanguage(language);
		int index = rand.nextInt(words.length);
		return words[index];
	}

	// returns a dictionary array for chosen language
	static String[] getWordsByLanguage(int language) {
		if (language == 1)
			return Config_WRDL.WORDS_EN;
		if (language == 2)
			return Config_WRDL.WORDS_UA;
		return Config_WRDL.WORDS_RU;

	}

	// this is the MAIN gameplay loop for one round.
	// player has MAX_TRIES attempts to guess the secret word.
	// special commands:
	// - hint: reveals one correct letter position (does not consume a try)
	// - quit: exits the round
	// returns: 1=win, -1=lose for statistics (quit also counts as loss)

	static int playRound(Scanner scan, String answer, int language) {
		System.out.println("Round started! (type 'hint' to reveal one letter or 'quit' to exit)");

		int triesUsed = 0;

		int hintsLeft = Config_WRDL.HINTS_ROUND;

		// forcedGreen[i] = true means: "this position was revealed by a hint"
		// (Used only to display the hint line, NOT to force future guesses to be green)
		boolean[] forcedGreen = new boolean[Config_WRDL.WORD_LEN];
		boolean[] knownGreen = new boolean[Config_WRDL.WORD_LEN];

		System.out.println("Hints left: " + hintsLeft);
		System.out.println();

		while (triesUsed < Config_WRDL.MAX_TRIES) {

			System.out.println("Enter a " + Config_WRDL.WORD_LEN
					+ "-letter word: (type 'hint' to reveal one letter or 'quit' to exit)");
			String input = scan.nextLine().trim();

			// hint block:
			// hint reveals one position from the answer that is:
			// 1)not already guessed correctly (knownGreen == false)
			// 2)not already revealed by a previous hint (forcedGreen == false)
			// hint does NOT consume a try

			if (input.equalsIgnoreCase("hint")) {

				if (hintsLeft > 0) {
					int pos = -1;

					// find the first position that is still unknown to the player
					for (int i = 0; i < Config_WRDL.WORD_LEN; i++) {
						if (!forcedGreen[i] && !knownGreen[i]) {
							pos = i;
							break;
						}
					}

					if (pos != -1) {
						forcedGreen[pos] = true;// mark position as revealed
						hintsLeft--;

						System.out.println("Hint used!");
						printHintLine(answer, forcedGreen);
						System.out.println("Hints left: " + hintsLeft);
						System.out.println();
					} else {
						System.out.println("All letters already revealed.");
					}
				} else {
					System.out.println("No hints left.");
				}

				continue; // skip the rest of loop to not consume a try
			}

			// QUIT command
			if (input.equalsIgnoreCase("quit")) {
				System.out.println(
						Config_WRDL.RED + "Mom raised a quitter I see. The word was: " + answer + Config_WRDL.RESET);
				pressEnter(scan);
				return -1; // personal note: maybe change. Do not know whether to consider quit a loss or
							// no
			}

			String guess = input.toUpperCase(Locale.ROOT);

			// 1 length check
			if (guess.length() != Config_WRDL.WORD_LEN) {
				System.out.println("Wrong length! Your word has to be exactly " + Config_WRDL.WORD_LEN + " letters.");
				continue;
			}

			// 2 language check since the game operates with languages that use different
			// alphabet
			if (!isValidForLanguage(guess, language)) {
				System.out.println("Invalid characters for chosen language.");
				System.out.println();
				continue;
			}

			// 3 consume a valid try
			triesUsed++;

			// updates knownGreen so hints do not waste a reveal on positions already
			// guessed correctly
			for (int i = 0; i < Config_WRDL.WORD_LEN; i++) {
				if (guess.charAt(i) == answer.charAt(i)) {
					knownGreen[i] = true;
				}
			}

			// 4 evaluate + print coloured tiles
			char[] status = evaluateGuess(answer, guess);
			printGuess(guess, status);

			// win check
			if (guess.equals(answer)) {
				System.out.println("Winner winner chicken dinner! You guessed the word in " + triesUsed + " tries");
				pressEnter(scan);
				return 1;
			}

			int triesLeft = Config_WRDL.MAX_TRIES - triesUsed;
			System.out.println("Not correct. Tries left: " + triesLeft);

		} // pl Round while close

		System.out.println("LOSE! The Word was: " + answer);
		pressEnter(scan);
		return -1;
	}// pl Round close

	// next method compares guess to answer and returns an array of statuses for
	// each letter:
	// 'G' = Green (correct letter, correct position)
	// 'Y' = Yellow (correct letter, wrong position)
	// 'S' = Gray (letter not in word)
	// uses a "used[]" array to correctly handle repeated letters.
	static char[] evaluateGuess(String answer, String guess) {
		char[] res = new char[Config_WRDL.WORD_LEN];
		boolean[] used = new boolean[Config_WRDL.WORD_LEN];

		// Pass 1: mark all exact matches as Green and lock those answer positions as
		// used
		for (int i = 0; i < Config_WRDL.WORD_LEN; i++) {

			if (guess.charAt(i) == answer.charAt(i)) {
				res[i] = 'G';
				used[i] = true;
			}
		}

		// Pass 2: for remaining letters, search for an unused matching letter in the
		// answer
		// if found Yellow, else Siryi(gray)
		for (int i = 0; i < Config_WRDL.WORD_LEN; i++) {// i
			if (res[i] == 'G')
				continue;

			char g = guess.charAt(i);
			int found = -1;

			for (int k = 0; k < Config_WRDL.WORD_LEN; k++) {// ii
				if (!used[k] && answer.charAt(k) == g) {
					found = k;
					break;
				}
			} // ii close

			if (found != -1) {
				res[i] = 'Y';
				used[found] = true;
			}

			else {
				res[i] = 'S';
			} // G is taken by green, so we use S as Siryi means gray in UA

		} // i close

		return res;
	}// evaluate close

	// next block is responsible for colouring the tiles
	static void printGuess(String guess, char[] status) {
		for (int i = 0; i < Config_WRDL.WORD_LEN; i++) {
			char ch = guess.charAt(i);
			char ch2 = status[i];

			if (ch2 == 'G') {
				System.out.print(Config_WRDL.GREEN + " " + ch + " " + Config_WRDL.RESET + " ");
			} else if (ch2 == 'Y') {
				System.out.print(Config_WRDL.YELLOW + " " + ch + " " + Config_WRDL.RESET + " ");
			} else {
				System.out.print(Config_WRDL.GRAY + " " + ch + " " + Config_WRDL.RESET + " ");
			}

			sleepMs(Config_WRDL.TITLE_PAUSE_MS);

		}
		System.out.println();
	}

	// timer block so that the letters are revealed like in the actual wordle
	static void sleepMs(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();

		}

	}// sleepMs close

	// since in programm's Config Class we have alphabets for each language, this
	// block checks that all letters in the guess belong to the selected alphabet
	// prevents mixing alphabets (e.g., English letters in Ukrainian mode)

	static boolean isValidForLanguage(String guess, int language) {
		String alphabet;

		if (language == 1)
			alphabet = Config_WRDL.ALPH_EN;
		else if (language == 2)
			alphabet = Config_WRDL.ALPH_UA;
		else
			alphabet = Config_WRDL.ALPH_RU;

		for (int i = 0; i < guess.length(); i++) {
			char ch = guess.charAt(i);
			if (alphabet.indexOf(ch) == -1) {
				return false;
			}
		}
		return true;
	}

	// prints big logo and colours it
	static void printTitleBig() {
		String[] art = {
				"                                                                                                                                                   ",
				"                                                                                                                                                   ",
				"IIIIIIIIIIVVVVVVVV           VVVVVVVV   AAA               NNNNNNNN        NNNNNNNNDDDDDDDDDDDDD      LLLLLLLLLLL             EEEEEEEEEEEEEEEEEEEEEE",
				"I::::::::IV::::::V           V::::::V  A:::A              N:::::::N       N::::::ND::::::::::::DDD   L:::::::::L             E::::::::::::::::::::E",
				"I::::::::IV::::::V           V::::::V A:::::A             N::::::::N      N::::::ND:::::::::::::::DD L:::::::::L             E::::::::::::::::::::E",
				"II::::::IIV::::::V           V::::::VA:::::::A            N:::::::::N     N::::::NDDD:::::DDDDD:::::DLL:::::::LL             EE::::::EEEEEEEEE::::E",
				"  I::::I   V:::::V           V:::::VA:::::::::A           N::::::::::N    N::::::N  D:::::D    D:::::D L:::::L                 E:::::E       EEEEEE",
				"  I::::I    V:::::V         V:::::VA:::::A:::::A          N:::::::::::N   N::::::N  D:::::D     D:::::DL:::::L                 E:::::E             ",
				"  I::::I     V:::::V       V:::::VA:::::A A:::::A         N:::::::N::::N  N::::::N  D:::::D     D:::::DL:::::L                 E::::::EEEEEEEEEE   ",
				"  I::::I      V:::::V     V:::::VA:::::A   A:::::A        N::::::N N::::N N::::::N  D:::::D     D:::::DL:::::L                 E:::::::::::::::E   ",
				"  I::::I       V:::::V   V:::::VA:::::A     A:::::A       N::::::N  N::::N:::::::N  D:::::D     D:::::DL:::::L                 E:::::::::::::::E   ",
				"  I::::I        V:::::V V:::::VA:::::AAAAAAAAA:::::A      N::::::N   N:::::::::::N  D:::::D     D:::::DL:::::L                 E::::::EEEEEEEEEE   ",
				"  I::::I         V:::::V:::::VA:::::::::::::::::::::A     N::::::N    N::::::::::N  D:::::D     D:::::DL:::::L                 E:::::E             ",
				"  I::::I          V:::::::::VA:::::AAAAAAAAAAAAA:::::A    N::::::N     N:::::::::N  D:::::D    D:::::D L:::::L         LLLLLL  E:::::E       EEEEEE",
				"II::::::II         V:::::::VA:::::A             A:::::A   N::::::N      N::::::::NDDD:::::DDDDD:::::DLL:::::::LLLLLLLLL:::::LEE::::::EEEEEEEE:::::E",
				"I::::::::I          V:::::VA:::::A               A:::::A  N::::::N       N:::::::ND:::::::::::::::DD L::::::::::::::::::::::LE::::::::::::::::::::E",
				"I::::::::I           V:::VA:::::A                 A:::::A N::::::N        N::::::ND::::::::::::DDD   L::::::::::::::::::::::LE::::::::::::::::::::E",
				"IIIIIIIIII            VVVAAAAAAA                   AAAAAAANNNNNNNN         NNNNNNNDDDDDDDDDDDDD      LLLLLLLLLLLLLLLLLLLLLLLLEEEEEEEEEEEEEEEEEEEEEE",
				"                                                                                                                                                   ",
				"                                                                                                                                                   ",
				"                                                                                                                                                   " };

		System.out.println(Config_WRDL.CYAN);
		for (int i = 0; i < art.length; i++) {
			System.out.println(art[i]);
		}
		System.out.println(Config_WRDL.RESET);
	}

	// prints small logo and colours it. Created in a different to big logo's stile
	// since it is coloured in different colours and ASCII stile is different as
	// well
	static void printTitleSmall() {

		String g = Config_WRDL.FG_GREEN;
		String y = Config_WRDL.FG_YELLOW;
		String gr = Config_WRDL.FG_GRAY;
		String r = Config_WRDL.RESET;

		System.out.println(
				g + "██╗" + g + "██╗" + g + "   ██╗ █████╗ ███╗   ██╗" + gr + "██████╗ " + y + "██╗     ███████╗" + r);

		System.out.println(
				g + "██║" + g + "██║" + g + "   ██║██╔══██╗████╗  ██║" + gr + "██╔══██╗" + y + "██║     ██╔════╝" + r);

		System.out.println(
				g + "██║" + g + "██║" + g + "   ██║███████║██╔██╗ ██║" + gr + "██║  ██║" + y + "██║     █████╗  " + r);

		System.out.println(
				g + "██║" + g + "╚██╗" + g + " ██╔╝██╔══██║██║╚██╗██║" + gr + "██║  ██║" + y + "██║     ██╔══╝  " + r);

		System.out.println(
				g + "██║" + g + " ╚████╔╝" + g + " ██║  ██║██║ ╚████║" + gr + "██████╔╝" + y + "███████╗███████╗" + r);

		System.out.println(
				g + "╚═╝" + g + "  ╚═══╝ " + g + " ╚═╝  ╚═╝╚═╝  ╚═══╝" + gr + "╚═════╝ " + y + "╚══════╝╚══════╝" + r);

		System.out.println();
	}

	// colours the revealed by hint command letter in green by first finding it and
	// then colouring
	static void printHintLine(String answer, boolean[] forcedGreen) {
		for (int i = 0; i < Config_WRDL.WORD_LEN; i++) {
			if (forcedGreen[i]) {
				System.out.print(Config_WRDL.GREEN + " " + answer.charAt(i) + " " + Config_WRDL.RESET + " ");
			} else {
				System.out.print(Config_WRDL.GRAY + "   " + Config_WRDL.RESET + " ");
			}
			sleepMs(Config_WRDL.TITLE_PAUSE_MS);
		}
		System.out.println();
	}

	// just menu printing, no functional
	static void printMenuBox() {
		String c = Config_WRDL.LIGHT_GRAY;// you cannot even see the difference between this colour and white. I just
											// didn't want to change c+....+r in the menu boxes
		String r = Config_WRDL.RESET;

		System.out.println(c + "     ╔══════════════════════════════╗" + r);
		System.out.println(c + "     ║  ▶ 1) New Game               ║" + r);
		System.out.println(c + "     ║  ▶ 2) Help / Rules           ║" + r);
		System.out.println(c + "     ║  ▶ 3) Change Language        ║" + r);
		System.out.println(c + "     ║  ▶ 4) Statistics             ║" + r);
		System.out.println(c + "     ║                              ║" + r);
		System.out.println(c + "     ║  ▶ 0) Quit                   ║" + r);
		System.out.println(c + "     ╚══════════════════════════════╝" + r);
		System.out.print(c + "The choice is yours: " + r);
	}

	// just menu printing, no functional
	static void printLanguageBox() {
		String c = Config_WRDL.LIGHT_GRAY;
		String r = Config_WRDL.RESET;

		System.out.println(c + "╔══════════════════════════════╗" + r);
		System.out.println(c + "║        CHOOSE LANGUAGE       ║" + r);
		System.out.println(c + "╠══════════════════════════════╣" + r);
		System.out.println(c + "║  ▶ 1) English                ║" + r);
		System.out.println(c + "║  ▶ 2) Українська             ║" + r);
		System.out.println(c + "║  ▶ 3) Русский                ║" + r);
		System.out.println(c + "╚══════════════════════════════╝" + r);
		System.out.println(c + "Language selection affects gameplay only :P" + r);
		System.out.println(c + "The UI remains in English so you can practice your language knowledge xD" + r);
		System.out.println();
	}

	// decided to use a little bit of math in this project, so this block is
	// responsible for statistics.
	// It collects the num of wins and loses counted in the main gameplay block and
	// then just prints them
	// the win rate is also calculated here
	static void showStats(int played, int won, int lost, Scanner scan) {
		clearScreen();
		System.out.println("Games played: " + played);
		System.out.println("Wins: " + won);
		System.out.println("Losses: " + lost);
		double winRate;
		// prevents division by zero if no games played yet
		if (played == 0) {
			winRate = 0;
		}

		else {
			winRate = (won * 100.0) / played;
		}
		System.out.printf("Win rate: %.2f%%%n", winRate);
		System.out.println();
		pressEnter(scan);
	}

}
