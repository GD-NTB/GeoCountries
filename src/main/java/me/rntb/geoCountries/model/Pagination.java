package me.rntb.geoCountries.model;

import java.util.Arrays;
import java.util.List;

public class Pagination {

    public String text;
    public int index;
    public int pageCount;

    public Pagination(String text, int index, int pageCount) {
        this.text = text;
        this.index = index;
        this.pageCount = pageCount;
    }

    public static Pagination paginate(String text, String delimiter, int index, int linesPerPage) {
        if (text == null)
            return null;

        // split string by newlines
        List<String> lines = Arrays.stream(text.split(delimiter)).toList();
        int lineCount = lines.size();

        // split into pages
        int pageCount = Math.ceilDiv(lineCount, linesPerPage);
        int effectiveIndex = Math.clamp(index, 1, pageCount);

        int from = (effectiveIndex - 1) * linesPerPage;
        int to = Math.min(from + linesPerPage, lineCount);

        List<String> pageList = lines.subList(from, to);

        // convert back to string
        String pageString = String.join(delimiter, pageList);
        return new Pagination(pageString, effectiveIndex, pageCount);
    }
}
