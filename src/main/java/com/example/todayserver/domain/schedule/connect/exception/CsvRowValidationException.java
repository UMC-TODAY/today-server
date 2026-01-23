package com.example.todayserver.domain.schedule.connect.exception;

public class CsvRowValidationException extends RuntimeException {

    private final String field;

    public CsvRowValidationException(String field, String message) {
        super(message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
