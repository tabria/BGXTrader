package trader.controller.enums;

public enum SettingsFieldNames {

    INSTRUMENT, QUANTITY, GRANULARITY ;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
