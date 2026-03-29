package me.rntb.geoCountries.type;

import java.util.Arrays;
import java.util.List;

public record Pagination (Object content, int pageIndex, int pageCount) {

    public static final Pagination EMPTY = new Pagination(null, 0, 0);

    // content is returned as null on empty or null list
    public static <T> Pagination paginate(List<T> list, int wantedPage, int perPage) {
        if (list == null || list.isEmpty())
            return EMPTY;

        int pageCount = getPageCount(list.size(), perPage);
        int pageIndex = getPageIndex(wantedPage, pageCount);

        int from = (pageIndex - 1) * perPage;
        int to = Math.min(from + perPage, list.size());
        List<T> content = list.subList(from, to); // so much easier

        return new Pagination(content, pageIndex, pageCount);
    }

    public static Pagination paginate(String text, String delimiter, int wantedPage, int perPage) {
        if (text == null || text.isEmpty())
            return EMPTY;

        List<String> splitText = Arrays.stream(text.split(delimiter)).toList();
        Pagination pagination = paginate(splitText, wantedPage, perPage);
        String pageText = String.join(delimiter, (List<String>) pagination.content);

        return new Pagination(pageText, pagination.pageIndex, pagination.pageCount);
    }

    private static int getPageCount(int listSize, int perPage) {
        return (int) Math.ceil((double) listSize / perPage);
    }

    private static int getPageIndex(int currentPage, int pageCount) {
        if (pageCount < 1)
            return 1;
        else
            return Math.clamp(currentPage, 1, pageCount);
    }
}