package com.uj.stxtory.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, String> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String convertToDatabaseColumn(LocalDateTime attribute) {
        return attribute != null ? attribute.format(FORMATTER) : null;
    }

    @Override
    public LocalDateTime convertToEntityAttribute(String dbData) {
        return dbData != null ? LocalDateTime.parse(dbData, FORMATTER) : null;
    }
}
