package me.roman_shamov.aot.lab1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PSMethods {
    private static final String vowels = "а, е, и, о, у, ы, э, ю, я".replace(", ", "");
    private static final int MAX_END_LENGTH = 6; // Длина окончаний "ившись", "ывшись", и некоторых ADJECTIVAL.
    private static final HashMap<WordEnding, HashSet<String>> wordEndings = new HashMap<>() {{
        put(WordEnding.PERFECTIVE_GERUND_1, new HashSet<>(List.of("в, вши, вшись".split(", "))));
        put(WordEnding.PERFECTIVE_GERUND_2, new HashSet<>(List.of("ив, ивши, ившись, ыв, ывши, ывшись".split(", "))));
        put(WordEnding.ADJECTIVE, new HashSet<>(List.of("ее, ие, ые, ое, ими, ыми, ей, ий, ый, ой, ем, им, ым, ом, его, ого, ему, ому, их, ых, ую, юю, ая, яя, ою, ею".split(", "))));
        put(WordEnding.PARTICIPLE_1, new HashSet<>(List.of("ем, нн, вш, ющ, щ".split(", "))));
        put(WordEnding.PARTICIPLE_2, new HashSet<>(List.of("ивш, ывш, ующ".split(", "))));
        put(WordEnding.REFLEXIVE, new HashSet<>(List.of("ся, сь".split(", "))));
        put(WordEnding.VERB_1, new HashSet<>(List.of("ла, на, ете, йте, ли, й, л, ем, н, ло, но, ет, ют, ны, ть, ешь, нно".split(", "))));
        put(WordEnding.VERB_2, new HashSet<>(List.of("ила, ыла, ена, ейте, уйте, ите, или, ыли, ей, уй, ил, ыл, им, ым, ен, ило, ыло, ено, ят, ует, уют, ит, ыт, ены, ить, ыть, ишь, ую, ю".split(", "))));
        put(WordEnding.NOUN, new HashSet<>(List.of("а, ев, ов, ие, ье, е, иями, ями, ами, еи, ии, и, ией, ей, ой, ий, й, иям, ям, ием, ем, ам, ом, о, у, ах, иях, ях, ы, ь, ию, ью, ю, ия, ья, я".split(", "))));
        put(WordEnding.SUPERLATIVE, new HashSet<>(List.of("ейш, ейше".split(", "))));
        put(WordEnding.DERIVATIONAL, new HashSet<>(List.of("ост, ость".split(", "))));
        put(WordEnding.ADJECTIVAL, new HashSet<>() {{
            addAll(get(WordEnding.ADJECTIVE));
            HashSet<String> participle = new HashSet<>(get(WordEnding.PARTICIPLE_1));
            participle.addAll(get(WordEnding.PARTICIPLE_2));
            for (String part : participle) {
                for (String adj : get(WordEnding.ADJECTIVE)) {
                    add(part + adj);
                }
            }
        }});
    }};

    public static String trimWord(String word) {
        int lenDiff = 0;
        // Шаг 0
        String rv = getRV(word.trim()).toLowerCase().replace("ё", "е");
        String r2 = getR2(word.trim()).toLowerCase().replace("ё", "е");
        if (rv.isEmpty())
            return word.trim();
        // Шаг 1
        String end = getEnding(rv, List.of(WordEnding.PERFECTIVE_GERUND_1, WordEnding.PERFECTIVE_GERUND_2));
        if (!end.isEmpty()) {
            rv = rv.substring(0, rv.length() - end.length());
            lenDiff += end.length();
        } else {
            end = getEnding(rv, List.of(WordEnding.REFLEXIVE));
            rv = rv.substring(0, rv.length() - end.length());
            lenDiff += end.length();
            for (List<WordEnding> endings : List.of(
                    List.of(WordEnding.ADJECTIVAL),
                    List.of(WordEnding.VERB_1, WordEnding.VERB_2),
                    List.of(WordEnding.NOUN))) {
                end = getEnding(rv, endings);
                rv = rv.substring(0, rv.length() - end.length());
                lenDiff += end.length();
                if (!end.isEmpty())
                    break;
            }
        }
        // Шаг 2
        if (rv.endsWith("и")) {
            rv = rv.substring(0, rv.length() - 1);
            lenDiff += 1;
        }
        // Шаг 3
        end = r2.length() > lenDiff ? getEnding(r2.substring(0, r2.length() - lenDiff), List.of(WordEnding.DERIVATIONAL)) : "";
        rv = rv.substring(0, rv.length() - end.length());
        lenDiff += end.length();
        // Шаг 4
        end = getEnding(rv, List.of(WordEnding.SUPERLATIVE));
        if (rv.endsWith("ь") || rv.endsWith("нн")) {
            lenDiff += 1;
        } else if (!end.isEmpty()) {
            rv = rv.substring(0, rv.length() - end.length());
            lenDiff += end.length();
            if (rv.endsWith("нн")) {
                lenDiff += 1;
            }
        }
        return word.substring(0, word.length() - lenDiff);
    }

    private static int getMaxLengthOfEndings(Iterable<WordEnding> endingClasses) {
        int maxPossibleEndLength = 0;
        for (WordEnding endingClass : endingClasses) {
            for (String s : wordEndings.get(endingClass)) {
                if (s.length() > maxPossibleEndLength)
                    maxPossibleEndLength = s.length();
                if (maxPossibleEndLength >= MAX_END_LENGTH)
                    return maxPossibleEndLength;
            }
        }
        return maxPossibleEndLength;
    }

    private static String getEnding(String word, Iterable<WordEnding> endingClasses) {
        return getEnding(word, endingClasses, false);
    }

    private static String getEnding(String word, Iterable<WordEnding> endingClasses, boolean trimRV) {
        if (trimRV)
            word = getRV(word.trim());
        String foundEnd = "";
        int maxPossibleEndLength = getMaxLengthOfEndings(endingClasses);
        for (WordEnding endingClass : endingClasses) {
            for (String end : wordEndings.get(endingClass)) {
                HashSet<WordEnding> firstGroup = new HashSet<>(List.of(WordEnding.PARTICIPLE_1, WordEnding.VERB_1, WordEnding.PERFECTIVE_GERUND_1));
                int preEndIndex = word.length() - end.length() - 1;
                if (word.endsWith(end)
                        && end.length() > foundEnd.length()
                        && (!firstGroup.contains(endingClass)
                        || firstGroup.contains(endingClass) && preEndIndex >= 0 && "ая".contains("" + word.charAt(preEndIndex)))) {
                    if (end.length() == maxPossibleEndLength)
                        return end;
                    foundEnd = end;
                }
            }
        }
        return foundEnd;
    }

    private static String getRV(String word) {
        word = word.toLowerCase();
        boolean prevVowel = false;
        for (int i = 0; i < word.length(); i++) {
            if (prevVowel)
                return word.substring(i);
            prevVowel = (vowels.contains("" + word.charAt(i)));
        }
        return "";
    }

    private static String getR1(String word) {
        word = word.toLowerCase();
        if (word.length() <= 2)
            return "";
        boolean prevVowel = vowels.contains("" + word.charAt(0));
        for (int i = 1; i < word.length() - 1; i++) {
            boolean currentIsVowel = vowels.contains("" + word.charAt(i));
            if (prevVowel && !currentIsVowel)
                return word.substring(i + 1);
            prevVowel = currentIsVowel;
        }
        return "";
    }

    private static String getR2(String word) {
        return getR1(getR1(word));
    }
}
