package me.rntb.geoCountries.metadata;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMetadata {

    public static final Map<UUID, Boolean> isMenuOpen = new HashMap<>();
    public static final Map<UUID, String> previousPage = new HashMap<>();
}
