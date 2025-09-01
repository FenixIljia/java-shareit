package ru.practicum.shareit.controller;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class ErrorHandlerTest {

    @InjectMocks
    private ErrorHandler errorHandler;

    @Test
    void handleNotFound_ShouldReturnNotFoundStatus() {
        // Given
        String errorMessage = "Resource not found";
        NotFoundException exception = new NotFoundException(errorMessage);

        // When
        ExceptionResponse response = errorHandler.handleNotFound(exception);

        // Then
        assertNotNull(response);
        assertEquals(errorMessage, response.getMessage());
    }

    @Test
    void handlerValidationException_ShouldReturnBadRequestStatus() {
        // Given
        String errorMessage = "Validation failed";
        ValidationException exception = new ValidationException(errorMessage);

        // When
        ExceptionResponse response = errorHandler.handlerValidationException(exception);

        // Then
        assertNotNull(response);
        assertEquals(errorMessage, response.getMessage());
    }

    @Test
    void handlerDuplicationDataException_ShouldReturnConflictStatus() {
        // Given
        String errorMessage = "Duplicate data";
        DuplicatedDataException exception = new DuplicatedDataException(errorMessage);

        // When
        ExceptionResponse response = errorHandler.handlerDuplicationDataException(exception);

        // Then
        assertNotNull(response);
        assertEquals(errorMessage, response.getMessage());
    }

    @Test
    void handlerException_AccessRightException_ShouldReturnForbiddenStatus() {
        // Given
        String errorMessage = "Access denied";
        AccessRightException exception = new AccessRightException(errorMessage);

        // When
        ExceptionResponse response = errorHandler.handlerException(exception);

        // Then
        assertNotNull(response);
        assertEquals(errorMessage, response.getMessage());
    }

    @Test
    void handlerException_ValidationDataException_ShouldReturnBadRequestStatus() {
        // Given
        String errorMessage = "Data validation failed";
        ValidationDataException exception = new ValidationDataException(errorMessage);

        // When
        ExceptionResponse response = errorHandler.handlerException(exception);

        // Then
        assertNotNull(response);
        assertEquals(errorMessage, response.getMessage());
    }

    @Test
    void handlerException_NoAccess_ShouldReturnInternalServerErrorStatus() {
        // Given
        String errorMessage = "No access";
        NoAccess exception = new NoAccess(errorMessage);

        // When
        ExceptionResponse response = errorHandler.handlerException(exception);

        // Then
        assertNotNull(response);
        assertEquals(errorMessage, response.getMessage());
    }

    @Test
    void handlerException_NotCompletedBooking_ShouldReturnBadRequestStatus() {
        // Given
        String errorMessage = "Booking not completed";
        NotCompletedBooking exception = new NotCompletedBooking(errorMessage);

        // When
        ExceptionResponse response = errorHandler.handlerException(exception);

        // Then
        assertNotNull(response);
        assertEquals(errorMessage, response.getMessage());
    }

    // Тест для проверки, что все исключения возвращают правильный HTTP статус
    @Test
    void allExceptionHandlers_ShouldReturnCorrectHttpStatus() {
        // Given
        String errorMessage = "Test error";

        // When & Then - Проверяем все обработчики
        ExceptionResponse response1 = errorHandler.handleNotFound(new NotFoundException(errorMessage));
        assertNotNull(response1);

        ExceptionResponse response2 = errorHandler.handlerValidationException(new ValidationException(errorMessage));
        assertNotNull(response2);

        ExceptionResponse response3 = errorHandler.handlerDuplicationDataException(new DuplicatedDataException(errorMessage));
        assertNotNull(response3);

        ExceptionResponse response4 = errorHandler.handlerException(new AccessRightException(errorMessage));
        assertNotNull(response4);

        ExceptionResponse response5 = errorHandler.handlerException(new ValidationDataException(errorMessage));
        assertNotNull(response5);

        ExceptionResponse response6 = errorHandler.handlerException(new NoAccess(errorMessage));
        assertNotNull(response6);

        ExceptionResponse response7 = errorHandler.handlerException(new NotCompletedBooking(errorMessage));
        assertNotNull(response7);
    }

    // Дополнительный тест для проверки структуры ExceptionResponse
    @Test
    void exceptionResponse_ShouldHaveCorrectStructure() {
        // Given
        String errorMessage = "Test message";
        ExceptionResponse response = new ExceptionResponse(errorMessage);

        // When & Then
        assertEquals(errorMessage, response.getMessage());
        assertEquals("ExceptionResponse(message=Test message)", response.toString());
    }
}