package com.pharmacy.ThaiDuongPharmacyAPI.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public class SlugUtils {

    private static final Pattern NONLATIN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

    public static String toSlug(String input) {
        if (input == null) return "";

        String nowhitespace = WHITESPACE.matcher(input.toLowerCase()).replaceAll("-");

        String normalized = Normalizer.normalize(nowhitespace, Normalizer.Form.NFD);

        String slug = NONLATIN.matcher(normalized).replaceAll("");

        return slug.replaceAll("-{2,}", "-");
    }
}
