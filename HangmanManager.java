
/*  Student information for assignment:
 *
 *  On my honor, Tanvi Agrawal, this programming assignment is my own work
 *  and I have not provided this code to any other student.
 *
 *  Name: Tanvi Agrawal
 *  email address: tanviagrawal@utexas.edu
 *  UTEID: ta25453
 *  Section 5 digit ID: 50215
 *  Grader name: Diego
 *  Number of slip days used on this assignment: 0
 */

// add imports as necessary

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Manages the details of EvilHangman. This class keeps tracks of the possible
 * words from a dictionary during rounds of hangman, based on guesses so far.
 *
 */
public class HangmanManager implements Comparable {

    // instance variables / fields

    private Set<String> setOfWords;
    private boolean deBug;
    private int guessesLeft;
    private int wordLength;
    private HangmanDifficulty difficulty;
    private String pattern;
    private Set<String> newSet;
    private Set<Character> letGuessed;
    private int count;

    /**
     * Create a new HangmanManager from the provided set of words and phrases. pre:
     * words != null, words.size() > 0
     * 
     * @param words   A set with the words for this instance of Hangman.
     * @param debugOn true if we should print out debugging to System.out.
     */
    public HangmanManager(Set<String> words, boolean debugOn) {
        if (words == null || words.size() == 0) {
            throw new IllegalArgumentException("Words cannot be blank");
        }

        setOfWords = new HashSet<>(words);
        deBug = debugOn;

    }

    /**
     * Create a new HangmanManager from the provided set of words and phrases.
     * Debugging is off. pre: words != null, words.size() > 0
     * 
     * @param words A set with the words for this instance of Hangman.
     */
    public HangmanManager(Set<String> words) {
        this(words, false);
    }

    /**
     * Get the number of words in this HangmanManager of the given length. pre: none
     * 
     * @param length The given length to check.
     * @return the number of words in the original Dictionary with the given length
     */
    public int numWords(int length) {
        int count = 0;
        Iterator<String> it = setOfWords.iterator();

        while (it.hasNext()) {
            if (it.next().length() == length) {
                count++;
            }
        }

        return count;
    }

    /**
     * Get for a new round of Hangman. Think of a round as a complete game of
     * Hangman.
     * 
     * @param wordLen    the length of the word to pick this time. numWords(wordLen)
     *                   > 0
     * @param numGuesses the number of wrong guesses before the player loses the
     *                   round. numGuesses >= 1
     * @param diff       The difficulty for this round.
     */
    public void prepForRound(int wordLen, int numGuesses, HangmanDifficulty diff) {
        if (numWords(wordLen) <= 0 || numGuesses < 1) {
            throw new IllegalArgumentException("Lost");
        }
        this.count = 1;
        wordLength = wordLen;
        guessesLeft = numGuesses;
        difficulty = diff;
        pattern = "-".repeat(wordLen);
        newSet = new HashSet<>();
        letGuessed = new HashSet<>();

        Iterator<String> it = setOfWords.iterator();

        while (it.hasNext()) {
            String word = it.next();
            if (word.length() == wordLen) {
                newSet.add(word);
            }
        }

    }

    /**
     * The number of words still possible (live) based on the guesses so far.
     * Guesses will eliminate possible words.
     * 
     * @return the number of words that are still possibilities based on the
     *         original dictionary and the guesses so far.
     */
    public int numWordsCurrent() {
        return newSet.size();
    }

    /**
     * Get the number of wrong guesses the user has left in this round (game) of
     * Hangman.
     * 
     * @return the number of wrong guesses the user has left in this round (game) of
     *         Hangman.
     */
    public int getGuessesLeft() {
        return guessesLeft;
    }

    /**
     * Return a String that contains the letters the user has guessed so far during
     * this round. The characters in the String are in alphabetical order. The
     * String is in the form [let1, let2, let3, ... letN]. For example [a, c, e, s,
     * t, z]
     * 
     * @return a String that contains the letters the user has guessed so far during
     *         this round.
     */
    public String getGuessesMade() {
        StringBuilder sb = new StringBuilder("[");

        ArrayList<Character> sorted = new ArrayList<>(letGuessed);
        Collections.sort(sorted);

        Iterator<Character> it = sorted.iterator();
        boolean tOrF = true;

        while (it.hasNext()) {
            char check = it.next();
            if (!tOrF) {
                sb.append(", ");
            }
            sb.append(check);
            tOrF = false;
        }
        sb.append("]");

        return sb.toString();

    }

    /**
     * Check the status of a character.
     * 
     * @param guess The characater to check.
     * @return true if guess has been used or guessed this round of Hangman, false
     *         otherwise.
     */
    public boolean alreadyGuessed(char guess) {

        if (letGuessed.contains(guess)) {
            return true;
        }
        return false;
    }

    /**
     * Get the current pattern. The pattern contains '-''s for unrevealed (or
     * guessed) characters and the actual character for "correctly guessed"
     * characters.
     * 
     * @return the current pattern.
     */
    public String getPattern() {
        return pattern;
    }

