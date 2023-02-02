package me.roman_shamov.aot.lab1;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class PSMethods {
    private static final String vowels = "а, е, и, о, у, ы, э, ю, я".replace(", ", "");
    private static final HashMap<WordEnding, HashSet<String>> wordEndings = new HashMap<>() {{
        put(WordEnding.PERFECTIVE_GERUND_1, new HashSet<>(List.of("в, вши, вшись".split(", "))));
        put(WordEnding.PERFECTIVE_GERUND_2, new HashSet<>(List.of("ив, ивши, ившись, ыв, ывши, ывшись".split(", "))));
        put(WordEnding.ADJECTIVE, new HashSet<>(List.of("ее, ие, ые, ое, ими, ыми, ей, ий, ый, ой, ем, им, ым, ом, его, ого, ему, ому, их, ых, ую, юю, ая, яя, ою, ею".split(", "))));
        put(WordEnding.PARTICIPLE_1, new HashSet<>(List.of("ем, нн, вш, ющ, щ".split(", "))));
        put(WordEnding.PARTICIPLE_2, new HashSet<>(List.of("ивш, ывш, ующ".split(", "))));
        put(WordEnding.REFLEXIVE, new HashSet<>(List.of("ся, сь".split(", "))));
        put(WordEnding.VERB_1, new HashSet<>(List.of("ла, на, ете, йте, ли, й, л, ем, н, ло, но, ет, ют, ны, ть, ешь, нно".split(", "))));
        put(WordEnding.VERB_2, new HashSet<>(List.of("ила, ыла, ена, ейте, уйте, ите, или, ыли, ей, уй, ил, ыл, им, ым, ен, ило, ыло, ено, ят, ует, уют, ит, ыт, ены, ить, ыть, ишь, ую, ю".split(", "))));
        put(WordEnding.NOUN, new HashSet<>(List.of("ее, ие, ые, ое, ими, ыми, ей, ий, ый, ой, ем, им, ым, ом, его, ого, ему, ому, их, ых, ую, юю, ая, яя, ою, ею".split(", "))));
        put(WordEnding.SUPERLATIVE, new HashSet<>(List.of("ее, ие, ые, ое, ими, ыми, ей, ий, ый, ой, ем, им, ым, ом, его, ого, ему, ому, их, ых, ую, юю, ая, яя, ою, ею".split(", "))));
        put(WordEnding.DERIVATIONAL, new HashSet<>(List.of("ее, ие, ые, ое, ими, ыми, ей, ий, ый, ой, ем, им, ым, ом, его, ого, ему, ому, их, ых, ую, юю, ая, яя, ою, ею".split(", "))));
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
        HashSet<String> foundEnds = new HashSet<>();
        // Шаг 0
        String trimmed = getRV(word);
        if (trimmed.isBlank())
            return "";
        // Шаг 1
        String foundEnd = "";
        for (String end : wordEndings.get(WordEnding.PERFECTIVE_GERUND_1)) {
            int preEndIndex = word.length() - end.length() - 1; // делавши надо 3, длина 7, 7-3 = 4
            if (preEndIndex < 0)
                continue;
            if (trimmed.endsWith(end) && "ая".contains("" + trimmed.charAt(preEndIndex))) {
                if (end.length() > foundEnd.length()) {
                    foundEnd = end;
                }
            }
        }
        // Шаг 2
        // Шаг 3
        // Шаг 4
        return "";
    }

    private static int getMaxLengthOfEndings(Iterable<WordEnding> endingClasses) {
        int maxLength = 0;
        for (WordEnding endingClass : endingClasses) {
            for (String s : wordEndings.get(endingClass)) {
                if (s.length() > maxLength)
                    maxLength = s.length();
            }
        }
        return maxLength;
    }

    private static String checkEnding(String word, Iterable<WordEnding> endingClasses) {
        String foundEnd = "";
        int maxPossibleEndLength = 0;
        for (WordEnding endingClass : endingClasses) {
            for (HashSet<String> set : wordEndings.values()) {
                for (String s : set) {
                    if (s.length() > maxPossibleEndLength)
                        maxPossibleEndLength = s.length();
                }
            }
        }
        for (WordEnding endingClass : endingClasses) {
            for (String end : wordEndings.get(endingClass)) {
                HashSet<WordEnding> firstGroup = new HashSet<>(List.of(WordEnding.PARTICIPLE_1, WordEnding.VERB_1, WordEnding.PERFECTIVE_GERUND_1));
                int preEndIndex = word.length() - end.length() - 1; // делавши надо 3, длина 7, 7-3 = 4
                if ((word.endsWith(end) && !firstGroup.contains(endingClass)) ||
                        (preEndIndex > 0 && word.endsWith(end) && firstGroup.contains(endingClass) && "ая".contains("" + word.charAt(preEndIndex)))) {
                    foundEnd = end;
                    if (foundEnd.length() == maxPossibleEndLength)
                        break;
                }
            }
        }
    }

    public static String getRV(String word) {
        for (int i = 0; i < word.length(); i++) {
            if (vowels.contains("" + word.charAt(i)))
                return word.substring(i);
        }
        return "";
    }

    public static String getR1(String word) {
        return getRV(getRV(word));
    }

    public static String getR2(String word) {
        return getRV(getR1(word));
    }
}
