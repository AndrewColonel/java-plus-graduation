package ru.practicum.collector.common;

public class EnumMapper {

    public static <E extends Enum<E>> E toAvroEnum(Class<E> myEnum, String type) {
        E result;
        try {
            result = Enum.valueOf(myEnum, type);
        } catch (IllegalArgumentException exception) {
            // log error or something here
            throw new RuntimeException(String.format("Неверный тип перечислимого для enum %s : %s",
                     myEnum.getSimpleName(), type));
        }
        return result;
    }

}
