package com.frahhs.lightlib.util.logging;

public enum FatalCode {
    TEST_CODE;

    private boolean printable;

    FatalCode(boolean printable) {
        this.printable = printable;
    }

    FatalCode() {
        this(false);
    }

    public boolean isPrintable() {
        return printable;
    }

    public void setPrintable(boolean printable) {
        this.printable = printable;
    }
}
