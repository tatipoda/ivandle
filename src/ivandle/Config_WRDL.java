package ivandle;

public class Config_WRDL {

	// Settings
	// Length of the secret word
	static final int WORD_LEN = 5;
	// maximum number of attempts per round(can be changed that is why I did not
	// hardcode it in main, same goes to all setting nums
	static final int MAX_TRIES = 6;
	// number of hints allowed per round
	static final int HINTS_ROUND = 1;

	// COLORS
	// game and big logo colours

	// reset all formatting
	static final String RESET = "\u001B[0m"; // resets all formating
	static final String GREEN = "\u001B[42m\u001B[30m"; // green bg, black text
	static final String YELLOW = "\u001B[43m\u001B[30m"; // yellow bg, black text
	static final String GRAY = "\u001B[100m\u001B[37m"; // gray bg, white text
	static final String CYAN = "\u001B[36m";
	static final String RED = "\u001B[31m";
	static final String LIGHT_GRAY = "\u001B[37m";

	// small logo colours
	static final String FG_GREEN = "\u001B[32m";
	static final String FG_YELLOW = "\u001B[33m";
	static final String FG_GRAY = "\u001B[90m";

	// delay used for animated output(letters appearing one by one)
	static final int TITLE_PAUSE_MS = 120;

	// dictionaries
	// All words must be exactly WORD_LEN letters long and written in CAPS

	static final String[] WORDS_EN = { "ARINA", "APPLE", "BRICK", "CRANE", "DREAM", "EAGLE", "FLAME", "GRAPE", "HOUSE",
			"IVORY", "JOKER", "KNIFE", "LEMON", "MOUSE", "NIGHT", "OCEAN", "PRIDE", "QUEEN", "RIVER", "STONE", "TIGER",
			"UNITY", "VIVID", "WHEAT", "WORLD", "YOUNG", "ZEBRA", "ANGEL", "BLOOM", "CHESS", "DUSTY", "THORN", "KITTY",
			"PLANT", "SMILE", "LIGHT", "CLOUD", "FRAME", "SOUND", "SHAPE", "SWEET", "BRAIN", "TRACK", "POWER", "FAITH",
			"SMART", "ROUND", "HEART"

	};
	static final String[] WORDS_RU = { "АРИНА", "СЛОВО", "КОШКА", "КНИГА", "РЕЧКА", "ЗЕМЛЯ", "ОКЕАН", "ТРАВА", "ПТИЦА",
			"ВЕТЕР", "ДОЖДЬ", "ЛАМПА", "МЫШКА", "РЫБКА", "ПОЛКА", "ОКРАС", "КОВЕР", "САПОГ", "ШАРИК", "ЗВУКИ",
			"БУКВА", "ЦВЕТЫ", "АГЕНТ", "ПЛАСТ", "АДРЕС", "ОГОНЬ", "КРЫША", "РУЧКА", "ПАЛКА", "КОБЗА", "ЧАШКА", "СВАТЫ",
			"САХАР", "ДРОЖЬ", "КИСТЬ", "ПАЧКА", "ХРУСТ", "ПЕСОК", "СПОРТ", "ЛИНИЯ", "ПЛАТА", "СКЛАД", "МЕЧТА", "СЦЕНА",
			"ТЕМНО", "ВКЛАД", "ПОИСК"

	};

	static final String[] WORDS_UA = { "АРІНА", "КОЗАК", "ДУМКА", "ПІСНЯ", "КАЗКА", "САДОК", "ВЕРБА", "СОКІЛ",
			"КОШИК", "ЛЮБОВ", "НОМЕР", "МЕДОК", "ТАНОК", "БЕРЕГ", "КРИЛО", "ГЛИНА", "ХМАРА", "СХЕМА", "ЗАПИТ", "ГІЛКА",
			"ДЕБАГ", "КОБЗА", "ЛОГІН", "ТОКЕН", "ДРАЙВ", "ГОРОД", "ВУЗОЛ", "ФОРМА", "ЛІНІЯ", "РЯДОК", "КІШКА", "МІСТО",
			"РАНОК", "СОНЦЕ", "ПОПІЛ", "ЗРАДА", "КВІТИ", "ШАБЛЯ", "КЕЛИХ", "РІДНЯ", "ЛИСТЯ", "ЗІРКА", "ШАПКА", "ПРАЦЯ",
			"СТІНА"

	};

	// alphabets status tracking (simple concatenation of keyboard rows)
	// if a character is not found here, the guess is rejected
	static final String ALPH_EN = "QWERTYUIOPASDFGHJKLZXCVBNM";
	static final String ALPH_RU = "ЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮЁ";
	static final String ALPH_UA = "ЙЦУКЕНГШЩЗХЇФІВАПРОЛДЖЄЯЧСМИТЬБЮҐ";

}
