package org.springframework.samples.petclinic.admin;

public enum Constant {

    ALLOWED_ORIGIN_PATTERNS(new String[]{"http://localhost:[*]"});


    private final String[] allowedOriginPatterns;

    Constant(String[] allowedOriginPatterns) {
        this.allowedOriginPatterns = allowedOriginPatterns;
    }

    public String[] get() {
        return allowedOriginPatterns;
    }
}
