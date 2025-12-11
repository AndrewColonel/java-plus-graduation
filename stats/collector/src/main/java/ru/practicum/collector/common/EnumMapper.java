package ru.practicum.collector.common;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnumMapper {

    public static <E extends Enum<E>> E toAvroEnum(Class<E> myEnum, String type) {
        E result;
        log.trace("toAvroEnum converting {} to {}",myEnum.getSimpleName(), type.substring(7) );
           try {
            result = Enum.valueOf(myEnum, type.substring(7));
        } catch (IllegalArgumentException exception) {
            // log error or something here
            throw new RuntimeException(String.format("wrong type for enum to convert %s to: %s",
                     myEnum.getSimpleName(), type));
        }
        return result;
    }

}
