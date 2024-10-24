package com.fzq.xiaopotato.common.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagUtils {
    private static final Pattern TAG_PATTERN = Pattern.compile("#([a-zA-Z0-9]+)");

    /**
     * Get tags from String
     * @param content string for processing
     * @return List of tags
     */
    public static List<String> extractTags(String content) {
        Set<String> tags = new HashSet<>();

        Matcher matcher = TAG_PATTERN.matcher(content);

        while (matcher.find()) {
            String tag = matcher.group(1);
            if (isValidTag(tag)) {
                tags.add(tag);
            }
        }
        return new ArrayList<>(tags);
    }

    private static boolean isValidTag(String tag) {
        return tag.length() > 0 && tag.length() <= 20;
    }


}
