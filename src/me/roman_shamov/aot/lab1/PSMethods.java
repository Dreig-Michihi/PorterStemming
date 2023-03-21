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
        put(WordEnding.ADJECTIVAL_1, new HashSet<>() {{
            HashSet<String> participle = new HashSet<>(get(WordEnding.PARTICIPLE_1));
            for (String part : participle) {
                for (String adj : get(WordEnding.ADJECTIVE)) {
                    add(part + adj);
                }
            }
        }});
        put(WordEnding.ADJECTIVAL_2, new HashSet<>() {{
            addAll(get(WordEnding.ADJECTIVE));
            HashSet<String> participle = new HashSet<>(get(WordEnding.PARTICIPLE_2));
            for (String part : participle) {
                for (String adj : get(WordEnding.ADJECTIVE)) {
                    add(part + adj);
                }
            }
        }});
    }};

    public static String trimWord(String word) {
        System.out.println("\n\n\n\n\ntrimWord(" + word + ").");
        int lenDiff = 0;
        // Шаг 0
        System.out.println("Шаг 0: Получим RV и R2. С буквами ё, заменёнными на е.");
        String rv = getRV(word.trim()).toLowerCase().replace("ё", "е");
        System.out.println("R1("+word.trim()+"): "+getR1(word.trim()).toLowerCase().replace("ё", "е"));
        String r2 = getR2(word.trim()).toLowerCase().replace("ё", "е");
        if (rv.isEmpty()) {
            System.out.println("RV оказалось слишком коротким, поэтому результат: " + word.trim());
            return word.trim();
        }
        System.out.println("Рассматриваемая область RV: " + rv);
        // Шаг 1
        System.out.println("\nШаг 1: Удаляем окончания PERFECTIVE GERUND, или другие.");
        String end = getEnding(rv, List.of(WordEnding.PERFECTIVE_GERUND_1, WordEnding.PERFECTIVE_GERUND_2));
        if (!end.isEmpty()) {
            rv = rv.substring(0, rv.length() - end.length());
            lenDiff += end.length();
            System.out.println("Найдено окончание PERFECTIVE GERUND" + end + ". Удаляем его и переходим к следующему шагу.");
        } else {
            System.out.println("Окончание PERFECTIVE GERUND не найдено.");
            end = getEnding(rv, List.of(WordEnding.REFLEXIVE));
            System.out.println("Окончание REFLEXIVE " + (!end.isEmpty() ? "(" + end + ") найдено. Удаляем его." : "не найдено."));
            rv = rv.substring(0, rv.length() - end.length());
            lenDiff += end.length();
            for (List<WordEnding> endings : List.of(
                    List.of(WordEnding.ADJECTIVAL_1, WordEnding.ADJECTIVAL_2),
                    List.of(WordEnding.VERB_1, WordEnding.VERB_2),
                    List.of(WordEnding.NOUN))) {
                end = getEnding(rv, endings);
                rv = rv.substring(0, rv.length() - end.length());
                lenDiff += end.length();
                if (!end.isEmpty()) {
                    System.out.println("Найдено окончание " + end + " из " + endings + ". Удаляем его и завершаем Шаг 1.");
                    break;
                }
            }
        }
        System.out.println("Рассматриваемая область RV: " + rv);
        // Шаг 2
        System.out.println("\nШаг 2. Удаляем 'и', если есть.");
        if (rv.endsWith("и")) {
            System.out.println();
            rv = rv.substring(0, rv.length() - 1);
            lenDiff += 1;
        }
        System.out.println("Рассматриваемая область RV: " + rv);
        // Шаг 3
        System.out.println("\nШаг 3. Удаляем окончание DERIVATIONAL из R2 (" + r2 + ").");
        end = r2.length() > lenDiff ? getEnding(r2.substring(0, r2.length() - lenDiff), List.of(WordEnding.DERIVATIONAL)) : "";
        if (r2.length() <= lenDiff)
            System.out.println("Длина R2 слишком мала.");
        rv = rv.substring(0, rv.length() - end.length());
        lenDiff += end.length();
        System.out.println("Рассматриваемая область RV: " + rv);
        // Шаг 4
        System.out.println("\nШаг 4. Удаляем окончание SUPERLATIVE и 1 'н', если обрезанное слово оканчивается на 'нн', ЛИБО удаляем мягкий знак, ЛИБО 'н' из 'нн'.");
        end = getEnding(rv, List.of(WordEnding.SUPERLATIVE));
        if (rv.endsWith("ь") || rv.endsWith("нн")) {
            lenDiff += 1;
            System.out.println("Слово оканчивается на '"+(rv.endsWith("ь")?"ь":"нн")+"', удаляем 1 букву.");
        } else if (!end.isEmpty()) {
            System.out.println("Найдено окончание SUPERLATIVE '"+end+"', удаляем его.");
            rv = rv.substring(0, rv.length() - end.length());
            lenDiff += end.length();
            if (rv.endsWith("нн")) {
                System.out.println("Слово оканчивается на 'нн', удаляем 1 букву.");
                lenDiff += 1;
            }
        } else
            System.out.println("Ничего не найдено для Шага 4.");
        System.out.println("РЕЗУЛЬТАТ: " + word.substring(0, word.length() - lenDiff));
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
        System.out.print("getEnding(" + word + ", "+endingClasses+"): ");
        String foundEnd = "";
        int maxPossibleEndLength = getMaxLengthOfEndings(endingClasses);
        for (WordEnding endingClass : endingClasses) {
            for (String end : wordEndings.get(endingClass)) {
                HashSet<WordEnding> firstGroup = new HashSet<>(List.of(WordEnding.PARTICIPLE_1, WordEnding.VERB_1, WordEnding.PERFECTIVE_GERUND_1, WordEnding.ADJECTIVAL_1));
                int preEndIndex = word.length() - end.length() - 1;

                if (word.endsWith(end)
                        && end.length() > foundEnd.length()
                        && (!firstGroup.contains(endingClass)
                        || firstGroup.contains(endingClass) && preEndIndex >= 0 && "ая".contains("" + word.charAt(preEndIndex)))) {
                    if (end.length() == maxPossibleEndLength) {
                        System.out.println("Слово '" + word + "' кончается на '" + end + "'. Длина найденного окончания больше, чем у предыдущего найденного '" + foundEnd + "'.");
                        System.out.println(firstGroup.contains(endingClass) ? "Найденное окончание находится в первой группе " + endingClass.toString() + ", следовательно перед ним должны стоять 'а' или 'я'." :
                                "Найденное окончание не находится в 1й группе, никаких дополнительных проверок не требуется.");
                        System.out.println("Найденное окончание: " + end);
                        return end;
                    }
                    foundEnd = end;
                }
            }
        }
        System.out.println(foundEnd);
        return foundEnd;
    }

    private static String getRV(String word) {
        System.out.print("getRV(" + word + "):");
        word = word.toLowerCase();
        boolean prevVowel = false;
        for (int i = 0; i < word.length(); i++) {
            if (prevVowel) {
                System.out.println(" " + word.substring(i));
                return word.substring(i);
            }
            prevVowel = (vowels.contains("" + word.charAt(i)));
        }
        return "";
    }

    private static String getR1(String word) {
        //System.out.print("getR1(" + word + "):");
        word = word.toLowerCase();
        if (word.length() <= 2)
            return "";
        boolean prevVowel = vowels.contains("" + word.charAt(0));
        for (int i = 1; i < word.length() - 1; i++) {
            boolean currentIsVowel = vowels.contains("" + word.charAt(i));
            if (prevVowel && !currentIsVowel) {
                //System.out.println(" " + word.substring(i + 1));
                return word.substring(i + 1);
            }
            prevVowel = currentIsVowel;
        }
        return "";
    }

    private static String getR2(String word) {
        System.out.println("getR2(" + word + "): " + getR1(getR1(word)));
        return getR1(getR1(word));
    }
}