    /**
     * Update the game status (pattern, wrong guesses, word list), based on the give
     * guess.
     * 
     * @param guess pre: !alreadyGuessed(ch), the current guessed character
     * @return return a tree map with the resulting patterns and the number of words
     *         in each of the new patterns. The return value is for testing and
     *         debugging purposes.
     */
    public TreeMap<String, Integer> makeGuess(char guess) {
        if (alreadyGuessed(guess)) {
            throw new IllegalArgumentException("already guessed this letter");
        }
        
        letGuessed.add(guess);
        
        TreeMap<String, ArrayList<String>> possible = allPatterns(guess);
        
        TreeMap<String, Integer> arrayLength = new TreeMap<>();
        
        for (Map.Entry<String, ArrayList<String>> number : possible.entrySet()) {
            arrayLength.put(number.getKey(), number.getValue().size());
        }
        
        String best;
        if (difficulty == HangmanDifficulty.HARD) {
            best = hardestPat(arrayLength);
        } else if (difficulty == HangmanDifficulty.MEDIUM) {
            best = medium(arrayLength);
        } else {
            best = easy(arrayLength);
        }
        
        newSet = new HashSet<>(possible.get(best));
        if (!best.contains(Character.toString(guess))) {
            guessesLeft--;
        }
        this.count++;
        pattern = best;
        return arrayLength;
    }

    /*
     * iterates through the newSet which contains all the possible words
     * and assigns them to a pattern
     * pre: none
     * post: return a TreeMap in which gives all patterns and the words
     * corresponding to the pattern
     */
    public TreeMap<String, ArrayList<String>> allPatterns(char guess) {
        TreeMap<String, ArrayList<String>> possible = new TreeMap<>();

        Iterator<String> it = newSet.iterator();

        while (it.hasNext()) {
            String word = it.next();
            StringBuilder curPattern = new StringBuilder(pattern);
            for (int i = 0; i < word.length(); i++) {
                if (word.charAt(i) == guess) {
                    curPattern.setCharAt(i, guess);
                }
            }
            possible.putIfAbsent(curPattern.toString(), new ArrayList<>());
            possible.get(curPattern.toString()).add(word);
        }

        return possible;
    }

    /*
     * for difficulty medium
     * pre:none
     * post: returns the hardest path or the secondhardest path 
     * based on every 3 iterations
     */
    private String medium(TreeMap<String, Integer> arrayLength) {
        if (this.count % 4 == 0) {
            return secondHardest(arrayLength);
        }
        return hardestPat(arrayLength);
    }

    /*
     * for difficulty easy
     * pre:none
     * post: returns the hardest path or the secondhardest path 
     * based on every 1 iterations
     */
    private String easy(TreeMap<String, Integer> arrayLength) {
        if (this.count % 2 == 0) {
            return secondHardest(arrayLength);
        }
        return hardestPat(arrayLength);
    }

    /*
     * finds the pattern with that is hardest to guess the word
     * pre: none
     * post: returns a pattern of the String with hardest pattern
     */
    private String hardestPat(TreeMap<String, Integer> arrayLength) {

        String bestest = "";
        int max = 0;
        int dashes = 0;

        for (Map.Entry<String, Integer> best : arrayLength.entrySet()) {

            int count = dash(best.getKey());

            if (max < best.getValue() || (max == best.getValue() && 
                    dashes == count && pattern.compareTo(bestest) > 0)
                    || (max == best.getValue() && dashes < count)) {
                bestest = best.getKey();
                max = best.getValue();
                dashes = count;
            }
        }

        return bestest;
    }

    /*
     * finds the pattern with that is the second hardest to guess the word
     * pre: none
     * post: returns a pattern of the String with second hardest pattern
     */
    private String secondHardest(TreeMap<String, Integer> arrayLength) {
        String second = "";
        String first = hardestPat(arrayLength);
        int max = 0;
        int dashes = 0;

        for (Map.Entry<String, Integer> sec : arrayLength.entrySet()) {
            int count = dash(sec.getKey());

            if (max < sec.getValue() || (max == sec.getValue() && 
                    dashes == count && pattern.compareTo(second) > 0)
                    || (max == sec.getValue() && dashes < count)) {

                if (!first.equals(sec.getKey())) {
                    second = sec.getKey();
                    max = sec.getValue();
                    dashes = count;
                }
            }
        }

        return second.equals("") ? first : second;

    }

    /*
     * pre:none
     * post: counts the dashes in a particular pattern
     */
    private int dash(String pattern) {
        int count = 0;
        for (char slash : pattern.toCharArray()) {
            if (slash == '-') {
                count++;
            }
        }
        return count;
    }

    /*
     * Return the secret word this HangmanManager finally 
     * ended up picking for this round. If there are multiple 
     * possible words left one is selected at random.
     * <br> pre: numWordsCurrent() > 0
     * 
     * @return return the secret word the manager picked.
     */
    public String getSecretWord() {
        if (newSet.size() == 1) {
            return newSet.iterator().next();
        } 
        throw new IllegalArgumentException("Not able to compute secret word");
    }

    /*
     * pre:takes a Object o pattern 
     * post: compares it to another pattern
     */
    public int compareTo(Object o) {
        if (o instanceof HangmanManager) {
            HangmanManager other = (HangmanManager) o;
            return this.pattern.compareTo(other.pattern);
        }
        return -1;
    }
}