package model;

public enum EnumCategoria {
    FESTA("Festa", 1),
    EVENTO_ESPORTIVO("Evento Esportivo", 2),
    EVENTO_RELIGIOSO("Evento Religioso", 3),
    SHOW("Show", 4),
    ENCONTRO("Encontro", 5);

    private final String description;
    private final int code;

    private EnumCategoria(String description, int code) {
        this.description = description;
        this.code = code;
    }

    public String getDescription() {
        return this.description;
    }

    public int getCode() {
        return this.code;
    }

    public static EnumCategoria fromCode(int code) {
        for (EnumCategoria categoria : EnumCategoria.values()) {
            if (categoria.code == code) {
                return categoria;
            }
        }
        throw new IllegalArgumentException("Categoria nao encontrada: " + code);
    }
}
