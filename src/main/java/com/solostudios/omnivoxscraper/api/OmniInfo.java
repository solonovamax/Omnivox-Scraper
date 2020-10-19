package com.solostudios.omnivoxscraper.api;

@SuppressWarnings({ "ConstantConditions", "unused" })
public class OmniInfo {
    public static final String GITHUB           = "https://github.com/solonovamax/Omnivox-Scraper";
    public static final String VERSION_MAJOR    = "@versionMajor@";
    public static final String VERSION_MINOR    = "@versionMinor@";
    public static final String VERSION_PATCH    = "@versionRevision@";
    public static final String PRE_RELEASE_DATA = "@preReleaseData@";
    public static final String GIT_HASH         = "@gitHash@";
    public static final String VERSION          = VERSION_MAJOR.startsWith("@") ? "dev" : String.format("%s.%s.%s-%s+%s", VERSION_MAJOR,
                                                                                                        VERSION_MINOR, VERSION_PATCH,
                                                                                                        PRE_RELEASE_DATA, GIT_HASH);
    
    /**
     * No fun allowed
     */
    private OmniInfo() {
    }
}
